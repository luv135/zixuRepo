package com.zigsun.luo.projection.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.zigsun.mobile.R;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;


public class CaptureQRActivity extends Activity implements SurfaceHolder.Callback, IDecodeInteraction {

    private static final String TAG = CaptureQRActivity.class.getSimpleName();

    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    private static final String[] ZXING_URLS = { "http://zxing.appspot.com/scan", "zxing://scan/" };

    public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);

    private WrapperCameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private ViewfinderView viewfinderView;
    private TextView statusView;
    private View resultView;
    private Result lastResult;
    private boolean hasSurface;
    private boolean copyToClipboard;
//    private IntentSource source;
    private String sourceUrl;
//    private ScanFromWebPageManager scanFromWebPageManager;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType,?> decodeHints;
    private String characterSet;
//    private HistoryManager historyManager;
//    private InactivityTimer inactivityTimer;
protected BeepManager beepManager;
    private PreviewView surfaceView;
//    private AmbientLightManager ambientLightManager;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    @Override
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {

        lastResult = rawResult;
//        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);



        boolean fromLiveScan = barcode != null;
//    解码成功
        if (fromLiveScan) {
//            historyManager.addHistoryItem(rawResult, resultHandler);
            // Then not from history, so beep/vibrate and we have an image to draw on
            beepManager.playBeepSoundAndVibrate();
            Log.d(TAG, "文本消息为:"+rawResult.getText());
            drawResultPoints(barcode, scaleFactor, rawResult);
            ImageView imageView = (ImageView) findViewById(R.id.image_view);
            imageView.setImageBitmap(barcode);
            TextView textView = (TextView) findViewById(R.id.text);
            textView.setText(rawResult.getText());
        }

    }
    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
     *
     * @param barcode   A bitmap of the captured image.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param rawResult The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 &&
                    (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
                            rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and metadata
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
                    }
                }
            }
        }
    }
    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(),
                    scaleFactor * a.getY(),
                    scaleFactor * b.getX(),
                    scaleFactor * b.getY(),
                    paint);
        }
    }
    public CameraManager getCameraManager() {
        return cameraManager;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture_qr);

        hasSurface = false;
//        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
//        ambientLightManager = new AmbientLightManager(this);

//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);



    }

    @Override
    protected void onResume() {
        super.onResume();

        // historyManager must be initialized here to update the history preference
//        historyManager = new HistoryManager(this);
//        historyManager.trimHistory();

        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        cameraManager = new WrapperCameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        handler = null;
        lastResult = null;
        beepManager.updatePrefs();
//        ambientLightManager.start(cameraManager);


        surfaceView = (PreviewView) findViewById(R.id.preview_view);


        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
//        inactivityTimer.onPause();
//        ambientLightManager.stop();
        beepManager.close();
        cameraManager.stopPreview();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged  width"+width+" height="+height);
    }



    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openBackCamera(surfaceHolder);
            Point cameraResolution = cameraManager.getCameraResolution();
            //设置等比,由于是竖屏,宽高相反
//            surfaceView.setAspectRatio(cameraResolution.x,cameraResolution.y);
            // Creating the handler starts the preview, which can also throw a RuntimeException.

            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
//            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }
    //设置扫描二维码取景尺寸
    private void setFrame(){

        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);


        int width1 = theScreenResolution.y/2;
        int height1 = theScreenResolution.y/2;

        cameraManager.setManualFramingRect(width1, height1);
    }



}
