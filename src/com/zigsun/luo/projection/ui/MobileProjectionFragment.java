package com.zigsun.luo.projection.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.base.MainTabBaseFragment;
import com.zigsun.luo.projection.camera.QRCodeEncoder;
import com.zigsun.luo.projection.interfaces.IQRProjection;


/**
 * A simple {@link Fragment} subclass.
 */
public class MobileProjectionFragment extends MainTabBaseFragment implements IQRProjection {


    private static final String TAG = MobileProjectionFragment.class.getSimpleName();
    private ImageView qrImgeView;
    private QRCodeEncoder encoder;
    private int smallerDimension;
    private View qrLayout;
    private View connectedLayout;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            disconnect();
        }
    };

    public MobileProjectionFragment() {
        // Required empty public constructor
        encoder = new QRCodeEncoder();
    }

    @Override
    public String getFlagTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int width = displaySize.x;
        int height = displaySize.y;
        smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 5 / 8;

        setQrUrl("http://www.baidu.com");

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        qrImgeView = (ImageView) view.findViewById(R.id.iv_qrcode);
        qrLayout = view.findViewById(R.id.qr_layout);
        connectedLayout = view.findViewById(R.id.connected_layout);
        simulateConnect();
        view.findViewById(R.id.bt_disconnect).setOnClickListener(onClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    /**
     * 模拟连接
     */
    private void simulateConnect() {
        connectedLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                qrLayout.setVisibility(View.INVISIBLE);
                connectedLayout.setVisibility(View.VISIBLE);
            }
        }, 5000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobile_projection, container, false);
    }


    @Override
    public void setQrUrl(String string) {
        Bitmap bitmap = encoder.encodeAsBitmap(smallerDimension, string);
        qrImgeView.setImageBitmap(bitmap);
    }

    @Override
    public void disconnect() {
        qrLayout.setVisibility(View.VISIBLE);
        connectedLayout.setVisibility(View.INVISIBLE);
        simulateConnect();
    }
}
