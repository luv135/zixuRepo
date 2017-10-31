package com.zigsun.mobile.module;

import com.zigsun.ui.contact.SortModel;

/**
 * Created by Luo on 2015/7/10.
 * 联系人中用户的具体信息显示
 */
public class ContactItem extends SortModel {
    private String email;
    private String tel;
    private String company;
    private String depart;
    private byte status;
    private UserInfoStatus infoStatus;
    private String nickName;

    public UserInfoStatus getInfoStatus() {
        return infoStatus;
    }

    public void setInfoStatus(UserInfoStatus infoStatus) {
        this.infoStatus = infoStatus;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    /**
     *@deprecated use {@linkplain #getInfoStatus()}
     */
    @Deprecated
    public byte getStatus() {
        return status;
    }

    /**
     *@deprecated use {@linkplain #setInfoStatus(UserInfoStatus)}
     */
    @Deprecated
    public void setStatus(byte status) {
        this.status = status;
        switch (status){
            case 0:
                setInfoStatus(UserInfoStatus.OffLine);
                break;
            case 1:
                setInfoStatus(UserInfoStatus.Online);
                break;
            case 2:
                setInfoStatus(UserInfoStatus.InMeeting);
                break;
            case 3:
                setInfoStatus(UserInfoStatus.LeaveMeeting);
                break;
        }
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public String toString() {
        return "ContactItem{" +
                "company='" + company + '\'' +
                ", email='" + email + '\'' +
                ", tel='" + tel + '\'' +
                ", depart='" + depart + '\'' +
                ", status=" + status +
                '}'+super.toString();
    }
}
