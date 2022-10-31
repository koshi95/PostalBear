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

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.adapter.PostOfficeAdapter;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.executor.QueryExecutor;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sanduni.koshila.postalbear.util.Common.ADMIN_USER_ROLE;
import static com.sanduni.koshila.postalbear.util.Common.getLoggedInUser;
import static com.sanduni.koshila.postalbear.util.Common.setLoggedInUser;

public class PostOfficesActivity extends AppCompatActivity implements DBCallbackHandler {

    private PostalBearDBHelper postalBearDBHelper;
    private RecyclerView post_office_rv;
    private AppCompatActivity activity;
    private Context context;
    private HashMap<String, String> loggedInUser;
    String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_offices);

        // Register Database
        postalBearDBHelper = new PostalBearDBHelper(getApplicationContext());

        loggedInUser = getLoggedInUser(getApplicationContext());
        if (loggedInUser == null) {
            logout();
        }
        else {
            userRole = loggedInUser.get(PostalBearDBContract.User.COLUMN_NAME_USERROLE);
        }
        post_office_rv = findViewById(R.id.post_offices_rv);
        retrievePostOffices();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void retrievePostOffices() {
        activity = this;
        context = getApplicationContext();
        new QueryExecutor(this,
                PostalBearDBContract.PostOffice.TABLE_NAME,
                new String[]{PostalBearDBContract.PostOffice.COLUMN_NAME_NAME,
                        PostalBearDBContract.PostOffice.COLUMN_NAME_DISTRICT,
                        PostalBearDBContract.PostOffice.COLUMN_NAME_TELEPHONE,
                        PostalBearDBContract.PostOffice.COLUMN_NAME_LONGITUDE,
                        PostalBearDBContract.PostOffice.COLUMN_NAME_LATITUDE},
                "1",
                null, null, postalBearDBHelper);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            ArrayList<HashMap<String, String>> res = (ArrayList<HashMap<String, String>>) message.obj;
            if (res != null && res.size() > 0) {
                boolean allowedToEdit = false;
                if (userRole.equals(ADMIN_USER_ROLE)) {
                    allowedToEdit = true;
                }
                PostOfficeAdapter postOfficeAdapter = new PostOfficeAdapter(activity, res, allowedToEdit);
                post_office_rv.setAdapter(postOfficeAdapter);

                // Set LayoutManager of the RecyclerView
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                post_office_rv.setLayoutManager(linearLayoutManager);

                // Add a VerticalDivider as an ItemDecoration
                DividerItemDecoration dividerItemDecoration;
                dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                post_office_rv.addItemDecoration(dividerItemDecoration);
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