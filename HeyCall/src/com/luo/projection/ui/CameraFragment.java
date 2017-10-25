package com.zigsun.luo.projection.ui;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.interfaces.ICameraControl;
import com.zigsun.luo.projection.base.MainTabBaseFragment;
import com.zigsun.luo.projection.camera.FinishListener;
import com.zigsun.luo.projection.camera.PreviewView;
import com.zigsun.luo.projection.camera.WrapperCameraManager;
import com.zigsun.luo.projection.interfaces.ITabVisibilityChangeListener;
import com.zigsun.luo.projection.ProjectionActivity.BackPressListener;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

/**
 * Camera interface view
 *
 * @author luowei
 */
public class CameraFragment extends MainTabBaseFragment implements BackPressListener, ICameraControl, SurfaceHolder.Callback {

    private static final String TAG = CameraFragment.class.getSimpleName();
    private static final int SET_PREVIEW_RESOLUTION = 1;
    private static final int INIT_CAMERA_ERROR = 2;
    private static final int HIDE_LOADING_VIEW = 3;

    private boolean hasSurface;
    private PreviewView previewView;
    private WrapperCameraManager cameraManager;
    private ITabVisibilityChangeListener tabListener;
    //    private View root;
    private TextView loadingTextView;
    private CountDownLatch waitInitCameraFinish;
    private MyHandler MainHandler = new MyHandler(this);
    private Handler initCameraHandler;
    private boolean needOpenCamera;
    private SurfaceHolder surfaceHolder;

    @Override
    public void showSelf(FragmentTransaction transaction) {
        super.showSelf(transaction);
        openCamera();
    }

    @Override
    public void hideSelf(FragmentTransaction transaction) {
        super.hideSelf(transaction);
        closeCamera();
    }

    @Override
    public void openCamera() {
        //preview created initCamera directly.
        needOpenCamera = true;
        if (surfaceHolder != null) {
            Log.d(TAG, "surfaceHolder exist and initCamera");
            initCamera(surfaceHolder);
        }


    }

    private void initCamera(SurfaceHolder surfaceHolder) {

        Log.d(TAG, "initCamera");
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }

        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");

            return;
        }
        try {
            showLoadingView();
            cameraManager.openFrontCamera(surfaceHolder);
//			cameraManager.openBackCamera(surfaceHolder);
            updateCameraPreviewSize();
            Log.d(TAG, "startPreview");
            cameraManager.startPreview();
//            hideLoadingView();
//            Message message = Message.obtain();
//            message.what = HIDE_LOADING_VIEW;

            //delay 1 sec show camera preview
            //avoid black splash whatever.

            hideLoadingViewDelay(500);
//            MainHandler.sendMessageDelayed(message, 1000);

        } catch (IOException | RuntimeException e) {
            Log.w(TAG, e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void updateCameraPreviewSize() {
        Point cameraResolution = cameraManager.getCameraResolution();
        previewView.setAspectRatio(cameraResolution.x, cameraResolution.y);
    }

    private void showLoadingView() {
        loadingTextView.setAlpha(1);
//        loadingTextView.setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
//        final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.camera_loading_slide_out);
//        final Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.camera_slide_in);
//        loadingTextView.startAnimation(animation2);
//        previewView.startAnimation(animation);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                loadingTextView.setVisibility(View.INVISIBLE);
//                Log.d(TAG, "onAnimationEnd");
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

//        loadingTextView.animate().alpha(0).start();
    }

    private void hideLoadingViewDelay(long delayMillis) {
        loadingTextView.animate().alpha(0).setStartDelay(delayMillis).start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (cameraManager.isOpen()) {
            cameraManager.cameraOrientationUpdate();
            updateCameraPreviewSize();
        }
    }

    @Override
    public void closeCamera() {
        needOpenCamera = false;
        releaseCamera();
    }

    private void releaseCamera() {
        if (cameraManager != null && cameraManager.isOpen()) {
            Log.d(TAG, "releaseCamera()");
            cameraManager.stopPreview();
            cameraManager.closeDriver();
//            loadingTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        Log.d(TAG, "surfaceCreated()");
        if (needOpenCamera) {
            Log.d(TAG, "surfaceCreated()-> initCamera()");
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceHolder = null;
    }

    private static class MyHandler extends Handler {
        WeakReference<CameraFragment> fragmentWeak;

        public MyHandler(CameraFragment cameraFragment) {
            this.fragmentWeak = new WeakReference<CameraFragment>(cameraFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final CameraFragment fragment = fragmentWeak.get();
            if (fragment == null) {
                Log.w(TAG, "Handle Message > CameraFragment is null!");
                return;
            }
            switch (msg.what) {
                case SET_PREVIEW_RESOLUTION:
                    fragment.previewView.setAspectRatio(msg.arg1, msg.arg2);
                    fragment.cameraManager.startPreview();
                    fragment.loadingTextView.setVisibility(View.INVISIBLE);
                    break;
                case INIT_CAMERA_ERROR:
                    fragment.displayFrameworkBugMessageAndExit();
                    break;
                case HIDE_LOADING_VIEW:
                    fragment.hideLoadingView();
                    break;
            }
//            fragment.waitInitCameraFinish.countDown();
        }
    }

    public CameraFragment() {
        // Required empty public constructor
        Log.d(TAG, "CameraFragment()");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        cameraManager = new WrapperCameraManager(activity);
//		try {
//			tabListener = (ITabVisibilityChangeListener) activity;
//		} catch (Exception e) {
//			throw new ClassCastException(
//					"must implements ITabVisibilityChangeListener!");
//		}
//		Log.d(TAG, "startService");
//		getActivity().startService(new Intent(getActivity(), ShareWindowService.class));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tabListener = null;
//        Log.d(TAG, "stopService");
//		getActivity().stopService(new Intent(getActivity(), ShareWindowService.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        hasSurface = false;

    }

    @Override
    public String getFlagTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()");
        previewView = (PreviewView) view.findViewById(R.id.sv_preview);
        loadingTextView = (TextView) view.findViewById(R.id.initCameraTextView);
//			previewView.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
////					tabListener.switchTabVisibility();
//					// getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//					// //---状态栏颜色
//					// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//					// setTranslucentStatus(true);
//					// }
//					// SystemBarTintManager tintManager = new
//					// SystemBarTintManager(getActivity());
//					// tintManager.setStatusBarTintEnabled(true);
//					// tintManager.setStatusBarAlpha(0);
//					// //
//					// tintManager.setStatusBarTintResource(android.R.color.transparent);
//				}
//			});
        view.findViewById(R.id.closeCameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCamera();
            }
        });
        view.findViewById(R.id.openCameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        view.findViewById(R.id.fullScreenPreviewButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewView.setAspectRatio(0, 0);
            }
        });
        view.findViewById(R.id.normalScreenPreviewButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCameraPreviewSize();
            }
        });
