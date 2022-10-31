package com.sanduni.koshila.postalbear.util.callbackInterface;

import java.util.ArrayList;
import java.util.HashMap;

public interface DBCallbackHandler {
    void onSuccess(int actionId, ArrayList<HashMap<String, String>> res);
    void onSuccess(int actionId, int res);
    void onFailed(int failureId);
}
