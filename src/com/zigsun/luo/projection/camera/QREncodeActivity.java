//package com.luo.camera;
//
//import android.graphics.Bitmap;
//import android.graphics.Point;
//import android.support.v7.app.ActionBarActivity;
//import android.os.Bundle;
//import android.view.Display;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//
//public class QREncodeActivity extends ActionBarActivity {
//
//    private QRCodeEncoder encoder;
//    private int smallerDimension;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_qrencode);
//        encoder = new QRCodeEncoder();
//        findViewById(R.id.edit_text).setOnKeyListener(textListener);
//    }
//
//    private final View.OnKeyListener textListener = new View.OnKeyListener() {
//        @Override
//        public boolean onKey(View view, int keyCode, KeyEvent event) {
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
//                String text = ((TextView) view).getText().toString();
//                if (text != null && !text.isEmpty()) {
//                    Bitmap bitmap = encoder.encodeAsBitmap(smallerDimension, text);
//                    encoder.saveQRBitmapToFile();
//                    ImageView imageView = (ImageView) findViewById(R.id.image_view);
//                    imageView.setImageBitmap(bitmap);
//
//                }
//                return true;
//            }
//            return false;
//        }
//    };
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        // This assumes the view is full screen, which is a good assumption
//        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        Display display = manager.getDefaultDisplay();
//        Point displaySize = new Point();
//        display.getSize(displaySize);
//        int width = displaySize.x;
//        int height = displaySize.y;
//        smallerDimension = width < height ? width : height;
//        smallerDimension = smallerDimension * 7 / 8;
//
//    }
////
////    private void encode(String text) {
//////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//////        intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
//////        intent.putExtra(Intents.Encode.DATA, text);
//////        intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
////    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_qrencode, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//}
