package com.erlema.fragment;

import cn.bmob.v3.Bmob;

import com.erlema.config.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Bmob.initialize(getActivity(), Constants.Bmob_APPID);
		initViews();
		initListeners();
		initData();
	}
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
	
	Toast mToast;
	public void ShowToast(String text) {
		if (!TextUtils.isEmpty(text)) {
			if (mToast == null) {
				mToast = Toast.makeText(getActivity(), text,
						Toast.LENGTH_SHORT);
			} else {
				mToast.setText(text);
			}
			mToast.show();
		}
	}

	public void startActivity(Class<? extends Activity> target, Bundle bundle,boolean finish) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), target);
		if (bundle != null)
			intent.putExtra(getActivity().getPackageName(), bundle);
		startActivity(intent);
		if (finish)
			getActivity().finish();
	}
}
