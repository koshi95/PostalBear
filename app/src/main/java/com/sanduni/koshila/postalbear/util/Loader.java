package com.sanduni.koshila.postalbear.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sanduni.koshila.postalbear.R;

public class Loader {
    private Activity activity;
    private AlertDialog dialog;
    private boolean cancelable = false;
    private String message;
    private boolean dismissAllowed = false;
    private CountDownTimer loadingCountDown;
    private CountDownTimer dismissCountDown;
    private LoaderListener loaderListener;

    public interface LoaderListener {
        void loaderDismissed();
    }

    public Loader(Activity activity, String message) {
        this.activity = activity;
        this.message = message;
        loadingCountDown = new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                dismissAllowed = true;
            }
        };
        dismissCountDown = new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                dismiss(null);
            }
        };
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public void show() {
        dismissAllowed = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_layout, null);
        builder.setView(view);
        if (message != null && !message.equals("")) {
            TextView loadingTextView = view.findViewById(R.id.loadingMessage);
            loadingTextView.setText(message);
        }
        builder.setCancelable(cancelable);

        dialog = builder.create();
        dialog.show();
        loadingCountDown.start();
    }

    public void dismiss(LoaderListener loaderListener) {
        if (loaderListener != null) {
            this.loaderListener = loaderListener;
        }
        if (dismissAllowed) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                this.loaderListener.loaderDismissed();
            }
        }
        else {
            dismissCountDown.start();
        }
    }
}
