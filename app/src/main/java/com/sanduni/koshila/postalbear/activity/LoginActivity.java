package com.sanduni.koshila.postalbear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.util.Common.ValidateResponse;
import com.sanduni.koshila.postalbear.util.Loader;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.executor.QueryExecutor;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sanduni.koshila.postalbear.util.Common.md5;
import static com.sanduni.koshila.postalbear.util.Common.setLoggedInUser;

public class LoginActivity extends AppCompatActivity implements DBCallbackHandler {

    private PostalBearDBHelper postalBearDBHelper;
    private int backBtnPressedCount = 0;
    private CountDownTimer timer;
    private EditText nicEditText;
    private EditText passwordEditText;
    private Loader loader;
    private CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Register Database
        postalBearDBHelper = new PostalBearDBHelper(getApplicationContext());

        loader = new Loader(this, "Login...");
        nicEditText = findViewById(R.id.nic);
        passwordEditText = findViewById(R.id.password);
        rememberMeCheckBox = findViewById(R.id.rememberMe);
        TextView to_register_TextView = findViewById(R.id.to_register);
        to_register_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        Button loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate inputs
                login();
            }
        });

        final TextView showPasswordIcon = findViewById(R.id.show_hide_password_icon);
        showPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordEditText.getInputType() == InputType.TYPE_CLASS_TEXT){
                    showPasswordIcon.setBackground(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
                    passwordEditText.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{
                    showPasswordIcon.setBackground(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                passwordEditText.requestFocus();
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });
    }

    @Override
    public void onBackPressed() {
        backBtnPressedCount++;
        if (timer == null) {
            Toast.makeText(getApplicationContext(), "Please press BACK again to exit",Toast.LENGTH_SHORT).show();
            timer = new CountDownTimer(2000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // nothing to do;
                }

                public void onFinish() {
                    backBtnPressedCount = 0;
                    timer = null;
                }
            };
            timer.start();
        }
        else {
            if (backBtnPressedCount == 2) {
                finishAffinity();
                System.exit(0);
            }
        }
    }

    private ValidateResponse validate() {
        boolean result = true;
        String message = "Fill all the fields to continue";
        String nic = nicEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (nic.equalsIgnoreCase("")) {
            result = false;
        }
        if (result && (password.equalsIgnoreCase(""))) {
            result = false;
        }
        return new ValidateResponse(result, message);
    }

    public void login() {
        loader.show();
        final ValidateResponse validateResponse = validate();
        if (!validateResponse.result) {
            loader.dismiss(new Loader.LoaderListener() {
                @Override
                public void loaderDismissed() {
                    Toast.makeText(getApplicationContext(), validateResponse.message, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            String nic = nicEditText.getText().toString().toUpperCase();
            new QueryExecutor(this,
                    PostalBearDBContract.User.TABLE_NAME,
                    new String[]{PostalBearDBContract.User.COLUMN_NAME_PASSWORD},
                    PostalBearDBContract.User.COLUMN_NAME_NIC + " = ?",
                    new String[]{nic}, null, postalBearDBHelper);
        }
    }



    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if (rememberMeCheckBox.isSelected()) {

            }
            else {

            }
            String str = (String) message.obj;
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(str);
            builder.setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };

    private void showToast(String str) {
        Message message = Message.obtain();
        message.obj = str;
        handler.sendMessage(message);
    }

    @Override
    public void onSuccess(int actionId, ArrayList<HashMap<String, String>> res) {
        String enteredPassword = md5(passwordEditText.getText().toString());
        Log.i("##### Password #####", enteredPassword);
        if (res != null && res.size() > 0) {
            String dbPassword = res.get(0).get(PostalBearDBContract.User.COLUMN_NAME_PASSWORD);
            if (dbPassword != null) {
                Log.i("#####|Password|#####", dbPassword);
                if (dbPassword.equals(enteredPassword)) {
                    String nic = nicEditText.getText().toString().toUpperCase();
                    DBCallbackHandler dbCallbackHandler = new DBCallbackHandler() {
                        @Override
                        public void onSuccess(int actionId, final ArrayList<HashMap<String, String>> res) {
                            loader.dismiss(new Loader.LoaderListener() {
                                @Override
                                public void loaderDismissed() {
                                    setLoggedInUser(getApplicationContext(), res.get(0));
                                    Intent dashboardIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                                    startActivity(dashboardIntent);
                                }
                            });
                        }

                        @Override
                        public void onSuccess(int actionId, int res) {

                        }

                        @Override
                        public void onFailed(int failureId) {
                            loader.dismiss(new Loader.LoaderListener() {
                                @Override
                                public void loaderDismissed() {
                                    showToast("Failed to login. Please try again.");
                                }
                            });
                        }
                    };
                    new QueryExecutor(dbCallbackHandler,
                            PostalBearDBContract.User.TABLE_NAME,
                            new String[]{PostalBearDBContract.User.COLUMN_NAME_FIRSTNAME, PostalBearDBContract.User.COLUMN_NAME_LASTNAME, PostalBearDBContract.User.COLUMN_NAME_NIC,
                                    PostalBearDBContract.User.COLUMN_NAME_ADDRESS, PostalBearDBContract.User.COLUMN_NAME_USERROLE, PostalBearDBContract.User.COLUMN_NAME_PASSWORD},
                            PostalBearDBContract.User.COLUMN_NAME_NIC + " = ?",
                            new String[]{nic}, null, postalBearDBHelper);
                }
                else {
                    loader.dismiss(new Loader.LoaderListener() {
                        @Override
                        public void loaderDismissed() {
                            showToast("Incorrect Password");
                        }
                    });
                }
            }
        }
        else {
            loader.dismiss(new Loader.LoaderListener() {
                @Override
                public void loaderDismissed() {
                    showToast("Incorrect NIC");
                }
            });
        }
    }

    @Override
    public void onSuccess(int actionId, int res) {

    }

    @Override
    public void onFailed(int failureId) {

    }
}