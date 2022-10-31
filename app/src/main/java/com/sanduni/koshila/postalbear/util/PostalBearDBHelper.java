package com.sanduni.koshila.postalbear.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import static com.sanduni.koshila.postalbear.util.Common.md5;

public class PostalBearDBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "postalbear.db";
    private static int DB_VERSION = 1;

    // Create Tables
    private static final String SQL_CREATE_USER =
            "CREATE TABLE " +
            PostalBearDBContract.User.TABLE_NAME + " (" +
            PostalBearDBContract.User._ID + " INTEGER PRIMARY KEY," +
            PostalBearDBContract.User.COLUMN_NAME_ADDRESS + " TEXT," +
            PostalBearDBContract.User.COLUMN_NAME_NIC + " TEXT UNIQUE," +
            PostalBearDBContract.User.COLUMN_NAME_FIRSTNAME + " TEXT," +
            PostalBearDBContract.User.COLUMN_NAME_LASTNAME + " TEXT," +
            PostalBearDBContract.User.COLUMN_NAME_PASSWORD + " TEXT," +
            PostalBearDBContract.User.COLUMN_NAME_USERROLE + " TEXT)";

    private static final String SQL_CREATE_EMPLOYEE =
            "CREATE TABLE " +
            PostalBearDBContract.Employee.TABLE_NAME + " (" +
            PostalBearDBContract.Employee._ID + " INTEGER PRIMARY KEY," +
            PostalBearDBContract.Employee.COLUMN_NAME_NIC + " TEXT UNIQUE)";

    private static final String SQL_CREATE_POST_OFFICE =
            "CREATE TABLE " +
            PostalBearDBContract.PostOffice.TABLE_NAME + " (" +
            PostalBearDBContract.PostOffice._ID + " INTEGER PRIMARY KEY," +
            PostalBearDBContract.PostOffice.COLUMN_NAME_LATITUDE + " REAL," +
            PostalBearDBContract.PostOffice.COLUMN_NAME_LONGITUDE + " REAL," +
            PostalBearDBContract.PostOffice.COLUMN_NAME_DISTRICT + " REAL," +
            PostalBearDBContract.PostOffice.COLUMN_NAME_TELEPHONE + " REAL," +
            PostalBearDBContract.PostOffice.COLUMN_NAME_NAME + " TEXT UNIQUE)";

    private static final String SQL_CREATE_POST =
            "CREATE TABLE " +
            PostalBearDBContract.Post.TABLE_NAME + " (" +
            PostalBearDBContract.Post._ID + " INTEGER PRIMARY KEY," +
            PostalBearDBContract.Post.COLUMN_NAME_CHARGES + " TEXT," +
            PostalBearDBContract.Post.COLUMN_NAME_DISTRIBUTOR_NIC + " TEXT," +
            PostalBearDBContract.Post.COLUMN_NAME_LAST_POST_OFFICE_ID + " INTEGER," +
            PostalBearDBContract.Post.COLUMN_NAME_NEXT_POST_OFFICE_ID + " INTEGER," +
            PostalBearDBContract.Post.COLUMN_NAME_RECEIVER_ADDRESS + " TEXT," +
            PostalBearDBContract.Post.COLUMN_NAME_REFERENCE_NUMBER + " TEXT UNIQUE," +
            PostalBearDBContract.Post.COLUMN_NAME_SEND_DATE + " INTEGER," +
            PostalBearDBContract.Post.COLUMN_NAME_SENDER_ADDRESS + " TEXT," +
            PostalBearDBContract.Post.COLUMN_NAME_DELIVERED + " INTEGER," +
            PostalBearDBContract.Post.COLUMN_NAME_SENDER_NIC + " TEXT)";

    private static final String SQL_CREATE_DISTRIBUTED_POST =
            "CREATE TABLE " +
                    PostalBearDBContract.DistributedPost.TABLE_NAME + " (" +
                    PostalBearDBContract.DistributedPost._ID + " INTEGER PRIMARY KEY," +
                    PostalBearDBContract.DistributedPost.COLUMN_NAME_DISTRIBUTED_DATE + " INTEGER," +
                    PostalBearDBContract.DistributedPost.COLUMN_NAME_RECEIVER_NIC + " TEXT," +
                    PostalBearDBContract.DistributedPost.COLUMN_NAME_REFERENCE_NUMBER + " TEXT UNIQUE)";

    private static final String SQL_CREATE_FAILED_POST =
            "CREATE TABLE " +
                    PostalBearDBContract.FailedPost.TABLE_NAME + " (" +
                    PostalBearDBContract.FailedPost._ID + " INTEGER PRIMARY KEY," +
                    PostalBearDBContract.FailedPost.COLUMN_NAME_REFERENCE_NUMBER + " TEXT UNIQUE," +
                    PostalBearDBContract.FailedPost.COLUMN_NAME_VISITED_DATE + " INTEGER)";

    private static final String[] SQL_CREATE_SAMPLE_USER =
            new String[]{
                    String.format("INSERT INTO %s (%s ,%s ,%s ,%s ,%s ,%s ) VALUES ( 'Colombo' ,'123456781V' ,'Sandhuni' ,'Koshila' ,'%s' ,'Admin' )",
                    PostalBearDBContract.User.TABLE_NAME,
                    PostalBearDBContract.User.COLUMN_NAME_ADDRESS,
                    PostalBearDBContract.User.COLUMN_NAME_NIC,
                    PostalBearDBContract.User.COLUMN_NAME_FIRSTNAME,
                    PostalBearDBContract.User.COLUMN_NAME_LASTNAME,
                    PostalBearDBContract.User.COLUMN_NAME_PASSWORD,
                    PostalBearDBContract.User.COLUMN_NAME_USERROLE,
                    md5("12345678")),

                    String.format("INSERT INTO %s (%s ,%s ,%s ,%s ,%s ,%s ) VALUES ( 'Colombo' ,'123456782V' ,'S' ,'Koshila' ,'%s' ,'Customer' )",
                    PostalBearDBContract.User.TABLE_NAME,
                    PostalBearDBContract.User.COLUMN_NAME_ADDRESS,
                    PostalBearDBContract.User.COLUMN_NAME_NIC,
                    PostalBearDBContract.User.COLUMN_NAME_FIRSTNAME,
                    PostalBearDBContract.User.COLUMN_NAME_LASTNAME,
                    PostalBearDBContract.User.COLUMN_NAME_PASSWORD,
                    PostalBearDBContract.User.COLUMN_NAME_USERROLE,
                    md5("12345678")),

                    String.format("INSERT INTO %s (%s ,%s ,%s ,%s ,%s ,%s ) VALUES ( 'Colombo' ,'123456783V' ,'SK' ,'Postman' ,'%s' ,'Postman' )",
                            PostalBearDBContract.User.TABLE_NAME,
                            PostalBearDBContract.User.COLUMN_NAME_ADDRESS,
                            PostalBearDBContract.User.COLUMN_NAME_NIC,
                            PostalBearDBContract.User.COLUMN_NAME_FIRSTNAME,
                            PostalBearDBContract.User.COLUMN_NAME_LASTNAME,
                            PostalBearDBContract.User.COLUMN_NAME_PASSWORD,
                            PostalBearDBContract.User.COLUMN_NAME_USERROLE,
                            md5("12345678"))
            };

    private static final String[] SQL_CREATE_POST_OFFICE_ENTRY =
            new String[] {
                    String.format("INSERT INTO %s (%s ,%s ,%s ,%s ,%s ) VALUES ( 'Bambalapitiya' ,'COLOMBO' ,'011-2859028' , '80.0923' ,'6.8409')",
                    PostalBearDBContract.PostOffice.TABLE_NAME,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_NAME,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_DISTRICT,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_TELEPHONE,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_LATITUDE,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_LONGITUDE),

                    String.format("INSERT INTO %s (%s ,%s ,%s ,%s ,%s ) VALUES ( 'Peradeniya' ,'KANDY' ,'081-2386441' , '80.6000' ,'7.2667')",
                    PostalBearDBContract.PostOffice.TABLE_NAME,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_NAME,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_DISTRICT,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_TELEPHONE,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_LATITUDE,
                    PostalBearDBContract.PostOffice.COLUMN_NAME_LONGITUDE)};

    // HashMap of Create Queries
    private static HashMap<String, String> createQueries;

    public PostalBearDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        // Create tables
        for (String createQuery: getCreateQueries().values()) {
            DB.execSQL(createQuery);
        }

        // Add initial entries
        for (String createUsersQuery: SQL_CREATE_SAMPLE_USER) {
            DB.execSQL(createUsersQuery);
        }
        for (String createPostOfficesQuery: SQL_CREATE_POST_OFFICE_ENTRY) {
            DB.execSQL(createPostOfficesQuery);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        // Drop tables if exists
        for (String table: getCreateQueries().keySet()) {
            DB.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(DB);
    }

    @Override
    public void onDowngrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        onUpgrade(DB, oldVersion, newVersion);
    }

    private HashMap<String, String> getCreateQueries() {
        if (createQueries == null) {
            setCreateQueries();
        }
        return createQueries;
    }

    private void setCreateQueries(){
        createQueries = new HashMap<>();
        createQueries.put(PostalBearDBContract.User.TABLE_NAME, SQL_CREATE_USER);
        createQueries.put(PostalBearDBContract.Employee.TABLE_NAME, SQL_CREATE_EMPLOYEE);
        createQueries.put(PostalBearDBContract.PostOffice.TABLE_NAME, SQL_CREATE_POST_OFFICE);
        createQueries.put(PostalBearDBContract.Post.TABLE_NAME, SQL_CREATE_POST);
        createQueries.put(PostalBearDBContract.DistributedPost.TABLE_NAME, SQL_CREATE_DISTRIBUTED_POST);
        createQueries.put(PostalBearDBContract.FailedPost.TABLE_NAME, SQL_CREATE_FAILED_POST);
    }
}
