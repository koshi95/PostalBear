package com.sanduni.koshila.postalbear.util.executor;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.sanduni.koshila.postalbear.util.runnable.CustomRunnable;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

public class UpdateExecutor implements Executor {

    public UpdateExecutor(
            final DBCallbackHandler handler, final String tableName,
            final ContentValues values, final String whereClause, final String[] whereClauseArgs, final PostalBearDBHelper postalBearDBHelper) {
        CustomRunnable insertRunnable = new CustomRunnable() {
            @Override
            public int executeAction() {
                SQLiteDatabase db = postalBearDBHelper.getWritableDatabase();
                long id = db.update(tableName, values, whereClause, whereClauseArgs);
                return (int) id;
            }

            @Override
            public ArrayList<HashMap<String, String>> executeQuery() {
                return null;
            }

            @Override
            public void run() {
                int res = executeAction();
                if (res == -1) {
                    handler.onFailed(res);
                } else {
                    handler.onSuccess(1, res);
                }
            }
        };
        this.execute(insertRunnable);
    }

    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
