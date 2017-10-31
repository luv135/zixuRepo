package com.zigsun.mobile.module;

/**
 * Created by Luo on 2015/6/17.
 */
public class MeListItem {
    public Type type;
    public int image;
    public int title;
    public int qr;  //Header

    public MeListItem(Type type, int image, int title, int qr) {
        this(type, image, title);
        this.qr = qr;
    }

    public MeListItem(Type type, int image, int title) {
        this.image = image;
        this.title = title;
        this.type = type;
    }

    public MeListItem() {
        this.type = Type.Space;
    }

    public enum Type {
        Space, Info, Header
    }
}
