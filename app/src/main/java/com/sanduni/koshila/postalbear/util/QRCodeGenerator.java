package com.sanduni.koshila.postalbear.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.content.Context.WINDOW_SERVICE;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class QRCodeGenerator {
    private String qrText;
    private Context context;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    public QRCodeGenerator(Context context, String qrText) {
        this.context = context;
        this.qrText = qrText;
    }

    public boolean generateQR() {
        if (TextUtils.isEmpty(qrText)) {
            return false;
        } else {
            WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

            Display display = manager.getDefaultDisplay();

            Point point = new Point();
            display.getSize(point);

            int width = point.x;
            int height = point.y;

            int dimen = Math.min(width, height);
            dimen = dimen * 3 / 4;
            qrgEncoder = new QRGEncoder(qrText, null, QRGContents.Type.TEXT, dimen);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                if (saveImage(bitmap, qrText)) {
                    return true;
                }
                else {
                    return false;
                }
            } catch (WriterException e) {
                Log.e("QRCodeGenerator", e.toString());
                return false;
            }
        }
    }

    private boolean saveImage(Bitmap finalBitmap, String image_name) {
        String root = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root, "PostalBear");
        boolean directoryExist = myDir.exists();
        boolean directoryCreated = false;
        if (!directoryExist) {
            directoryCreated = myDir.mkdirs();
        }
        if (directoryExist ||  directoryCreated) {
            String fileName = image_name+ ".jpg";
            File file = new File(myDir, fileName);
            if (file.exists()) file.delete();
            Log.i("QRCodeGenerator", root + fileName);
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                new SingleMediaScanner(context, file.getAbsoluteFile());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }
}
