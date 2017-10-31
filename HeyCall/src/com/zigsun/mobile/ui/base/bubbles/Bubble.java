package com.zigsun.mobile.ui.base.bubbles;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.zigsun.mobile.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/7/8.
 */
public class Bubble extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = Bubble.class.getSimpleName();
    @InjectView(R.id.startClickButton)
    BubbleLayout startClickButton;
    @InjectView(R.id.toAcceptButton)
    BubbleTrashLayout toAcceptButton;
    @InjectView(R.id.toMessageButton)
    BubbleTrashLayout toMessageButton;
    @InjectView(R.id.rejectButton)
    BubbleTrashLayout rejectButton;

    public Bubble(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.abc_bubble, this);
        ButterKnife.inject(this);
        ArrayList<BubbleTrashLayout> arr = new ArrayList<>();
        arr.add(toAcceptButton);
//        arr.add(toMessageButton);
        arr.add(rejectButton);
        BubblesLayoutCoordinator layoutCoordinator = new BubblesLayoutCoordinator.Builder()
                .setTrashViews(arr)
                .build();
        startClickButton.setLayoutCoordinator(layoutCoordinator);

    }

    public Bubble activateAcceptListener() {
        toAcceptButton.setOnClickListener(this);
        return this;
    }

    public Bubble activateRejectListener() {
        rejectButton.setOnClickListener(this);
        return this;

    }

    public Bubble activateMessageListener() {
        toMessageButton.setOnClickListener(this);
        return this;
    }

    public void setActivateListener(ActionListener listener) {
        this.listener = listener;
    }

    private ActionListener listener;

    public interface ActionListener {
        void acceptCall();

        void rejectCall();

        void messageCall();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toAcceptButton:
                setVisibility(GONE);
                Log.d(TAG, "TOACC");
                if (listener != null)
                    listener.acceptCall();
                break;
            case R.id.toMessageButton:
                Log.d(TAG, "TOMESG");
                if (listener != null)
                    listener.messageCall();
                setVisibility(GONE);
                break;
            case R.id.rejectButton:
                setVisibility(GONE);
                Log.d(TAG, "REJECT");
                if (listener != null)
                    listener.rejectCall();
                break;

        }
    }
}
