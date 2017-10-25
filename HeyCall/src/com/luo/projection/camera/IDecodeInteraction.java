package com.zigsun.luo.projection.camera;

import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;

/**
 * 解码类接口
 */
public interface IDecodeInteraction {
    /**
     * 获取相机管理
     *
     * @return
     */
    CameraManager getCameraManager();

    /**
     * 解码结果处理的handler,UI handler.
     *
     * @return
     */
    Handler getHandler();


    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode   A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor);

    ViewfinderView getViewfinderView();

    public void drawViewfinder();
}
