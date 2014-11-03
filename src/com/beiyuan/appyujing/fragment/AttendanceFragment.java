package com.beiyuan.appyujing.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beiyuan.appyujing.HeadteacherActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.TeacherActivity;
import com.beiyuan.appyujing.activity.CallNameActivity;
import com.beiyuan.appyujing.activity.QueryNameActivity;
import com.beiyuan.appyujing.activity.QuickQueryNameActivity;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.view.DragLayout;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;

public class AttendanceFragment extends Fragment {

	private TitleView mTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View parentView = inflater.inflate(R.layout.fragment_copy, container,
				false);
		initView(parentView);
		return parentView;
	}
	private DragLayout dl;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mTitle = (TitleView) getActivity().findViewById(R.id.title);
		mTitle.setTitle(R.string.attend_title);
		Drawable leftImg = getResources().getDrawable(R.drawable.side_btn_img);
		if (GlobalData.getRole().trim().equals("Role_Teacher")) {
			dl = TeacherActivity.dl;
		}else if (GlobalData.getRole().trim().equals("Role_Student")) {
			dl = StudentActivity.dl;
		}else{
			dl = HeadteacherActivity.dl;
		}
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				dl.open();
			}

		});
	}

	public void initView(View view) {

		ListView listView = (ListView) view.findViewById(R.id.list_attend);
		listView.setAdapter(new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_expandable_list_item_1, getData()));
		listView.setOnItemClickListener(onItemClickListener);

	}

	private List<String> getData() {
		List<String> data = new ArrayList<String>();
		data.add("点名考勤");
		data.add("快速查看");
		data.add("考勤查询");
		return data;
	}

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i,
				long l) {
			Intent intent = new Intent();
			switch (i) {
			case 0:
				intent.setClass(getActivity(), CallNameActivity.class);
				startActivity(intent);
				break;
			case 1:
				intent.setClass(getActivity(), QuickQueryNameActivity.class);
				startActivity(intent);
				break;
			case 2:
				intent.setClass(getActivity(), QueryNameActivity.class);
				startActivity(intent);
				break;
			}
		}
	};

}
