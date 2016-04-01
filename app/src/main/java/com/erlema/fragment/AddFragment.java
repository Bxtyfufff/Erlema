package com.erlema.fragment;

import cn.bmob.v3.BmobUser;

import com.erlema.activity.LoginActivity;
import com.example.erlema.R;
import com.gc.materialdesign.widgets.Dialog;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AddFragment extends BaseFragment {
	private RecyclerView mytopicview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.add_layout, container, false);

		return rootview;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			LoginOut();
		}
	}

	private void LoginOut() {
		Dialog d = new Dialog(getActivity(), null, "是否注销当前用户?");
		d.addCancelButton("取消");
		d.setOnAcceptButtonClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BmobUser.logOut(getActivity());
				Intent login=new Intent(getActivity(),LoginActivity.class);
				startActivity(login);
				getActivity().finish();
			}
		});
		d.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}

}
