package com.zigsun.mobile.module;

import android.os.Parcel;

import com.j256.ormlite.field.DatabaseField;
import com.zigsun.bean.UserInfo;

/**
 * Created by Luo on 2015/6/30.
 * 序列化
 */
public class ResultItem extends UserInfo{
    private boolean exist;


    public ResultItem(boolean exist) {
        this.exist = exist;
    }

    public boolean isExist() {
        return exist;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(exist ? (byte) 1 : (byte) 0);
    }

    protected ResultItem(Parcel in) {
        super(in);
        this.exist = in.readByte() != 0;
    }

    public static final Creator<ResultItem> CREATOR = new Creator<ResultItem>() {
        public ResultItem createFromParcel(Parcel source) {
            return new ResultItem(source);
        }

        public ResultItem[] newArray(int size) {
            return new ResultItem[size];
        }
    };
}
