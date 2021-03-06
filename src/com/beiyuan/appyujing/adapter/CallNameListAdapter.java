package com.beiyuan.appyujing.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.data.StudentData;

public class CallNameListAdapter extends BaseAdapter {

	private Context context; // 运行上下文
	private List<StudentData> listItems;
	private LayoutInflater listContainer;

	public final class ListItemView {
		// TODO Auto-generated constructor stub
		public TextView name;
		public TextView num;
		public RadioButton attend;
		public RadioButton absence;
		public RadioButton asking;
		public RadioButton late;
		public RadioGroup buttonGroup;
	}

	public CallNameListAdapter(Context context, List<StudentData> listItems) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.listItems = listItems;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView = null;
//		if (convertView == null) {
			listItemView = new ListItemView();
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.button_change, null);
			// 获取控件对象
			listItemView.name = (TextView) convertView
					.findViewById(R.id.studentname);
			listItemView.num = (TextView) convertView
					.findViewById(R.id.studentid);
			listItemView.buttonGroup = (RadioGroup) convertView
					.findViewById(R.id.button_group);
			listItemView.attend = (RadioButton) convertView
					.findViewById(R.id.button_one);
			listItemView.absence = (RadioButton) convertView
					.findViewById(R.id.button_two);
			listItemView.asking = (RadioButton) convertView
					.findViewById(R.id.button_three);
			listItemView.late = (RadioButton) convertView
					.findViewById(R.id.button_four);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
//		} else {
//			listItemView = (ListItemView) convertView.getTag();
//		}

		listItemView.name.setText((String) listItems.get(position).getName());
		listItemView.num.setText((String) listItems.get(position).getNumber());
		
		if(listItems.get(position).getStatus().equals("请假")){
		listItemView.buttonGroup.check(R.id.button_four);
		}else if(listItems.get(position).getStatus().equals("旷课")){
			listItemView.buttonGroup.check(R.id.button_two);
		}else if(listItems.get(position).getStatus().equals("迟到")){
			listItemView.buttonGroup.check(R.id.button_three);
		}else{
			listItemView.buttonGroup.check(R.id.button_one);
		}
		
		listItemView.buttonGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == R.id.button_one) {
							
							listItems.get(position).setStatus("正常");
						} else if (checkedId == R.id.button_two) {
							
							listItems.get(position).setStatus("旷课");
						} else if (checkedId == R.id.button_three) {
							
							listItems.get(position).setStatus("迟到");
						} else {
							
							listItems.get(position).setStatus("请假");
						}
					}
				});
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
		
		this.listItems = list;
		notifyDataSetChanged();
	}

}
