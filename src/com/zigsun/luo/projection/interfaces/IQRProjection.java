package com.zigsun.luo.projection.interfaces;

/**
 * Created by Luo on 2015/5/18.
 * 移动投影接口
 */
public interface IQRProjection {
    /**
     * 设置qr 码
     * @param string
     */
    void setQrUrl(String string);

    void disconnect();
}
