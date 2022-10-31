package com.sanduni.koshila.postalbear.util.runnable;

import java.util.ArrayList;
import java.util.HashMap;

public interface CustomRunnable extends Runnable{
    public int executeAction();
    public ArrayList<HashMap<String, String>> executeQuery();
}
