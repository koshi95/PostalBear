package com.sanduni.koshila.postalbear.util.executor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.runnable.CustomRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

public class BulkUpdateExecutor implements Executor {

    public BulkUpdateExecutor(
            final DBCallbackHandler handler, final String tableName,
            final ArrayList<ContentValues> valueList, final String whereClause, final ArrayList<String[]> whereClauseArgsList, final PostalBearDBHelper postalBearDBHelper) {
        CustomRunnable insertRunnable = new CustomRunnable() {
            @Override
            public int executeAction() {
                SQLiteDatabase db = postalBearDBHelper.getWritableDatabase();
                for (ContentValues values: valueList) {
                    db.update(tableName, values, whereClause, whereClauseArgsList.get((valueList.indexOf(values))));
                }
                return 0;
            }

            @Override
            public ArrayList<HashMap<String, String>> executeQuery() {
                return null;
            }

            @Override
            public void run() {
                int res = executeAction();
                handler.onSuccess(1, res);
            }
        };
        this.execute(insertRunnable);
    }

    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
