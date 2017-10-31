package com.zigsun.mobile.module;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Luo on 2015/6/25.
 */
@DatabaseTable(tableName = "recent")
public class RecenListItem {


    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private long userId;
    @DatabaseField
    private long begin;
    @DatabaseField
    private long end;
    @DatabaseField
    private int status;
    @DatabaseField
    private String people;
    @DatabaseField
    private String ulUserID;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public String getUlUserID() {
        return ulUserID;
    }

    public void setUlUserID(String ulUserID) {
        this.ulUserID = ulUserID;
    }


//    @DatabaseField
//    private String strUserName;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public RecenListItem() {
    }

      public RecenListItem(long begin, long end, String people, int status, String ulUserID) {
        this.begin = begin;
        this.end = end;
        this.people = people;
        this.status = status;
        this.ulUserID = ulUserID;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }



    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    @Override
    public String toString() {
        return "RecenListItem{" +
                "begin=" + begin +
                ", id=" + id +
                ", userId=" + userId +
                ", end=" + end +
                ", status=" + status +
                ", people='" + people + '\'' +
                ", ulUserID='" + ulUserID + '\'' +
                '}';
    }
}
