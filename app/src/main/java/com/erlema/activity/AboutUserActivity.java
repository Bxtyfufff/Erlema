package com.erlema.activity;

import com.erlema.fragment.AboutMeFragment;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import com.example.erlema.R.layout;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class AboutUserActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.activity_about_user);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		String userId = getIntent().getStringExtra("userId");
		MyUtils.showLog("about", "act:" + userId);
		AboutMeFragment aboutMeFragment = AboutMeFragment.newInstance(userId);
		transaction.add(R.id.aboutuser_fragment, aboutMeFragment);
		transaction.show(aboutMeFragment);
		transaction.commit();
	}
}
