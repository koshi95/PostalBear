package com.sanduni.koshila.postalbear.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.util.Common.ValidateResponse;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract.PostOffice;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.executor.InsertExecutor;
import com.sanduni.koshila.postalbear.util.executor.QueryExecutor;
import com.sanduni.koshila.postalbear.util.executor.UpdateExecutor;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sanduni.koshila.postalbear.util.Common.ALREADY_EXISTS;
import static com.sanduni.koshila.postalbear.util.Common.SUCCESS;

public class AddEditViewPostOfficeActivity extends AppCompatActivity implements DBCallbackHandler {

    private PostalBearDBHelper postalBearDBHelper;

    private String ACTION;
    private TextView add_edit_post_office_label;
    private Button add_edit_btn;
    private EditText nameEditText;
    private EditText districtEditText;
    private EditText telephoneEditText;
    private EditText longitudeEditText;
    private EditText latitudeEditText;
    private String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_view_post_office);

        // Register Database
        postalBearDBHelper = new PostalBearDBHelper(getApplicationContext());

        // Initialize UI Elements
        add_edit_post_office_label = findViewById(R.id.add_edit_post_office_label);
        add_edit_btn = findViewById(R.id.add_edit_btn);
        nameEditText = findViewById(R.id.post_office_name);
        districtEditText = findViewById(R.id.post_office_district);
        telephoneEditText = findViewById(R.id.post_office_telephone);
        longitudeEditText = findViewById(R.id.longitude);
        latitudeEditText = findViewById(R.id.latitude);

        add_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditPostOffice();
            }
        });
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            ACTION = currentIntent.getStringExtra("ACTION");
            if (ACTION != null && ACTION.equals("ADD")) {
                add_edit_post_office_label.setText(R.string.add_post_office_label);
                add_edit_btn.setText(ACTION);
                formattingTelephoneNumber();
            }
            else if (ACTION != null && ACTION.equals("EDIT")) {
                add_edit_post_office_label.setText(R.string.edit_post_office);
                add_edit_btn.setText(ACTION);
                formattingTelephoneNumber();

                final String postOfficeName = currentIntent.getStringExtra("POST_OFFICE_NAME");
                retrievePostOfficeData(postOfficeName);
            }
            else if (ACTION != null && ACTION.equals("VIEW")) {
                add_edit_post_office_label.setText(R.string.view_post_office);
                add_edit_btn.setVisibility(View.GONE);

                // Disable EditTexts
                nameEditText.setEnabled(false);
                districtEditText.setEnabled(false);
                telephoneEditText.setEnabled(false);
                longitudeEditText.setEnabled(false);
                latitudeEditText.setEnabled(false);

                final String postOfficeName = currentIntent.getStringExtra("POST_OFFICE_NAME");
                retrievePostOfficeData(postOfficeName);
            }
        }
    }

    // Use this only in Edit and Add
    private void formattingTelephoneNumber() {
        telephoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    if (!editable.toString().isEmpty()) {
                        String str = editable.toString();
                        if (str.length() >=3 && !str.contains("-")) {
                            String areaCode = str.substring(0,3);
                            String number = str.substring(3);
                            String tel = String.format("%s-%s", areaCode, number);
                            int textLength = tel.length();
                            if (textLength >= 10) {
                                tel = tel.substring(0, 11);
                            }
                            telephoneEditText.setText(tel);
                            telephoneEditText.setSelection(tel.length());
                        }
                    }
                }
            }
        });
    }

    private void retrievePostOfficeData(String postOfficeName) {
        new QueryExecutor(new DBCallbackHandler() {
            @Override
            public void onSuccess(int actionId, ArrayList<HashMap<String, String>> res) {
                if (res != null && res.size() > 0) {
                    HashMap<String, String> postOffice = res.get(0);

                    // Setting values
                    ID = postOffice.get(PostOffice._ID);
                    nameEditText.setText(postOffice.get(PostOffice.COLUMN_NAME_NAME));
                    districtEditText.setText(postOffice.get(PostOffice.COLUMN_NAME_DISTRICT));
                    telephoneEditText.setText(postOffice.get(PostOffice.COLUMN_NAME_TELEPHONE));
                    longitudeEditText.setText(postOffice.get(PostOffice.COLUMN_NAME_LONGITUDE));
                    latitudeEditText.setText(postOffice.get(PostOffice.COLUMN_NAME_LATITUDE));
                }
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
                        PostOffice.COLUMN_NAME_DISTRICT,
                        PostOffice.COLUMN_NAME_TELEPHONE,
                        PostOffice.COLUMN_NAME_LONGITUDE,
                        PostOffice.COLUMN_NAME_LATITUDE},
                PostOffice.COLUMN_NAME_NAME + " == ?" ,
                new String[]{postOfficeName}, null, postalBearDBHelper);
    }


    private ValidateResponse validate() {
        boolean result = true;
        String message = "Fill all the fields to continue";
        String name = nameEditText.getText().toString();
        String district = districtEditText.getText().toString();
        String telephone = telephoneEditText.getText().toString();
        String longitude = longitudeEditText.getText().toString();
        String latitude = latitudeEditText.getText().toString();

        if (name.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && district.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && telephone.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && longitude.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && latitude.equalsIgnoreCase("")) {
            result = false;
        }
        return new ValidateResponse(result, message);
    }

    private void addEditPostOffice() {
        ValidateResponse validateResponse = validate();
        if (!validateResponse.result) {
            Toast.makeText(getApplicationContext(), validateResponse.message, Toast.LENGTH_SHORT).show();
        }
        else {
            String name = nameEditText.getText().toString();
            String district = districtEditText.getText().toString();
            String telephone = telephoneEditText.getText().toString();
            String longitude = longitudeEditText.getText().toString();
            String latitude = latitudeEditText.getText().toString();
            ContentValues values = new ContentValues();
            values.put(PostOffice.COLUMN_NAME_NAME, name.trim().toUpperCase());
            values.put(PostOffice.COLUMN_NAME_DISTRICT, district.trim().toUpperCase());
            values.put(PostOffice.COLUMN_NAME_TELEPHONE, telephone);
            values.put(PostOffice.COLUMN_NAME_LONGITUDE, longitude);
            values.put(PostOffice.COLUMN_NAME_LATITUDE, latitude);

            if (ACTION.equals("ADD")) {
                new InsertExecutor(this, PostOffice.TABLE_NAME, values, postalBearDBHelper);
            }
            else {
                new UpdateExecutor(this, PostOffice.TABLE_NAME, values, PostOffice._ID + " == ?", new String[]{ID}, postalBearDBHelper);
            }
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String[] strArr = (String[]) message.obj;
            AlertDialog.Builder builder = new AlertDialog.Builder(AddEditViewPostOfficeActivity.this);
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
}