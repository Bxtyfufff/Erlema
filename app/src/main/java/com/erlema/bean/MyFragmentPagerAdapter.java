package com.erlema.bean;

import java.util.ArrayList;

import com.erlema.fragment.GoodsAboutMeFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	ArrayList<Fragment> list;

	public MyFragmentPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragmentList) {
		super(fm);
		this.list = fragmentList;
	}

	@Override
	public Fragment getItem(int position) {
		 return list.get(position);
	}

	@Override
	public int getCount() {
		return list.size();
	}

}
