package com.sanduni.koshila.postalbear.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Common {

    public static String app_package = "com.sanduni.koshila.postalbear";

    // Error Codes
    public static int ALREADY_EXISTS = -2;

    // Status
    public static String SUCCESS = "Success";
    public static String FAILED = "Failed";

    // Edit Access
    public static int EDIT = 1;
    public static int PARTIAL_EDIT = 2;
    public  static int VIEW = 0;

    // User Roles
    public static String ADMIN_USER_ROLE = "Admin";
    public static String POSTMAN_USER_ROLE = "Postman";
    public static String CUSTOMER_USER_ROLE = "Customer";

    // Activity IDs
    public static String ADD_POST_OFFICE = "Add Post Office";
    public static String VIEW_POST_OFFICES = "View Post Offices";
    public static String VIEW_HISTORY = "View History";
    public static String VIEW_SEND_POSTS = "View Send Post History";
    public static String CREATE_DISTRIBUTION = "Create Distribution";
    public static String ADD_POST = "Add Post";
    public static String VIEW_POSTS = "View Pending Posts";
    public static String SEARCH_BY_REFERENCE_NUMBER = "Search by reference number";
    public static String SEARCH_BY_QR_SCANNER = "Search by QR scanner";
    public static class ValidateResponse {
        public ValidateResponse(boolean result, String message) {
            this.result = result;
            this.message = message;
        }

        public boolean result;
        public String message;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setLoggedInUser(Context context, HashMap<String, String> user) {
        SharedPreferences sharedPreference = context.getSharedPreferences(app_package, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreference.edit();
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        prefsEditor.putString("loggedUser", userJson);
        prefsEditor.commit();
    }

    public static HashMap<String, String> getLoggedInUser(Context context) {
        SharedPreferences sharedPreference = context.getSharedPreferences(app_package, MODE_PRIVATE);
        Gson gson = new Gson();
        String userJson = sharedPreference.getString("loggedUser", "");
        return gson.fromJson(userJson, HashMap.class);
    }
}
