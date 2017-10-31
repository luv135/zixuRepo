package com.zigsun.mobile.utils;

import com.zigsun.mobile.model.ContactsModel;

/**
 * Created by Luo on 2015/7/16.
 */
public class NickCheckHelper {

    public NickCheckHelper() {
        this.contactsModel = new ContactsModel();
    }

    private ContactsModel contactsModel;

    /**
     *
     * @param userName 用户名,注册的手机号码
     * @return 昵称,显示的名称
     */
    public String getNickNameByUserNumber(String userName) {
        return contactsModel.qureyName(userName);
    }
}
