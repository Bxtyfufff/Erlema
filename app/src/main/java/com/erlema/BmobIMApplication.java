package com.erlema;

import android.app.Application;


import cn.bmob.newim.BmobIM;

public class BmobIMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //im初始化
        BmobIM.init(this);
    }
}