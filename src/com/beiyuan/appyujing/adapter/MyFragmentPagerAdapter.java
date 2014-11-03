package com.beiyuan.appyujing.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragmentsList;

	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public MyFragmentPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments) {
		super(fm);
		this.fragmentsList = fragments;
	}

	@Override
	public int getCount() {
		return fragmentsList.size();
	}

	@Override
	public Fragment getItem(int position) {
		// return fragmentsList.get(arg0);
		Fragment fragment = null;
		if (position < fragmentsList.size())
			fragment = fragmentsList.get(position);
		else
			fragment = fragmentsList.get(0);

		return fragment;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

}
