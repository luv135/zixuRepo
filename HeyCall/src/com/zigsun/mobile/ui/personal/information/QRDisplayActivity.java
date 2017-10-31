package com.zigsun.mobile.ui.personal.information;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.ImageView;

import com.zigsun.luo.projection.camera.QRCodeEncoder;
import com.zigsun.luo.projection.utils.Util;
import com.zigsun.mobile.R;
import com.zigsun.mobile.ui.base.Activity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QRDisplayActivity extends Activity {

    public static final String QRSTRING = "QRDispalyActivity.QRSITNG";
    @InjectView(R.id.qrImage)
    ImageView qrImage;
    private QRCodeEncoder encoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_qrdispaly);
        ButterKnife.inject(this);
        encoder = new QRCodeEncoder();
        final String string = getIntent().getStringExtra(QRSTRING);
        Point displaySize = Util.getScreenResolution(this);
        int width = displaySize.x;
        int height = displaySize.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 5 / 8;
        Bitmap bitmap = encoder.encodeAsBitmap(smallerDimension, string);
        qrImage.setImageBitmap(bitmap);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_qrdispaly, menu);
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
}
