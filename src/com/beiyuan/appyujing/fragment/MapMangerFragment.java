package com.beiyuan.appyujing.fragment;

import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.view.MapGetStudent;

public class MapMangerFragment extends Fragment {
	//
	private MyAdapter mAdapter;
	private ViewPager mPager;
	private TextView tv1, tv2;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	boolean isFirst = true;
	private Resources resources;
	private ImageView ivBottomLine;
	private int bottomLineWidth;
	private int position_one;
	private int position_two;
	private int position_three;
	private int currIndex = 0;
	Animation animation = null;
	String role = GlobalData.getRole();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.mapmanager, null);
		tv1 = (TextView) mView.findViewById(R.id.map_tv1);
		tv2 = (TextView) mView.findViewById(R.id.map_tv2);

		resources = getResources();
		if (isFirst) {
			tv1.setTextColor(resources.getColor(R.color.blue));
		}
		mPager = (ViewPager) mView.findViewById(R.id.map_pager);
		tv1.setOnClickListener(new MyOnClickListener(0));
		tv2.setOnClickListener(new MyOnClickListener(1));
		GaodeMapFragment page1 = new GaodeMapFragment();
		role = "学生";
		if (role.equals("学生")) {
			// MapSendMessage page2 = new MapSendMessage();
			// pagerItemList.clear();
			// pagerItemList.add(page1);
			// pagerItemList.add(page2);
		} else {
			MapGetStudent page2 = new MapGetStudent();
			pagerItemList.clear();
			pagerItemList.add(page1);
			pagerItemList.add(page2);
		}

		mAdapter = new MyAdapter(getChildFragmentManager());
		mPager.setAdapter(null);
		mPager.removeAllViews();
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

				if (myPageChangeListener != null)
					myPageChangeListener.onPageSelected(arg0);
				switch (arg0) {
				case 0:
					if (currIndex == 1) {
						animation = new TranslateAnimation(position_one, 0, 0,
								0);
						tv2.setTextColor(resources.getColor(R.color.lightwhite));
					}
					tv1.setTextColor(resources.getColor(R.color.blue));
					isFirst = true;

					break;
				case 1:
					if (currIndex == 0) {
						animation = new TranslateAnimation(0, position_one, 0,
								0);
						tv1.setTextColor(resources.getColor(R.color.lightwhite));
					}
					tv2.setTextColor(resources.getColor(R.color.blue));
					isFirst = false;
					break;
				}

				currIndex = arg0;
				animation.setFillAfter(true);// 图片停在动画结束位置
				animation.setDuration(300);
				ivBottomLine.startAnimation(animation);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int position) {

			}
		});
		InitWidth(mView);
		return mView;
	}

	/**
	 * 初始化滑动的位置和图标
	 * 
	 * @param view
	 */
	private void InitWidth(View view) {

		ivBottomLine = (ImageView) view.findViewById(R.id.map_iv);
		bottomLineWidth = ivBottomLine.getLayoutParams().width;
		// 获取手机分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;

		position_one = (int) (screenW / 2.0);
		position_two = position_one * 2;
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	// @Override
	// public void onDestroyView() {
	// // TODO Auto-generated method stub
	// // ((FragmentPagerAdapter)mViewPager.getAdapter()).
	// mPager.setAdapter(null);
	// mPager.removeAllViews();
	// super.onDestroyView();
	// }

	// public boolean isFirst() {
	// if (mPager.getCurrentItem() == 0)
	// return true;
	// else
	// return false;
	// }

	// public boolean isEnd() {
	// if (mPager.getCurrentItem() == pagerItemList.size() - 1)
	// return true;
	// else
	// return false;
	// }

	public class MyAdapter extends FragmentPagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return pagerItemList.size();
		}

		@Override
		public Fragment getItem(int position) {

			Fragment fragment = null;
			if (position < pagerItemList.size())
				fragment = pagerItemList.get(position);
			else
				fragment = pagerItemList.get(0);

			return fragment;

		}
	}

	private MyPageChangeListener myPageChangeListener;

	public void setMyPageChangeListener(MyPageChangeListener l) {

		myPageChangeListener = l;

	}

	public interface MyPageChangeListener {
		public void onPageSelected(int position);
	}

}
