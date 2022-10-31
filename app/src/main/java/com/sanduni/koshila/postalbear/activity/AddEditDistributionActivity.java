package com.sanduni.koshila.postalbear.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Size;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.adapter.PostAdapter;
import com.sanduni.koshila.postalbear.util.AdapterListener;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract.Post;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;
import com.sanduni.koshila.postalbear.util.QRCodeFoundListener;
import com.sanduni.koshila.postalbear.util.QRCodeImageAnalyzer;
import com.google.common.util.concurrent.ListenableFuture;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.executor.BulkUpdateExecutor;
import com.sanduni.koshila.postalbear.util.executor.QueryExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.sanduni.koshila.postalbear.util.Common.getLoggedInUser;
import static com.sanduni.koshila.postalbear.util.Common.setLoggedInUser;

public class AddEditDistributionActivity extends AppCompatActivity {

    private HashMap<String, String> loggedInUser;

    private PostalBearDBHelper postalBearDBHelper;
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageAnalysis imageAnalysis;
    private boolean cameraPaused = true;
    private RecyclerView post_rv;
    private AppCompatActivity activity;
    private Context context;
    private String qrCode;

    private ArrayList<String> postOfficeIdList;
    private ArrayList<String> postOfficeList;
    private PostAdapter postAdapter;
    private Button acceptPost;
    private Button assignPosts;
    private LinearLayout distributionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_distribution);

        // Register Database
        postalBearDBHelper = new PostalBearDBHelper(getApplicationContext());

        loggedInUser = getLoggedInUser(getApplicationContext());
        if (loggedInUser == null) {
            logout();
        }

        previewView = findViewById(R.id.activity_main_previewView);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();

        getPostOffices();
        post_rv = findViewById(R.id.distribution_rv);
        activity = this;
        context = getApplicationContext();

        acceptPost = findViewById(R.id.accept_post_btn);
        assignPosts = findViewById(R.id.assign_post_btn);
        distributionView = findViewById(R.id.distribution_view);
        acceptPost.setEnabled(false);
        acceptPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                distributionView.setVisibility(View.GONE);
                previewView.setVisibility(View.VISIBLE);
                cameraPaused = false;
            }
        });
        assignPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddEditDistributionActivity.this);
                builder.setMessage("Do you really want to assign these posts to yourself?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // nothing to do
                        assignPosts();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // nothing to do
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraPaused) {
            cameraPaused = false;
        }
    }

    private AdapterListener adapterListener = new AdapterListener() {
        @Override
        public void dataSetChanged() {
            if (postAdapter != null) {
                int itemCount = postAdapter.getItemCount();
                if (itemCount > 0) {
                    assignPosts.setVisibility(View.VISIBLE);
                }
                else {
                    assignPosts.setVisibility(View.GONE);
                }
            }
        }
    };



    private Handler assignPostHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            onBackPressed();
            finish();
        }
    };

    private void assignPosts() {
        if (postAdapter.getItemCount() > 0) {
            ArrayList<HashMap<String, String>> selectedPosts = postAdapter.getPostList();
            ArrayList<ContentValues> valueList = new ArrayList<>();
            ArrayList<String[]> whereClauseArgsList = new ArrayList<>();
            String[] whereClauseArgs;
            ContentValues contentValues;
            for (HashMap<String, String> post: selectedPosts) {
                String distributor = post.get(Post.COLUMN_NAME_DISTRIBUTOR_NIC);
                if (distributor != null && (distributor.equals("") || distributor.equals("0"))) {
                    distributor = loggedInUser.get(PostalBearDBContract.User.COLUMN_NAME_NIC);
                }
                else {
                    if (distributor == null) distributor = "";
                    distributor += ", " + loggedInUser.get(PostalBearDBContract.User.COLUMN_NAME_NIC);
                }
                contentValues = new ContentValues();
                contentValues.put(Post.COLUMN_NAME_DISTRIBUTOR_NIC, distributor);
                valueList.add(contentValues);
                whereClauseArgs = new String[]{post.get(Post.COLUMN_NAME_REFERENCE_NUMBER)};
                whereClauseArgsList.add(whereClauseArgs);
            }
            new BulkUpdateExecutor(new DBCallbackHandler() {
                @Override
                public void onSuccess(int actionId, ArrayList<HashMap<String, String>> res) {

                }

                @Override
                public void onSuccess(int actionId, int res) {
                    Message message = Message.obtain();
                    assignPostHandler.sendMessage(message);
                }

                @Override
                public void onFailed(int failureId) {

                }
            },
            Post.TABLE_NAME,
            valueList,
            Post.COLUMN_NAME_REFERENCE_NUMBER + " == ?" ,whereClauseArgsList, postalBearDBHelper);
        }
    }

    private Handler postOfficeHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            ArrayList<HashMap<String, String>> res = (ArrayList<HashMap<String, String>>) message.obj;
            if (res != null && res.size() > 0) {
                postOfficeList = new ArrayList<String>();
                postOfficeIdList = new ArrayList<String>();
                postOfficeList.add("---");
                postOfficeIdList.add("---");
                for (HashMap<String, String> entry: res) {
                    String str = entry.get(PostalBearDBContract.PostOffice.COLUMN_NAME_DISTRICT) + " - " + entry.get(PostalBearDBContract.PostOffice.COLUMN_NAME_NAME);
                    postOfficeList.add(str);
                    postOfficeIdList.add(entry.get(PostalBearDBContract.PostOffice._ID));
                }
            }
            cameraPaused = false;
            acceptPost.setEnabled(true);
        }
    };

    private void getPostOffices() {
        new QueryExecutor(new DBCallbackHandler() {
            @Override
            public void onSuccess(int actionId, ArrayList<HashMap<String, String>> res) {
                Message message = Message.obtain();
                message.obj = res;
                postOfficeHandler.sendMessage(message);
            }

            @Override
            public void onSuccess(int actionId, int res) {

            }

            @Override
            public void onFailed(int failureId) {

            }
        },
        PostalBearDBContract.PostOffice.TABLE_NAME,
        new String[]{
                PostalBearDBContract.PostOffice._ID,
                PostalBearDBContract.PostOffice.COLUMN_NAME_NAME,
                PostalBearDBContract.PostOffice.COLUMN_NAME_DISTRICT
        },
        "1", null, null, postalBearDBHelper);
    }

    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(AddEditDistributionActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private QRCodeImageAnalyzer qrCodeImageAnalyzer =  new QRCodeImageAnalyzer(new QRCodeFoundListener() {
        @Override
        public void onQRCodeFound(String _qrCode) {
            if (!cameraPaused) {
                cameraPaused = true;
                distributionView.setVisibility(View.VISIBLE);
                previewView.setVisibility(View.GONE);
                qrCode = _qrCode;

                /* Add posts to the recycler view*/
                cameraPaused = true;
                retrievePostData(qrCode);
            }
        }

        @Override
        public void qrCodeNotFound() {
        }
    });

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), qrCodeImageAnalyzer);

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }

    private Handler postHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            ArrayList<HashMap<String, String>> res = (ArrayList<HashMap<String, String>>) message.obj;
            if (res != null && res.size() > 0) {
                HashMap<String, String> post = res.get(0);
                if (postAdapter == null) {
                    ArrayList<HashMap<String, String>> postList = new ArrayList<>();
                    postAdapter = new PostAdapter(activity, postList, 0, adapterListener);
                    postAdapter.setPostOfficeArrays(postOfficeIdList, postOfficeList);
                    postAdapter.setDistributionPostListStatus(true);
                    postAdapter.addPost(post);
                    post_rv.setAdapter(postAdapter);

                    // Set LayoutManager of the RecyclerView
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                    post_rv.setLayoutManager(linearLayoutManager);
                    post_rv.setHasFixedSize(true);

                    // Add a VerticalDivider as an ItemDecoration
                    DividerItemDecoration dividerItemDecoration;
                    dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                    post_rv.addItemDecoration(dividerItemDecoration);
                }
                else {
                    postAdapter.addPost(post);
                }
            }
        }
    };

    private void retrievePostData(String postReferenceNumber) {
        new QueryExecutor(new DBCallbackHandler() {
            @Override
            public void onSuccess(int actionId, ArrayList<HashMap<String, String>> res) {
                Message message = Message.obtain();
                message.obj = res;
                postHandler.sendMessage(message);
            }

            @Override
            public void onSuccess(int actionId, int res) {

            }

            @Override
            public void onFailed(int failureId) {

            }
        },
        Post.TABLE_NAME,
        new String[]{
                Post.COLUMN_NAME_REFERENCE_NUMBER,
                Post.COLUMN_NAME_SEND_DATE,
                Post.COLUMN_NAME_CHARGES,
                Post.COLUMN_NAME_DELIVERED,
                Post.COLUMN_NAME_SENDER_NIC,
                Post.COLUMN_NAME_SENDER_ADDRESS,
                Post.COLUMN_NAME_RECEIVER_ADDRESS,
                Post.COLUMN_NAME_DISTRIBUTOR_NIC,
                Post.COLUMN_NAME_LAST_POST_OFFICE_ID,
                Post.COLUMN_NAME_NEXT_POST_OFFICE_ID},
        Post.COLUMN_NAME_REFERENCE_NUMBER + " == ?" ,
        new String[]{postReferenceNumber}, null, postalBearDBHelper);
    }

    private void logout() {
        setLoggedInUser(getApplicationContext(), null);
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }
}