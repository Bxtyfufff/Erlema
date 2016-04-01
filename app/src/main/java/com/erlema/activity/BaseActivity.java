package com.erlema.activity;

import com.erlema.config.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import de.greenrobot.event.EventBus;

/**
 * BaseActivity.java
 * 基类
 * @author bxtyfufff
 * @version 2015-7-27下午10:26:59
 */
public abstract class BaseActivity extends AppCompatActivity {

	protected int mScreenWidth;
	protected int mScreenHeight;
	
	public static final String TAG = "bmob";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
		Bmob.initialize(this, Constants.Bmob_APPID);
		//获取当前屏幕宽高
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		
		setContentView();
		initViews();
		initListeners();
		initData();
	}
	/**
	 * 设置布局文件
	 */
	public abstract void setContentView();

	/**
	 * 初始化布局文件中的控件
	 */
	public abstract void initViews();

	/**
	 * 初始化控件的监听
	 */
	public abstract void initListeners();
	
	/** 进行数据初始化
	  * initData
	  */
	public abstract void initData();
	
	
	public void ToActivity(Class<? extends Activity> activity){
		Intent intent=new Intent(this,activity);
		startActivity(intent);
	}
	Toast mToast;

	public void ShowToast(String text) {
		if (!TextUtils.isEmpty(text)) {
			if (mToast == null) {
				mToast = Toast.makeText(getApplicationContext(), text,
						Toast.LENGTH_SHORT);
			} else {
				mToast.setText(text);
			}
			mToast.show();
		}
	}
	
	/** 获取当前状态栏的高度
	  * getStateBar
	  * @Title: getStateBar
	  * @throws
	  */
	public  int getStateBar(){
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}
	
	public static int dip2px(Context context,float dipValue){
		float scale=context.getResources().getDisplayMetrics().density;		
		return (int) (scale*dipValue+0.5f);		
	}
	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(Boolean empty){

	}
	public void startActivity(Class<? extends Activity> target, Bundle bundle,boolean finish) {
		Intent intent = new Intent();
		intent.setClass(this, target);
		if (bundle != null)
			intent.putExtra(getPackageName(), bundle);
		startActivity(intent);
		if (finish)
			finish();
	}
	public Bundle getBundle() {
		if (getIntent() != null && getIntent().hasExtra(getPackageName()))
			return getIntent().getBundleExtra(getPackageName());
		else
			return null;
	}
}
