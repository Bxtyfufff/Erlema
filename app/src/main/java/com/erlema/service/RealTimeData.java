package com.erlema.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

public class RealTimeData extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		
	}

}
