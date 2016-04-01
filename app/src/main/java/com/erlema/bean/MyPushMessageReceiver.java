package com.erlema.bean;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

import com.erlema.BmobIMApplication;
import com.erlema.activity.GoodsDetailsActivity;
import com.erlema.activity.MainActivity;
import com.erlema.activity.WantedGoodsDetailsActivity;
import com.erlema.config.Constants;
import com.erlema.util.MyUtils;
import com.example.erlema.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat.Builder;

public class MyPushMessageReceiver extends BroadcastReceiver {
public static int notifyCount;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		MainActivity.ShowRedPoint(3);
		String js = intent
				.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
		String text = "";
		int action=0;
		JSONObject jsonObject = null;
		try {
			jsonObject=new JSONObject(js);
			text=jsonObject.getString("alert");
			action=jsonObject.getInt("action");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//调转至消息中心的
		if (action== Constants.ACTION_MESSAGECENTER){
			Intent in = new Intent();
			in.setClass(context, MainActivity.class);
			in.putExtra("isPush",true);
			PendingIntent pi = PendingIntent.getActivity(context, 99, in, 99);
			Builder mBuilder = new Builder(context)
					.setContentTitle("您有新的消息").setContentText(text)
					.setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
					.setContentIntent(pi).setSmallIcon(R.drawable.ic_launcher);
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notifyCount, mBuilder.build());
			notifyCount++;
		}else if (action==Constants.ACTION_GOODSDETAIL){//调转至物品详情的
			Intent in = new Intent();
			in.setClass(context, GoodsDetailsActivity.class);
			in.putExtra("isPush",true);
			try {
				in.putExtra("goodsId", jsonObject.getString("goodsId"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			PendingIntent pi = PendingIntent.getActivity(context, 99, in, 99);
			Builder mBuilder = new Builder(context)
					.setContentTitle("评论回复").setContentText(text)
					.setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
					.setContentIntent(pi).setSmallIcon(R.drawable.ic_launcher);
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notifyCount, mBuilder.build());
			notifyCount++;
		}else if (action==Constants.ACTION_WGOODSDETAIL){
			Intent in = new Intent();
			in.setClass(context, WantedGoodsDetailsActivity.class);
			in.putExtra("isPush",true);
			try {
				in.putExtra("goodsId", jsonObject.getString("goodsId"));
				MyUtils.showLog("ttt", jsonObject.getString("goodsId"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			PendingIntent pi = PendingIntent.getActivity(context, 99, in, 99);
			Builder mBuilder = new Builder(context)
					.setContentTitle("评论回复").setContentText(text)
					.setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
					.setContentIntent(pi).setSmallIcon(R.drawable.ic_launcher);
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notifyCount, mBuilder.build());
			notifyCount++;
		}


	}

}