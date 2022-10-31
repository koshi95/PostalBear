package com.sanduni.koshila.postalbear.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.sanduni.koshila.postalbear.modal.DashBoardItemModal;
import com.sanduni.koshila.postalbear.adapter.DashboardAdapter;
import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sanduni.koshila.postalbear.util.Common.ADD_POST;
import static com.sanduni.koshila.postalbear.util.Common.ADD_POST_OFFICE;
import static com.sanduni.koshila.postalbear.util.Common.ADMIN_USER_ROLE;
import static com.sanduni.koshila.postalbear.util.Common.CREATE_DISTRIBUTION;
import static com.sanduni.koshila.postalbear.util.Common.POSTMAN_USER_ROLE;
import static com.sanduni.koshila.postalbear.util.Common.SEARCH_BY_QR_SCANNER;
import static com.sanduni.koshila.postalbear.util.Common.SEARCH_BY_REFERENCE_NUMBER;
import static com.sanduni.koshila.postalbear.util.Common.VIEW_HISTORY;
import static com.sanduni.koshila.postalbear.util.Common.VIEW_POSTS;
import static com.sanduni.koshila.postalbear.util.Common.VIEW_POST_OFFICES;
import static com.sanduni.koshila.postalbear.util.Common.VIEW_SEND_POSTS;
import static com.sanduni.koshila.postalbear.util.Common.getLoggedInUser;
import static com.sanduni.koshila.postalbear.util.Common.setLoggedInUser;

public class DashboardActivity extends AppCompatActivity {

    private HashMap<String, String> loggedInUser;
    private String userRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        loggedInUser = getLoggedInUser(getApplicationContext());
        if (loggedInUser == null) {
            logout();
        }
        else {
            userRole = loggedInUser.get(PostalBearDBContract.User.COLUMN_NAME_USERROLE);
        }

        GridView gridView = findViewById(R.id.dashboard_grid_view);

        ArrayList<DashBoardItemModal> dashboardItemArrayList = new ArrayList<DashBoardItemModal>();
        if (userRole.equals(ADMIN_USER_ROLE)) {
            dashboardItemArrayList.add(new DashBoardItemModal(ADD_POST, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(VIEW_POSTS, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(ADD_POST_OFFICE, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(VIEW_POST_OFFICES, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(VIEW_HISTORY, R.drawable.srilanka_post_logo_no_background));
        }
        else if (userRole.equals(POSTMAN_USER_ROLE)) {
            dashboardItemArrayList.add(new DashBoardItemModal(VIEW_POSTS, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(VIEW_POST_OFFICES, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(VIEW_HISTORY, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(CREATE_DISTRIBUTION, R.drawable.srilanka_post_logo_no_background));
        }
        else {
            dashboardItemArrayList.add(new DashBoardItemModal(SEARCH_BY_REFERENCE_NUMBER, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(SEARCH_BY_QR_SCANNER, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(VIEW_POST_OFFICES, R.drawable.srilanka_post_logo_no_background));
            dashboardItemArrayList.add(new DashBoardItemModal(VIEW_SEND_POSTS, R.drawable.srilanka_post_logo_no_background));
        }

        DashboardAdapter adapter = new DashboardAdapter(this, dashboardItemArrayList);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        OpenLogoutDialog();
    }

    private void OpenLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setMessage("Do you want to logout ?");
        builder.setTitle("Logout !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                logout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void logout() {
        setLoggedInUser(getApplicationContext(), null);
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }
}