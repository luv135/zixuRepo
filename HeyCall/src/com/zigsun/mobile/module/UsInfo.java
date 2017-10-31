package com.zigsun.mobile.module;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Luo on 2015/7/7.
 */
public class UsInfo implements Parcelable {
    private String tel;
    private long userId;

    public UsInfo(String tel, long userId) {
        this.tel = tel;
        this.userId = userId;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tel);
        dest.writeLong(this.userId);
    }

    protected UsInfo(Parcel in) {
        this.tel = in.readString();
        this.userId = in.readLong();
    }

    public static final Parcelable.Creator<UsInfo> CREATOR = new Parcelable.Creator<UsInfo>() {
        public UsInfo createFromParcel(Parcel source) {
            return new UsInfo(source);
        }

        public UsInfo[] newArray(int size) {
            return new UsInfo[size];
        }
    };
}
