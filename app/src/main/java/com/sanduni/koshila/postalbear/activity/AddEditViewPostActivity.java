package com.sanduni.koshila.postalbear.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.util.Common.ValidateResponse;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract.Post;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract.PostOffice;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;
import com.sanduni.koshila.postalbear.util.QRCodeGenerator;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.executor.InsertExecutor;
import com.sanduni.koshila.postalbear.util.executor.QueryExecutor;
import com.sanduni.koshila.postalbear.util.executor.UpdateExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.sanduni.koshila.postalbear.util.Common.ALREADY_EXISTS;
import static com.sanduni.koshila.postalbear.util.Common.SUCCESS;
import static com.sanduni.koshila.postalbear.util.Common.getLoggedInUser;
import static com.sanduni.koshila.postalbear.util.Common.setLoggedInUser;

public class AddEditViewPostActivity extends AppCompatActivity implements DBCallbackHandler {

    private PostalBearDBHelper postalBearDBHelper;

    private Intent currentIntent;
    private String ACTION;
    private TextView add_edit_post_label;
    private Button add_edit_btn;
    private EditText referenceNumberEditText;
    private EditText sendDateEditText;
    private EditText chargesEditText;
    private EditText senderNICEditText;
    private EditText senderAddressEditText;
    private EditText receiverAddressEditText;
    private EditText distributorNICEditText;
    private Spinner lastPostOfficeSpinner;
    private Spinner nextPostOfficeSpinner;
    private CheckBox postDeliveredCheckBox;
    private EditText lastPostOfficeSpinnerSupportEditText;
    private EditText nextPostOfficeSpinnerSupportEditText;

    private ArrayList<String> postOfficeIdList;
    private ArrayList<String> postOfficeList;

    private String qrStr;

