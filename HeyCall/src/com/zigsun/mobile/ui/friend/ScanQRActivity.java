package com.zigsun.mobile.ui.friend;

import android.content.Intent;
import android.graphics.Bitmap;

import com.google.zxing.Result;
import com.zigsun.luo.projection.camera.CaptureQRActivity;

public class ScanQRActivity extends CaptureQRActivity {

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {

        boolean fromLiveScan = barcode != null;
//    解码成功
        if (fromLiveScan) {
//            historyManager.addHistoryItem(rawResult, resultHandler);
            // Then not from history, so beep/vibrate and we have an image to draw on
            beepManager.playBeepSoundAndVibrate();
//            Log.d(TAG, "文本消息为:" + rawResult.getText());
            setResult(RESULT_OK, new Intent(rawResult.getText()));
            finish();
        }

    }
}
