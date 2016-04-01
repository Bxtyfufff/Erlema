package com.erlema.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Bxtyfufff on 2016/3/18 0018.
 */
public class MySubject extends BmobObject {
    private String img_url;
    private String link_url;
    private int action;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