    private HashMap<String, String> loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_view_post);

        // Register Database
        postalBearDBHelper = new PostalBearDBHelper(getApplicationContext());

        loggedInUser = getLoggedInUser(getApplicationContext());
        if (loggedInUser == null) {
            logout();
        }

        // Initialize UI Elements
        add_edit_post_label = findViewById(R.id.add_edit_post_label);
        add_edit_btn = findViewById(R.id.add_edit_btn);
        referenceNumberEditText = findViewById(R.id.reference_number);
        sendDateEditText = findViewById(R.id.send_date);
        chargesEditText = findViewById(R.id.charges);
        senderNICEditText = findViewById(R.id.sender_nic);
        senderAddressEditText = findViewById(R.id.sender_address);
        receiverAddressEditText = findViewById(R.id.receiver_address);
        distributorNICEditText = findViewById(R.id.distributor_nic);;
        lastPostOfficeSpinner = findViewById(R.id.last_post_office_spinner);
        nextPostOfficeSpinner = findViewById(R.id.next_post_office_spinner);
        postDeliveredCheckBox = findViewById(R.id.post_delivered);
        lastPostOfficeSpinnerSupportEditText = findViewById(R.id.last_post_office_spinner_support);
        nextPostOfficeSpinnerSupportEditText = findViewById(R.id.next_post_office_spinner_support);

        add_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditPost();
            }
        });
        getPostOffices();
        currentIntent = getIntent();
        if (currentIntent != null) {
            ACTION = currentIntent.getStringExtra("ACTION");
            if (ACTION != null && ACTION.equals("ADD")) {
                String refNumber = UUID.randomUUID().toString();
                referenceNumberEditText.setText(refNumber);
            }
            else if (ACTION != null && ACTION.equals("PARTIAL_EDIT")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEditViewPostActivity.this);
                builder.setTitle("Warning!!!");
                builder.setMessage("You can edit this if only if you are the distributor. Continue in Edit Mode?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // nothing to do
                        handleUIElements();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ACTION = "VIEW";
                        handleUIElements();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    private void handleUIElements() {
        referenceNumberEditText.setEnabled(false);
        if (ACTION.equals("ADD")) {
            add_edit_post_label.setText(R.string.add_post_label);
            add_edit_btn.setText(ACTION);
        }
        else if (ACTION.equals("EDIT")) {
            add_edit_post_label.setText(R.string.edit_post_label);
            add_edit_btn.setText(ACTION);
        }
        else if(ACTION.equals("PARTIAL_EDIT")) {
            add_edit_post_label.setText(R.string.edit_post_label);
            add_edit_btn.setText(R.string.edit_btn_label);
            distributorNICEditText.setText(loggedInUser.get(PostalBearDBContract.User.COLUMN_NAME_NIC));
            postDeliveredCheckBox.setVisibility(View.VISIBLE);

            // Disable EditTexts
            sendDateEditText.setEnabled(false);
            chargesEditText.setEnabled(false);
            senderNICEditText.setEnabled(false);
            senderAddressEditText.setEnabled(false);
            receiverAddressEditText.setEnabled(false);
            distributorNICEditText.setEnabled(false);
            lastPostOfficeSpinner.setEnabled(false);
        }
        else if(ACTION.equals("VIEW")) {
            add_edit_post_label.setText(R.string.view_post_label);
            add_edit_btn.setVisibility(View.GONE);

            sendDateEditText.setEnabled(false);
            chargesEditText.setEnabled(false);
            senderNICEditText.setEnabled(false);
            senderAddressEditText.setEnabled(false);
            receiverAddressEditText.setEnabled(false);
            distributorNICEditText.setEnabled(false);
            lastPostOfficeSpinner.setVisibility(View.GONE);
            nextPostOfficeSpinner.setVisibility(View.GONE);

            postDeliveredCheckBox.setEnabled(false);
            postDeliveredCheckBox.setVisibility(View.VISIBLE);
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
                    String str = entry.get(PostOffice.COLUMN_NAME_DISTRICT) + " - " + entry.get(PostOffice.COLUMN_NAME_NAME);
                    postOfficeList.add(str);
                    postOfficeIdList.add(entry.get(PostOffice._ID));
                }
                // Create Spinner Dropdown
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddEditViewPostActivity.this, android.R.layout.simple_spinner_item, postOfficeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lastPostOfficeSpinner.setAdapter(adapter);
                nextPostOfficeSpinner.setAdapter(adapter);
                if (!ACTION.equals("ADD")) {
                    final String postReferenceNumber = currentIntent.getStringExtra("POST_REFERENCE_NUMBER");
                    retrievePostData(postReferenceNumber);
                }
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
        PostOffice.TABLE_NAME,
        new String[]{
                PostOffice._ID,
                PostOffice.COLUMN_NAME_NAME,
                PostOffice.COLUMN_NAME_DISTRICT
        },
        "1", null, null, postalBearDBHelper);
    }

    private Handler postHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            ArrayList<HashMap<String, String>> res = (ArrayList<HashMap<String, String>>) message.obj;

            if (res != null && res.size() > 0) {
                HashMap<String, String> post = res.get(0);

                // Setting values
                referenceNumberEditText.setText(post.get(Post.COLUMN_NAME_REFERENCE_NUMBER));
                sendDateEditText.setText(post.get(Post.COLUMN_NAME_SEND_DATE));
                chargesEditText.setText(post.get(Post.COLUMN_NAME_CHARGES));
                senderNICEditText.setText(post.get(Post.COLUMN_NAME_SENDER_NIC));
                senderAddressEditText.setText(post.get(Post.COLUMN_NAME_SENDER_ADDRESS));
                receiverAddressEditText.setText(post.get(Post.COLUMN_NAME_RECEIVER_ADDRESS));
                distributorNICEditText.setText(post.get(Post.COLUMN_NAME_DISTRIBUTOR_NIC));
                int lastPostOfficeIndex = postOfficeIdList.indexOf(post.get(Post.COLUMN_NAME_LAST_POST_OFFICE_ID));
                int nextPostOfficeIndex = postOfficeIdList.indexOf(post.get(Post.COLUMN_NAME_NEXT_POST_OFFICE_ID));
                if (ACTION.equals("VIEW")) {
                    if (lastPostOfficeIndex != -1) {
                        lastPostOfficeSpinnerSupportEditText.setText(postOfficeList.get(lastPostOfficeIndex));
                    }
                    if (nextPostOfficeIndex != -1) {
                        nextPostOfficeSpinnerSupportEditText.setText(postOfficeList.get(nextPostOfficeIndex));
                    }
                }
                lastPostOfficeSpinner.setSelection(lastPostOfficeIndex);
                nextPostOfficeSpinner.setSelection(nextPostOfficeIndex);
                boolean delivered = false;
                String deliveredText = post.get(Post.COLUMN_NAME_DELIVERED);
                if (deliveredText != null) {
                    if (deliveredText.equals("1")) {
                        delivered = true;
                    }
                }
                postDeliveredCheckBox.setChecked(delivered);;
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


    private ValidateResponse validate() {
        boolean result = true;
        String message = "Fill all the fields to continue";
        String referenceNumber = referenceNumberEditText.getText().toString();
        String sendDate = sendDateEditText.getText().toString();
        String charges = chargesEditText.getText().toString();
        String senderNIC = senderNICEditText.getText().toString();
        String senderAddress = senderAddressEditText.getText().toString();
        String receiverAddress = receiverAddressEditText.getText().toString();
        String distributorNIC = distributorNICEditText.getText().toString();

        if (referenceNumber.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && sendDate.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && charges.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && senderNIC.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && senderAddress.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && receiverAddress.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && distributorNIC.equalsIgnoreCase("")) {
            result = false;
        }
//        if (result && !lastPostOfficeSpinner.isSelected()) {
//            message = "Please select the last post office as your post office";
//            result = false;
//        }
        if (result && lastPostOfficeSpinner.getSelectedItem().toString().equals("---")) {
            message = "Please select the last post office as your post office";
            result = false;
        }
        return new ValidateResponse(result, message);
    }

    private void addEditPost() {
        ValidateResponse validateResponse = validate();
        if (!validateResponse.result) {
            Toast.makeText(getApplicationContext(), validateResponse.message, Toast.LENGTH_SHORT).show();
        }
        else {
            String referenceNumber = referenceNumberEditText.getText().toString();
            String sendDate = sendDateEditText.getText().toString();
            String charges = chargesEditText.getText().toString();
            String senderNIC = senderNICEditText.getText().toString();
            String senderAddress = senderAddressEditText.getText().toString();
            String receiverAddress = receiverAddressEditText.getText().toString();
            String distributorNIC = distributorNICEditText.getText().toString();
            int lastPostOfficeSelect = lastPostOfficeSpinner.getSelectedItemPosition();
            int nextPostOfficeSelect = nextPostOfficeSpinner.getSelectedItemPosition();
            String lastPostOffice, nextPostOffice;
            if (lastPostOfficeSelect == 0) {
                String message = "Please select the last post office as your post office";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                lastPostOffice = postOfficeIdList.get(lastPostOfficeSelect);
            }
            if (nextPostOfficeSelect == 0) {
                nextPostOffice = "";
            }
            else {
                nextPostOffice = postOfficeIdList.get(nextPostOfficeSelect);
            }

            ContentValues values = new ContentValues();
            values.put(Post.COLUMN_NAME_SEND_DATE, sendDate);
            values.put(Post.COLUMN_NAME_CHARGES, charges);
            values.put(Post.COLUMN_NAME_SENDER_NIC, senderNIC.toUpperCase());
            values.put(Post.COLUMN_NAME_SENDER_ADDRESS, senderAddress);
            values.put(Post.COLUMN_NAME_RECEIVER_ADDRESS, receiverAddress);
            values.put(Post.COLUMN_NAME_DISTRIBUTOR_NIC, distributorNIC.toUpperCase());
            values.put(Post.COLUMN_NAME_LAST_POST_OFFICE_ID, lastPostOffice);
            values.put(Post.COLUMN_NAME_NEXT_POST_OFFICE_ID, nextPostOffice);
            if (ACTION.equals("ADD")) {
                values.put(Post.COLUMN_NAME_DELIVERED, "0");
                values.put(Post.COLUMN_NAME_REFERENCE_NUMBER, referenceNumber);
                new InsertExecutor(this, Post.TABLE_NAME, values, postalBearDBHelper);
            }
            else {
                if (ACTION.equals("PARTIAL_EDIT")) {
                    values.put(Post.COLUMN_NAME_DISTRIBUTOR_NIC, loggedInUser.get(PostalBearDBContract.User.COLUMN_NAME_NIC));
                    boolean delivered = postDeliveredCheckBox.isChecked();
                    if (delivered) {
                        values.put(Post.COLUMN_NAME_DELIVERED, "1");
                    }
                }
                new UpdateExecutor(this, Post.TABLE_NAME, values, Post.COLUMN_NAME_REFERENCE_NUMBER + " == ?", new String[]{referenceNumber}, postalBearDBHelper);
            }
            qrStr = referenceNumber;
            requestStorage();
        }
    }



    private void requestStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(getApplicationContext(), qrStr);
            if (qrCodeGenerator.generateQR()) {
                return;
            }
            Toast.makeText(getApplicationContext(), "QR is not saved", Toast.LENGTH_LONG).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(AddEditViewPostActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(getApplicationContext(), qrStr);
                if (qrCodeGenerator.generateQR()) {
                    return;
                }
                Toast.makeText(getApplicationContext(), "QR is not saved", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String[] strArr = (String[]) message.obj;
            AlertDialog.Builder builder = new AlertDialog.Builder(AddEditViewPostActivity.this);
            builder.setTitle(strArr[0]);
            builder.setMessage(strArr[1]);
            if (strArr[0].equals(SUCCESS)) {
                builder.setCancelable(false);
                builder.setPositiveButton("Go to Dashboard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        onBackPressed();
                    }
                });
            }
            else {
                builder.setCancelable(true);
            }
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };

    @Override
    public void onSuccess(int actionId, ArrayList<HashMap<String, String>> res) {

    }

    @Override
    public void onSuccess(int actionId, int res) {
        Message message = Message.obtain();
        if (actionId == 0) {
            message.obj = new String[]{"Success", "Successfully added"};
        }
        else {
            message.obj = new String[]{"Success", "Successfully edited"};
        }
        handler.sendMessage(message);
    }

    @Override
    public void onFailed(int failureId) {
        Message message = Message.obtain();
        if (failureId == ALREADY_EXISTS) {
            message.obj = new String[]{"Failed", "This post office name already exists"};
        }
        else {
            message.obj = new String[]{"Failed", "Failed to add the post office"};
        }
        handler.sendMessage(message);
    }

    private void logout() {
        setLoggedInUser(getApplicationContext(), null);
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }
}