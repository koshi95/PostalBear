package com.sanduni.koshila.postalbear.util.executor;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.runnable.CustomRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

public class QueryExecutor implements Executor {

    public QueryExecutor(
            final DBCallbackHandler handler, final String tableName,
            final String[] projection, final String selection,
            final String[] selectionArgs, final String sortOrder, final PostalBearDBHelper postalBearDBHelper) {
        CustomRunnable queryRunnable = new CustomRunnable() {
            @Override
            public int executeAction() {
                return 0;
            }

            @Override
            public ArrayList<HashMap<String, String>> executeQuery() {
                SQLiteDatabase db = postalBearDBHelper.getReadableDatabase();
                Cursor cursor = db.query(tableName, projection, selection, selectionArgs,
                        null, null, sortOrder);
                ArrayList<HashMap<String, String>> result = new ArrayList<>();
                while(cursor.moveToNext()) {
                    HashMap<String, String> row = new HashMap<>();
                    for (String s : projection) {
                        String entryValue = cursor.getString(cursor.getColumnIndex(s));
                        row.put(s, entryValue);
                    }
                    result.add(row);
                }
                cursor.close();
                return result;
            }

            @Override
            public void run() {
                ArrayList<HashMap<String, String>> res = executeQuery();
                handler.onSuccess(0, res);
            }
        };
        this.execute(queryRunnable);
    }

    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
