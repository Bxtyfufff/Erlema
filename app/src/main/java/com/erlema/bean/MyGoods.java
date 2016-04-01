/**
 * MyGoods.java
 * ErLeMa
 *
 * @author 作者：bxtyfufff
 * @version 创建时间：2015-7-27 下午10:28:58
 * 类说明
 */

package com.erlema.bean;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * MyGoods.java 物品类
 *
 * @author bxtyfufff
 * @version 2015-7-27下午10:28:58
 */
public class MyGoods extends BmobObject {
    private String title;// 标题
    private String describ;// 描述
    private Integer degree = 0;// 新旧程度（10）
    /**
     *  0 正常 1 审核不通过  2卖家交易完成 3买家交易完成
     */
    private Integer status = 0;
    private List<String> tags = new ArrayList<String>();// 标签
    private List<String> wantBuyers = new ArrayList<>();//想买的用户列表
    private String buyer;//购买的用户
    private String owerID;// 主人
    private double pirce;// 价格
    private String school;// 学校
    private Integer cate;// 类别
    private String[] imgUrl;// 图片数组
    private Integer clickCount = 0;// 点击数
    private Integer starCount = 0;// 收藏数

    public Integer getClickCount() {
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }

    public Integer getStarCount() {
        return starCount;
    }

    public void setStarCount(Integer starCount) {
        this.starCount = starCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPirce() {
        return pirce;
    }

    public void setPirce(double pirce) {
        this.pirce = pirce;
    }

    public Integer getCate() {
        return cate;
    }

    public void setCate(Integer cate) {
        this.cate = cate;
    }

    public String[] getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String[] imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public String getOwerID() {
        return owerID;
    }

    public void setOwerID(String owerID) {
        this.owerID = owerID;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public List<String> getWantBuyers() {
        return wantBuyers;
    }

    public void addBuyer(String id) {
        boolean had = false;
        for (String buyer : wantBuyers) {
            if (buyer.equals(id)) {
                had = true;
                break;
            }
        }
        if (!had) {
            this.wantBuyers.add(id);
        }
    }

    public void setWantBuyers(List<String> wantBuyers) {
        this.wantBuyers = wantBuyers;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

}
