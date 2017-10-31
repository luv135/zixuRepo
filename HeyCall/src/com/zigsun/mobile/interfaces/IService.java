package com.zigsun.mobile.interfaces;

/**
 * Created by Luo on 2015/6/17.
 *
 * <pre>
 * 在Application 中初始化jni
 * notify = new C2SNotifyImpl(EMeetingApplication.getHandler());
 * hMgr = ClientSessMgr.CSMCreateEMClient(notify,
 *                      EMeetingApplication.getContext());
 * </pre>
 */
public interface IService {
    /**
     * 服务器初始化
     */
    void initService(String ip, String port);

    /**
     * 初始化回调
     */
    interface ServiceListener {
        void serviceConnected();

        void serviceDisconnected();
    }

}
