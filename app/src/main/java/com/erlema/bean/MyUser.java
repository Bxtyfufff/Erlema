package com.erlema.bean;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;

import com.erlema.activity.GoodsDetailsActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * MyUser.java 用户类
 *
 * @author bxtyfufff
 * @version 2015-7-27下午10:27:59
 */
public class MyUser extends BmobUser {
    private String gender;// 性别
    private String name;// 实名
    private BmobGeoPoint geopoint;// 地理位置
    private String school;// 学校
    private String city;// 城市
    private String user_ico;// 头像
    private Integer credibility;// 信用度
    private String Statement;//个人简介
    private String auth_img;//学生证照片地址
    private Boolean Authed=false;//学生认证
    private String installationID;//设备ID
    private List<String> collection;//点赞的历史纪录
    private List<String> buys;//点赞的历史纪录

    public MyUser() {
        super();
        this.collection = new ArrayList<String>();
        this.buys = new ArrayList<String>();
        this.credibility = Integer.valueOf(100);
    }

    public void setBuys(List<String> buys) {
        this.buys = buys;
    }

    public List<String> getBuys() {
        return buys;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getrelName() {
        return name;
    }

    public void setrelName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public BmobGeoPoint getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(BmobGeoPoint geopoint) {
        this.geopoint = geopoint;
    }

    public String getAvator() {
        return user_ico;
    }

    public void setAvator(String user_ico) {
        this.user_ico = user_ico;
    }

    @Override
    public String toString() {
        return "MyUser [gender=" + gender + ", relName=" + name
                + ", geopoint=" + geopoint + ", school=" + school + ", city="
                + city + ", user_ico=" + user_ico + "]";
    }

    public List<String> getCollection() {
        return collection;
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }

    public void addCollection(String goodId) {
        boolean had = false;
        for (String id : collection) {
            if (id.equals(goodId)) {
                had = true;
                break;
            }
        }
        if (!had) {
            collection.add(goodId);
        }
    }

    public void addBuys(String goodId) {
        boolean had = false;
        for (String id : buys) {
            if (id.equals(goodId)) {
                had = true;
                break;
            }
        }
        if (!had) {
            buys.add(goodId);
        }
    }

    public void removeCollection(String goodId) {
        collection.remove(goodId);
    }

    public void removeBuys(String goodId) {
        buys.remove(goodId);
    }

    public Integer getCredibility() {
        return credibility;
    }

    public void setCredibility(Integer credibility) {
        this.credibility = credibility;
    }

    public Boolean getAuthed() {
        return Authed;
    }

    public void setAuthed(Boolean authed) {
        Authed = authed;
    }

    public String getAuth_img() {
        return auth_img;
    }

    public void setAuth_img(String auth_img) {
        this.auth_img = auth_img;
    }

    public String getInstallationID() {
        return installationID;
    }

    public void setInstallationID(String installationID) {
        this.installationID = installationID;
    }

    public String getStatement() {
        return Statement;
    }

    public void setStatement(String statement) {
        Statement = statement;
    }
}