//        loadingTextView.setVisibility(View.VISIBLE);

    }

//    @TargetApi(19)
//    private void setTranslucentStatus(boolean on) {
//        Window win = getActivity().getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        final SurfaceHolder holder = previewView.getHolder();
        holder.addCallback(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");

        releaseCamera();

        previewView.getHolder().removeCallback(this);
    }

//    private void releaseCameraThread() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (waitInitCameraFinish != null) {
////            等待相机初始化完成,handler消息处理完毕.
//                    Log.d(TAG, "wait InitCameraRun Finish...");
//                    try {
//                        waitInitCameraFinish.await();
//                    } catch (InterruptedException e) {
////                e.printStackTrace();
//                    }
//                }
//                if (cameraManager != null) {
//                    Log.d(TAG, " cameraManager->stop preview and close driver");
//                    cameraManager.stopPreview();
//                    cameraManager.closeDriver();
//                    cameraManager = null;
//                }
//                waitInitCameraFinish = null;
//            }
//        }).start();
//    }


//    private class InitCameraAsync extends AsyncTask<Void, Void, Point> {
//
//
//        private SurfaceHolder surfaceHolder;
//
//        private InitCameraAsync(SurfaceHolder surfaceHolder) {
//            this.surfaceHolder = surfaceHolder;
//        }
//
//        @Override
//        protected Point doInBackground(Void... params) {
//            if (cameraManager == null) {
//                cameraManager = new WrapperCameraManager(getActivity());
//            }
//            if (surfaceHolder == null) {
//                throw new IllegalStateException("No SurfaceHolder provided");
//            }
//            try {
//                if (cameraManager.isOpen()) {
//                    Log.w(TAG,
//                            "initCamera() while already open -- late SurfaceView callback?");
//                } else {
//                    cameraManager.openFrontCamera(surfaceHolder);
////			cameraManager.openBackCamera(surfaceHolder);
//                }
//                Point cameraResolution = cameraManager.getCameraResolution();
//                Log.d(TAG, "startPreview");
//                return cameraResolution;
////                Message message = Message.obtain();
////                message.what = SET_PREVIEW_RESOLUTION;
////                message.arg1 = cameraResolution.x;
////                message.arg2 = cameraResolution.y;
////                handler.sendMessage(message);
//
//            } catch (IOException | RuntimeException e) {
//                Log.w(TAG, e);
////                waitInitCameraFinish.countDown();
////                getActivity().runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        displayFrameworkBugMessageAndExit();
////                    }
////                });
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Point resolution) {
//            if (resolution == null){
//                displayFrameworkBugMessageAndExit();
//                return;
//            }
//            previewView.setAspectRatio(resolution.x, resolution.y);
//            cameraManager.startPreview();
//            loadingTextView.setVisibility(View.INVISIBLE);
//            waitInitCameraFinish.countDown();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            loadingTextView.setVisibility(View.VISIBLE);
//        }
//    }


//    public class InitCameraRun implements Runnable {
//        final SurfaceHolder surfaceHolder;
//
//        public InitCameraRun(SurfaceHolder surfaceHolder) {
//            this.surfaceHolder = surfaceHolder;
//        }
//
//        @Override
//        public void run() {
//            Log.d(TAG, "InitCameraRun()->run()");
//            if (cameraManager == null) {
//                cameraManager = new WrapperCameraManager(getActivity());
//            }
//            if (surfaceHolder == null) {
//                throw new IllegalStateException("No SurfaceHolder provided");
//            }
//            try {
//                if (cameraManager.isOpen()) {
//                    Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
//                } else {
//                    cameraManager.openFrontCamera(surfaceHolder);
////			cameraManager.openBackCamera(surfaceHolder);
//                }
//                Point cameraResolution = cameraManager.getCameraResolution();
//                Message message = Message.obtain();
//                message.what = SET_PREVIEW_RESOLUTION;
//                message.arg1 = cameraResolution.x;
//                message.arg2 = cameraResolution.y;
//                MainHandler.sendMessage(message);
//                Log.d(TAG, "startPreview");
//            } catch (IOException | RuntimeException e) {
//                Log.w(TAG, e);
//                MainHandler.sendEmptyMessage(INIT_CAMERA_ERROR);
////                waitInitCameraFinish.countDown();
////                getActivity().runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        displayFrameworkBugMessageAndExit();
////                    }
////                });
//            } finally {
//
//            }
//        }
//
//    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(
                getActivity()));
        builder.setOnCancelListener(new FinishListener(getActivity()));
        builder.show();
    }


    @Override
    public boolean onBackPressed() {
        return true;
    }


    /**
     * 摄像头初始&释放线程
     */
    private class InitCameraThread extends HandlerThread implements Handler.Callback {
        private static final int QUIT = 1;
        private static final int INIT = 2;
        private static final int RELEASE = 3;
        private Handler handler;

        public InitCameraThread(SurfaceHolder surfaceHolder) {
            super("InitCameraThread");
            Log.d(TAG, "InitCameraThread() ");

            this.surfaceHolder = surfaceHolder;
        }

        private SurfaceHolder surfaceHolder;

        public Handler getHandler() {
            handler = new Handler(getLooper(), this);
            return handler;
        }

        @Override
        public void run() {
            Log.d(TAG, "InitCameraThread running..");
            super.run();
            Log.d(TAG, "InitCameraThread exits");
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case QUIT:
                    Log.d(TAG, "handle QUIT");
                    getLooper().quit();
                    return true;
                case INIT:
                    Log.d(TAG, "handle INIT");
                    initCamera();
                    return true;
                case RELEASE:
                    Log.d(TAG, "handle RELEASE");
                    releaseCamera();
                    return true;
            }
            return false;
        }

        private void releaseCamera() {
            cameraManager.stopPreview();
            cameraManager.closeDriver();
//            previewView.clear();
        }

        private void initCamera() {
            if (surfaceHolder == null) {
                throw new IllegalStateException("No SurfaceHolder provided");
            }

            if (cameraManager.isOpen()) {
                Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
                return;
            }
            try {
                cameraManager.openFrontCamera(surfaceHolder);
//			cameraManager.openBackCamera(surfaceHolder);
                Point cameraResolution = cameraManager.getCameraResolution();
                Message message = Message.obtain();
                message.what = SET_PREVIEW_RESOLUTION;
                message.arg1 = cameraResolution.x;
                message.arg2 = cameraResolution.y;
                MainHandler.sendMessage(message);
                Log.d(TAG, "startPreview");
            } catch (IOException | RuntimeException e) {
                Log.w(TAG, e);
                MainHandler.sendEmptyMessage(INIT_CAMERA_ERROR);
            }
        }
    }
}
