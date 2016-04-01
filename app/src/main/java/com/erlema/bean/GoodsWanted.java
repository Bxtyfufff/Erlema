package com.erlema.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;

public class GoodsWanted extends BmobObject {
    private String title;// 标题
    private String describ;// 描述
    private Integer status = 0;// 0 正常 1 交易完成 2 审核不通过
    private String owerID;// 主人
    private String avator;// 主人图像地址
    private Integer pirce_high;// 最高价格
    private Integer pirce_low;// 最低价格
    private String school;// 学校
    private Integer cate;// 类别
    private BmobGeoPoint location;//发布的位置

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOwerID() {
        return owerID;
    }

    public void setOwerID(String owerID) {
        this.owerID = owerID;
    }

    public void setPirce(Integer low, Integer high) {
        this.setPirce_high(high);
        this.pirce_low = low;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Integer getCate() {
        return cate;
    }

    public void setCate(Integer cate) {
        this.cate = cate;
    }

    public Integer getPirce_low() {
        return pirce_low;
    }

    public void setPirce_low(Integer pirce_low) {
        this.pirce_low = pirce_low;
    }

    public Integer getPirce_high() {
        return pirce_high;
    }

    public void setPirce_high(Integer pirce_high) {
        this.pirce_high = pirce_high;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }
}
