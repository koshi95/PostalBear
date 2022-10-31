package com.sanduni.koshila.postalbear.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.util.Common;
import com.sanduni.koshila.postalbear.util.Common.ValidateResponse;
import com.sanduni.koshila.postalbear.util.Loader;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.executor.InsertExecutor;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sanduni.koshila.postalbear.util.Common.ALREADY_EXISTS;
import static com.sanduni.koshila.postalbear.util.Common.SUCCESS;
import static com.sanduni.koshila.postalbear.util.Common.md5;

public class RegisterActivity extends AppCompatActivity implements DBCallbackHandler{

    private PostalBearDBHelper postalBearDBHelper;
    private EditText firstnameEditText;
    private EditText lastnameEditText;
    private EditText nicEditText;
    private EditText addressEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Spinner userRoleSpinner ;
    private Button registerBtn;
    private Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Register Database
        postalBearDBHelper = new PostalBearDBHelper(getApplicationContext());

        loader = new Loader(this, "Registering...");
        // Initialize UI Elements
        firstnameEditText = findViewById(R.id.firstname);
        lastnameEditText = findViewById(R.id.lastname);
        nicEditText = findViewById(R.id.nic);
        addressEditText = findViewById(R.id.address);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        userRoleSpinner = findViewById(R.id.user_role_spinner);
        registerBtn = findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        // Create Spinner Dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userRoleSpinner.setAdapter(adapter);
    }

    private ValidateResponse validate() {
        boolean result = true;
        String message = "Fill all the fields to continue";
        String firstname = firstnameEditText.getText().toString();
        String lastname = lastnameEditText.getText().toString();
        String nic = nicEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String userRole = userRoleSpinner.getSelectedItem().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (firstname.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && lastname.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && nic.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && address.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && userRole.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && (password.equalsIgnoreCase("") || (confirmPassword.equalsIgnoreCase("")))) {
            result = false;
        }
        else if (result && !password.equals(confirmPassword)) {
            result = false;
            message = "Passwords are not matching";
        }
        else if  (result && password.length() < 8) {
            result = false;
            message = "Passwords should contain at least 8 characters";
        }
        return new ValidateResponse(result, message);
    }

    private void register() {
        loader.show();
        ValidateResponse validateResponse = validate();
        if (!validateResponse.result) {
            loader.dismiss(new Loader.LoaderListener() {
                @Override
                public void loaderDismissed() {
                    Toast.makeText(getApplicationContext(), validateResponse.message, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            String firstname = firstnameEditText.getText().toString();
            String lastname = lastnameEditText.getText().toString();
            String nic = nicEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String userRole = userRoleSpinner.getSelectedItem().toString();
            String password = passwordEditText.getText().toString();
            String hashedPassword = md5(password);
            if (hashedPassword.equals("")) {
                loader.dismiss(new Loader.LoaderListener() {
                    @Override
                    public void loaderDismissed() {
                        Toast.makeText(getApplicationContext(), "Error occurred while saving the password", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            ContentValues values = new ContentValues();
            values.put(PostalBearDBContract.User.COLUMN_NAME_ADDRESS, address);
            values.put(PostalBearDBContract.User.COLUMN_NAME_FIRSTNAME, firstname);
            values.put(PostalBearDBContract.User.COLUMN_NAME_LASTNAME, lastname);
            values.put(PostalBearDBContract.User.COLUMN_NAME_NIC, nic.trim().toUpperCase());
            values.put(PostalBearDBContract.User.COLUMN_NAME_PASSWORD, hashedPassword);
            values.put(PostalBearDBContract.User.COLUMN_NAME_USERROLE, userRole);

            new InsertExecutor(this, PostalBearDBContract.User.TABLE_NAME, values, postalBearDBHelper);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String[] strArr = (String[]) message.obj;
            loader.dismiss(new Loader.LoaderListener() {
                @Override
                public void loaderDismissed() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle(strArr[0]);
                    builder.setMessage(strArr[1]);
                    builder.setCancelable(false);
                    if (strArr[0].equals(SUCCESS)) {
                        builder.setPositiveButton("Go to Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                onBackPressed();
                            }
                        });
                    }
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    };

    @Override
    public void onSuccess(int actionId, ArrayList<HashMap<String, String>> res) {

    }

    @Override
    public void onSuccess(int actionId, int res) {
        Message message = Message.obtain();
        message.obj = new String[]{"Success", "Successfully registered"};
        handler.sendMessage(message);
    }

    @Override
    public void onFailed(int failureId) {
        Message message = Message.obtain();
        if (failureId == ALREADY_EXISTS) {
            message.obj = new String[]{"Failed", "This nic is already used"};
        }
        else {
            message.obj = new String[]{"Failed", "Failed to register"};
        }
        handler.sendMessage(message);
    }
}