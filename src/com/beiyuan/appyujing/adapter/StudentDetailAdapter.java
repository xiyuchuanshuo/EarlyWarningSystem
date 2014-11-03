package com.beiyuan.appyujing.adapter;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.data.StudentData;

public class StudentDetailAdapter extends BaseAdapter {

	private Context context; // 运行上下文
	private List<StudentData> data;
	private LayoutInflater listContainer;

	public final class ListItemView {
		// TODO Auto-generated constructor stub
		public TextView date;
		public TextView coursename;
		public TextView teachername;
		public TextView number;
		public View view_one;
		public View view_two;

	}

	public StudentDetailAdapter(Context context, List<StudentData> listItems) {
		super();
		this.context = context;
		this.data = listItems;
		this.listContainer = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView = null;

//		if (convertView == null) {
			listItemView = new ListItemView();
			// 获取list_item布局文件的视图
			convertView = listContainer
					.inflate(R.layout.detail_list_item, null);
			// 获取控件对象
			listItemView.date = (TextView) convertView.findViewById(R.id.date);
			listItemView.number = (TextView) convertView
					.findViewById(R.id.number);
			listItemView.coursename = (TextView) convertView
					.findViewById(R.id.coursename);
			listItemView.teachername = (TextView) convertView
					.findViewById(R.id.teachername);
			listItemView.view_one = (View) convertView
					.findViewById(R.id.view_one);
			listItemView.view_two = (View) convertView
					.findViewById(R.id.view_two);

			if (position == 0) {

				listItemView.view_one.setBackgroundColor(android.R.color.white);

			}
			if (position == data.size() - 1) {
				listItemView.view_two.setBackgroundColor(android.R.color.white);
			}

			convertView.setTag(listItemView);
//		} else {
//			listItemView = (ListItemView) convertView.getTag();
//		}

		listItemView.date.setText((String) data.get(position).getDateMonth());
		listItemView.coursename.setText((String) data.get(position)
				.getCourseName());
		listItemView.teachername.setText((String) data.get(position)
				.getTeacherName());

		listItemView.number.setText((String) data.get(position).getDate());
		listItemView.number.setGravity(Gravity.CENTER);

		return convertView;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<StudentData> list) {
		if (list == null) {
			list = new ArrayList<StudentData>();
		}

		this.data = list;
		notifyDataSetChanged();
	}

}
