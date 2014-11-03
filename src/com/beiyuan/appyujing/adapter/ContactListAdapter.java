/**
 * 
 */
package com.beiyuan.appyujing.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.tools.ContactItemData;
import com.beiyuan.appyujing.tools.ContactItemView;
import com.beiyuan.appyujing.tools.ContactItemView.OnContactItemSelectedListener;

public class ContactListAdapter extends BaseAdapter {

	private Context context;
	private List<ContactItemData> contactDataList;
	private ContactItemView.OnContactItemSelectedListener listener;

	public ContactListAdapter(Context context,
			List<ContactItemData> contactDataList,
			OnContactItemSelectedListener listener) {
		if (contactDataList == null) {
			contactDataList = new ArrayList<ContactItemData>();
		}
		this.contactDataList = contactDataList;
		this.listener = listener;
		this.context = context;
	}

	@Override
	public int getCount() {
		return contactDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return contactDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactItemViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ContactItemViewHolder();
			convertView = new ContactItemView(context);
			viewHolder.itemView = (ContactItemView) convertView;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ContactItemViewHolder) convertView.getTag();
		}
		ContactItemData itemData = contactDataList.get(position);

		ContactItemView itemView = viewHolder.itemView;
		itemView.setOnContactItemSelectedListener(listener);
		itemView.setItemId(position);

		itemView.getName().setText(itemData.getName());
		itemView.getNumber().setText(itemData.getNumber());

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			TextView headView = (TextView) itemView.getItemHeadLayout()
					.getChildAt(0);
			headView.setVisibility(View.VISIBLE);
			headView.setText(itemData.getSortLetters());
			itemView.getItemTopSep().setVisibility(View.GONE);
		} else {
			itemView.getItemHeadLayout().setVisibility(View.GONE);
			itemView.getItemTopSep().setVisibility(View.VISIBLE);
		}
		if (position == getCount() - 1) {// 如果是最后一个
			itemView.getItemBottomSep().setVisibility(View.VISIBLE);
		} else {
			itemView.getItemBottomSep().setVisibility(View.GONE);
		}

		itemView.setBackgroundResource(R.drawable.contact_item_bg);

		return itemView;
	}

	class ContactItemViewHolder {
		ContactItemView itemView;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<ContactItemData> list) {
		if (list == null) {
			list = new ArrayList<ContactItemData>();
		}
		this.contactDataList = list;
		notifyDataSetChanged();
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return contactDataList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = contactDataList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase(Locale.CHINA).charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}
}