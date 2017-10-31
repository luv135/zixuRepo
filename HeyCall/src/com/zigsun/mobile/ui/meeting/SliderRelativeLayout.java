//package com.zigsun.mobile.ui.meeting;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Rect;
//import android.os.Handler;
//import android.os.Message;
//import android.os.Vibrator;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.zigsun.mobile.R;
//
//
//public class SliderRelativeLayout extends RelativeLayout {
//
//	private static String TAG = "SliderRelativeLayout";
//	private TextView tv_slider_icon = null; // 初始控件，用来判断是否为拖动？
//	private Bitmap dragBitmap = null; //拖拽图片
//	private Context mContext = null; // 初始化图片拖拽时的Bitmap对象
//	private Handler mainHandler = null; //与主Activity通信的Handler对象
//	private ImageView mPhoneImageView;
//	private ImageView mUnlockImageView;
//	private ImageView mMessageImageView;
//	private ImageView mCameraImageView;
//	private boolean mStopBoolean = false;
//	private Canvas mCanvas;
//	public SliderRelativeLayout(Context context) {
//		super(context);
//		mContext = context;
//		initDragBitmap();
//	}
//
//	public SliderRelativeLayout(Context context, AttributeSet attrs) {
//		super(context, attrs, 0);
//		mContext = context;
//		initDragBitmap();
//	}
//
//	public SliderRelativeLayout(Context context, AttributeSet attrs,
//			int defStyle) {
//		super(context, attrs, defStyle);
//		mContext = context;
//		initDragBitmap();
//	}
//
//	// 初始化图片拖拽时的Bitmap对象
//	private void initDragBitmap() {
//		if (dragBitmap == null)
//			dragBitmap = BitmapFactory.decodeResource(mContext.getResources(),
//					R.drawable.getup_slider_ico_normal);
//	}
//
//	@Override
//	protected void onFinishInflate() {
//		super.onFinishInflate();
//		// 该控件主要判断是否处于滑动点击区域。滑动时 处于INVISIBLE(不可见)状态，滑动时处于VISIBLE(可见)状态
//		tv_slider_icon = (TextView) findViewById(R.id.slider_icon);
//		mPhoneImageView = (ImageView) findViewById(R.id.iv_phone);
//		mUnlockImageView = (ImageView) findViewById(R.id.iv_unlock);
//		mCameraImageView = (ImageView) findViewById(R.id.iv_camera);
//		mMessageImageView = (ImageView) findViewById(R.id.iv_message);
//	}
//	private int mLastMoveX = 1000;  //当前bitmap应该绘制的地方 ， 初始值为足够大，可以认为看不见
//	private int mLastMoveY = 1000;  //当前bitmap应该绘制的地方 ， 初始值为足够大，可以认为看不见
//	public boolean onTouchEvent(MotionEvent event) {
//		int x = (int) event.getX();
//		int y = (int) event.getY();
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			mLastMoveX = (int) event.getX();
//			mLastMoveY = (int) event.getY();
//			//处理Action_Down事件：  判断是否点击了滑动区域
//			return handleActionDownEvenet(event);
//		case MotionEvent.ACTION_MOVE:
//			mLastMoveX = x; //保存了X轴方向
//			mLastMoveY = y;
//            invalidate(); //重新绘制
//			return true;
//		case MotionEvent.ACTION_UP:
//			//处理Action_Up事件：  判断是否解锁成功，成功则结束我们的Activity ；否则 ，缓慢回退该图片。
//			handleActionUpEvent(event);
//			return true;
//		}
//		return super.onTouchEvent(event);
//	}
//
//	// 绘制拖动时的图片
//	@Override
//    protected void onDraw (Canvas canvas) {
//		super.onDraw(canvas);
//		// 图片更随手势移动
//		if (!mStopBoolean) {
//			invalidateDragImg(canvas);
//		}
//		mCanvas = canvas;
//	}
//
//	// 图片更随手势移动
//	private void invalidateDragImg(Canvas canvas) {
//		//Log.e(TAG, "handleActionUpEvenet : invalidateDragImg" );
//		//以合适的坐标值绘制该图片
//		int drawXCor = mLastMoveX - dragBitmap.getWidth()/2;
//		int drawYCor = mLastMoveY - dragBitmap.getHeight()/2;
//	    canvas.drawBitmap(dragBitmap,  drawXCor < 0 ? 5 : drawXCor , drawYCor , null);
////	    isHitUnlock(canvas);
//	}
//
//	// 手势落下是，是否点中了图片，即是否需要开始移动
//	private boolean handleActionDownEvenet(MotionEvent event) {
//		Rect rect = new Rect();
//		tv_slider_icon.getHitRect(rect);
//		boolean isHit = rect.contains((int) event.getX(), (int) event.getY());
//		//开始拖拽 ，隐藏该图片
//		if(isHit && !mStopBoolean){
//			tv_slider_icon.setVisibility(View.INVISIBLE);
//			mPhoneImageView.setVisibility(View.VISIBLE);
//			mUnlockImageView.setVisibility(View.VISIBLE);
//			mMessageImageView.setVisibility(View.VISIBLE);
//			mCameraImageView.setVisibility(View.VISIBLE);
//		}
//		return isHit;
//	}
//
//	//判断是否到达解锁点
//	private boolean isHitUnlock() {
//		Rect phoneRect = new Rect();
//		mPhoneImageView.getHitRect(phoneRect);
//		Rect unlockRect = new Rect();
//		mUnlockImageView.getHitRect(unlockRect);
//		Rect messageRect = new Rect();
//		mMessageImageView.getHitRect(messageRect);
//		Rect cameraRect = new Rect();
//		mCameraImageView.getHitRect(cameraRect);
//		//解锁到电话界面
//		if(phoneRect.contains(mLastMoveX,mLastMoveY)){
//			mStopBoolean = true;
//			adStopTwoSecond();
//		   //结束我们的主Activity界面
//			Message msg = new Message();
//			msg.what = MainLockActivity.MSG_PHONE_LOCK_SUCESS;
//			mainHandler.sendMessageDelayed(msg, 2*1000);
////		   mainHandler.obtainMessage(MainLockActivity.MSG_PHONE_LOCK_SUCESS).sendToTarget();
//		   return true;
//		}else if(unlockRect.contains(mLastMoveX,mLastMoveY)){
//			mStopBoolean = true;
//			adStopTwoSecond();
//			//结束我们的主Activity界面
//			Message msg = new Message();
//			msg.what = MainLockActivity.MSG_LOCK_SUCESS;
//			mainHandler.sendMessageDelayed(msg, 2*1000);
//			return true;
//		}else if(messageRect.contains(mLastMoveX,mLastMoveY)){
//			mStopBoolean = true;
//			adStopTwoSecond();
//			//结束我们的主Activity界面
//			Message msg = new Message();
//			msg.what = MainLockActivity.MSG_MESSAGE_LOCK_SUCESS;
//			mainHandler.sendMessageDelayed(msg, 2*1000);
////			mainHandler.obtainMessage(MainLockActivity.MSG_MESSAGE_LOCK_SUCESS).sendToTarget();
//			return true;
//		}else if(cameraRect.contains(mLastMoveX,mLastMoveY)){
//			mStopBoolean = true;
//			adStopTwoSecond();
//			//结束我们的主Activity界面
//			Message msg = new Message();
//			msg.what = MainLockActivity.MSG_CAMERA_LOOK_SUCESS;
//			mainHandler.sendMessageDelayed(msg, 2*1000);
////			mainHandler.obtainMessage(MainLockActivity.MSG_CAMERA_LOOK_SUCESS).sendToTarget();
//			return true;
//		}
//		return false;
//	}
//
//	//回退动画时间间隔值
//	private static int BACK_DURATION = 20 ;   // 20ms
//    //水平方向前进速率
//	private static float VE_HORIZONTAL = 0.7f ;  //0.1dip/ms
//    //判断松开手指时，是否达到末尾即可以开锁了 , 是，则开锁，否则，通过一定的算法使其回退。
//	private void handleActionUpEvent(MotionEvent event){
//		int x = (int) event.getX() ;
//		int y = (int)event.getY();
//		//解锁到电话界面
//		if(isHitUnlock()){
//		}else {
//			mStopBoolean = false;
//			//没有成功解锁，以一定的算法使其回退
//		    //每隔20ms , 速率为0.6dip/ms ,  使当前的图片往后回退一段距离，直到到达最左端
//			mLastMoveX = x ;  //记录手势松开时，当前的坐标位置。
//			int distance = x - tv_slider_icon.getRight() ;
//			//只有移动了足够距离才回退
//			Log.e(TAG, "handleActionUpEvent : mLastMoveX -->" + mLastMoveX + " distance -->" + distance );
//			if(distance >= 0)
//			    mHandler.postDelayed(BackDragImgTask, BACK_DURATION);
//			else{  //复原初始场景
//				resetViewState();
//			}
//		}
//	}
//
//	//暂停两秒
//	private void adStopTwoSecond() {
//		mPhoneImageView.setVisibility(View.INVISIBLE);
//		mUnlockImageView.setVisibility(View.INVISIBLE);
//		mCameraImageView.setVisibility(View.INVISIBLE);
//		mMessageImageView.setVisibility(View.INVISIBLE);
////		mCanvas.drawBitmap(dragBitmap, 2000, 2000, null);
//		invalidate();
////		try {
////			Thread.sleep(2*1000);
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////		}
//	}
//
//	//重置初始的状态，显示tv_slider_icon图像，使bitmap不可见
//	private void resetViewState(){
//		mLastMoveX = 1000;
//		mLastMoveY = 1000;
//		tv_slider_icon.setVisibility(View.VISIBLE);
//		mPhoneImageView.setVisibility(View.INVISIBLE);
//		mUnlockImageView.setVisibility(View.INVISIBLE);
//		mCameraImageView.setVisibility(View.INVISIBLE);
//		mMessageImageView.setVisibility(View.INVISIBLE);
//		invalidate();        //重绘最后一次
//	}
//
//	//通过延时控制当前绘制bitmap的位置坐标
//	private Runnable BackDragImgTask = new Runnable(){
//		public void run(){
//			//一下次Bitmap应该到达的坐标值
//			mLastMoveX = mLastMoveX - (int)(BACK_DURATION * VE_HORIZONTAL);
//			invalidate();//重绘
//			//是否需要下一次动画 ？ 到达了初始位置，不在需要绘制
//			boolean shouldEnd = Math.abs(mLastMoveX - tv_slider_icon.getRight()) <= 8 ;
//			if(!shouldEnd)
//			    mHandler.postDelayed(BackDragImgTask, BACK_DURATION);
//			else { //复原初始场景
//				resetViewState();
//			}
//		}
//	};
//
//	private Handler mHandler =new Handler (){
//		public void handleMessage(Message msg){
//		}
//	};
//	//震动一下下咯
//	private void virbate(){
//		Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
//		vibrator.vibrate(200);
//	}
//	public void setMainHandler(Handler handler){
//		mainHandler = handler;//activity所在的Handler对象
//	}
//}