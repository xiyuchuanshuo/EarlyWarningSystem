package com.beiyuan.appyujing.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.data.StudentData;

public class QueryNameListAdapter extends BaseAdapter {
	private Context context; // 运行上下文
	private List<StudentData> listItems;
	private LayoutInflater listContainer;

	public final class ListItemView {
		// TODO Auto-generated constructor stub

		public TextView name;
		public TextView num;
		public TextView status;

	}

	public QueryNameListAdapter(Context context, List<StudentData> listItems) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.listItems = listItems;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
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
		if (convertView == null) {
			listItemView = new ListItemView();
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.query_name_item, null);
			// 获取控件对象
			listItemView.name = (TextView) convertView
					.findViewById(R.id.name_item);
			listItemView.num = (TextView) convertView
					.findViewById(R.id.num_item);
			listItemView.status = (TextView) convertView
					.findViewById(R.id.status_item);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		listItemView.name.setText((String) listItems.get(position).getName());
		listItemView.num.setText((String) listItems.get(position).getNumber());
		listItemView.status.setText((String) listItems.get(position)
				.getStatus());
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
