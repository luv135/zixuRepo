package com.zigsun.mobile.ui.base;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zigsun.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/24.
 */
public class SelectEditText extends RelativeLayout implements View.OnKeyListener, View.OnClickListener {
    private static final String TAG = SelectEditText.class.getSimpleName();
    @InjectView(R.id.selectedContainer)
    LinearLayout selectedContainer;
    private int padingLeft;

    public EditText getEditText() {
        return outEditText;
    }

    @InjectView(R.id.outEditText)
    EditText outEditText;
    @InjectView(R.id.hr)
    HorizontalScrollView hr;
    @InjectView(R.id.containerMask)
    FrameLayout containerMask;

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.select_edit_layout, this);
        ButterKnife.inject(this);
        outEditText.setOnKeyListener(this);
        padingLeft = getContext().getResources().getDimensionPixelSize(R.dimen.abc_base_gap);

    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    private ChangeListener listener;
    public interface ChangeListener {
        void itemRemoved(View view);

    }

    public SelectEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public void addItem(View view) {
        view.setOnClickListener(this);
        final int dimension = getContext().getResources().getDimensionPixelSize(R.dimen.abc_contact_head_image_size);
        padingLeft = getContext().getResources().getDimensionPixelSize(R.dimen.abc_base_gap);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dimension,dimension);

        selectedContainer.addView(view,params);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        Log.d(TAG, "onLayout");
        int left = (containerMask.getRight() > hr.getRight() ? hr.getRight() : containerMask.getRight());
        outEditText.setLeft(left+padingLeft);
    }


    public void removeItem(View view) {
        selectedContainer.removeView(view);
        if (listener != null) {
            listener.itemRemoved(view);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (KeyEvent.ACTION_UP == event.getAction() && keyCode == KeyEvent.KEYCODE_DEL) {
            Log.d(TAG, "onKeyDown() -ACTION_UP- length=" + outEditText.getText().length());
//            if (outEditText.getText().length() == 0) {
//                removeItem();
//                return true;
//            }

            EditText editText = outEditText;
            if (editText != null) {
                final Object tag = editText.getTag();
                if (tag != null && ((Boolean) tag)) {
                    editText.setTag(false);
                    removeItem();
                    return true;
                }
                if (editText.getText().length() == 0) {
                    editText.setTag(true);
                }
            }


        }
        return false;
    }

    private void removeItem() {
        final int childCount = selectedContainer.getChildCount();
        if (childCount > 0) {
            removeItem(selectedContainer.getChildAt(childCount - 1));
        }
    }

    @Override
    public void onClick(View v) {
        removeItem(v);

    }
}
