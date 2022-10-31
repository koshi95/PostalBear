package com.sanduni.koshila.postalbear.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                // nothing to do;
            }

            public void onFinish() {// Start home activity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                // close splash activity
                finish();
            }
        }.start();

    }
}