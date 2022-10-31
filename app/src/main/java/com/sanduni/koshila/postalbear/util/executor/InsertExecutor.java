package com.sanduni.koshila.postalbear.util.executor;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.sanduni.koshila.postalbear.util.runnable.CustomRunnable;
import com.sanduni.koshila.postalbear.util.callbackInterface.DBCallbackHandler;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

import static com.sanduni.koshila.postalbear.util.Common.ALREADY_EXISTS;

public class InsertExecutor implements Executor {

    public InsertExecutor(
            final DBCallbackHandler handler, final String tableName,
            final ContentValues values, final PostalBearDBHelper postalBearDBHelper) {
        CustomRunnable insertRunnable = new CustomRunnable() {
            @Override
            public int executeAction() {
                SQLiteDatabase db = postalBearDBHelper.getWritableDatabase();
                long newId;
                try {
                    newId  = db.insertOrThrow(tableName, null, values);
                    return (int) newId;
                }
                catch (SQLiteConstraintException ex) {
                    return ALREADY_EXISTS;
                }
                catch (Exception ex) {
                    return -1;
                }
            }

            @Override
            public ArrayList<HashMap<String, String>> executeQuery() {
                return null;
            }

            @Override
            public void run() {
                int res = executeAction();
                if (res < 0) {
                    handler.onFailed(res);
                } else {
                    handler.onSuccess(0, res);
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
