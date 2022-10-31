package com.sanduni.koshila.postalbear.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ArrayAdapter;

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.adapter.PostAdapter;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.executor.QueryExecutor;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sanduni.koshila.postalbear.util.Common.ADMIN_USER_ROLE;
import static com.sanduni.koshila.postalbear.util.Common.EDIT;
import static com.sanduni.koshila.postalbear.util.Common.PARTIAL_EDIT;
import static com.sanduni.koshila.postalbear.util.Common.POSTMAN_USER_ROLE;
import static com.sanduni.koshila.postalbear.util.Common.getLoggedInUser;
import static com.sanduni.koshila.postalbear.util.Common.setLoggedInUser;

public class PostsActivity extends AppCompatActivity implements DBCallbackHandler {

    private PostalBearDBHelper postalBearDBHelper;
    private RecyclerView post_rv;
    private AppCompatActivity activity;
    private Context context;
    private HashMap<String, String> loggedInUser;
    String userRole;

    private ArrayList<String> postOfficeIdList;
    private ArrayList<String> postOfficeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        // Register Database
        postalBearDBHelper = new PostalBearDBHelper(getApplicationContext());

        loggedInUser = getLoggedInUser(getApplicationContext());
        if (loggedInUser == null) {
            logout();
        }
        else {
            userRole = loggedInUser.get(PostalBearDBContract.User.COLUMN_NAME_USERROLE);
        }
        post_rv = findViewById(R.id.posts_rv);

        getPostOffices();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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
                retrievePendingPosts();
            }
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

    private void retrievePendingPosts() {
        activity = this;
        context = getApplicationContext();
        new QueryExecutor(this,
                PostalBearDBContract.Post.TABLE_NAME,
                new String[]{PostalBearDBContract.Post.COLUMN_NAME_REFERENCE_NUMBER,
                        PostalBearDBContract.Post.COLUMN_NAME_SENDER_NIC,
                        PostalBearDBContract.Post.COLUMN_NAME_SENDER_ADDRESS,
                        PostalBearDBContract.Post.COLUMN_NAME_RECEIVER_ADDRESS,
                        PostalBearDBContract.Post.COLUMN_NAME_DISTRIBUTOR_NIC,
                        PostalBearDBContract.Post.COLUMN_NAME_LAST_POST_OFFICE_ID,
                        PostalBearDBContract.Post.COLUMN_NAME_NEXT_POST_OFFICE_ID},
                PostalBearDBContract.Post.COLUMN_NAME_DELIVERED + " != ?",
                new String[]{"1"}, null, postalBearDBHelper);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            ArrayList<HashMap<String, String>> res = (ArrayList<HashMap<String, String>>) message.obj;
            if (res != null && res.size() > 0) {
                int allowedToEdit = 0;
                if (userRole.equals(ADMIN_USER_ROLE)) {
                    allowedToEdit = EDIT;
                }
                else if (userRole.equals(POSTMAN_USER_ROLE)) {
                    allowedToEdit = PARTIAL_EDIT;
                }
                PostAdapter postAdapter = new PostAdapter(activity, res, allowedToEdit);
                postAdapter.setPostOfficeArrays(postOfficeIdList, postOfficeList);
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
        }
    };

    @Override
    public void onSuccess(int actionId, ArrayList<HashMap<String, String>> res) {
        Message message = Message.obtain();
        message.obj = res;
        handler.sendMessage(message);
    }

    @Override
    public void onSuccess(int actionId, int res) {
    }

    @Override
    public void onFailed(int failureId) {

    }

    private void logout() {
        setLoggedInUser(getApplicationContext(), null);
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }
}