package com.zigsun.luo.projection.interfaces;

/**
 * Created by Luo on 2015/6/6.
 */
public interface IShareInterface {
    void openScreenShare();

    void closeScreenShare();

    /**
     * @return {@code true} Sharing, {@code false} otherwise
     */
    boolean screenIsShare();
}
