package com.zigsun.mobile.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

/**
 * Created by Luo on 2015/7/16.
 */
public class TextNumberMatcher {
    private String text;

    /**
     *
     * @param color highlight color.
     */
    public TextNumberMatcher(int color) {
        this.color = color;
    }

    private int color;

    /**
     *
     * @param text need highlight text.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 从name中找 {@linkplain #setText(String)}设置的文本,然后高亮
     */
    public SpannableStringBuilder getColorText(String name) {
        SpannableStringBuilder builder = new SpannableStringBuilder(name);


        final int i1 = name.indexOf(text);
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        builder.setSpan(span, i1, i1 + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



//
//        for (int i = 0, j = 0; i < text.length() && j < name.length(); j++) {
//            if (text.charAt(i) == name.charAt(j)) {
//                ForegroundColorSpan span = new ForegroundColorSpan(color);
//                builder.setSpan(span, j, j + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                i++;
//            }
//        }
        return builder;
    }
}
