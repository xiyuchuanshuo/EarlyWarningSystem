package com.beiyuan.appyujing.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.adapter.SortGroupMemberAdapter;
import com.beiyuan.appyujing.data.GroupMemberBean;
import com.beiyuan.appyujing.data.PinyinComparator;
import com.beiyuan.appyujing.database.EmergencyHelper;
import com.beiyuan.appyujing.database.table.ClassMates;
import com.beiyuan.appyujing.database.table.HeadTeacher;
import com.beiyuan.appyujing.database.table.Leader;
import com.beiyuan.appyujing.tools.CharacterParser;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.ClearEditText;
import com.beiyuan.appyujing.view.SideBar;
import com.beiyuan.appyujing.view.SideBar.OnTouchingLetterChangedListener;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;

/**
 * 紧急情况查看所有联系人
 * 
 * @author juan
 * 
 */
public class EmergeContactActivity extends MyActivity implements SectionIndexer {

	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortGroupMemberAdapter adapter;
	private ClearEditText mClearEditText;

	private LinearLayout titleLayout;
	private TextView title;
	private TextView tvNofriends;
	private EmergencyHelper emergencyHelper = new EmergencyHelper(this);
	private TitleView mTitle;
	/**
	 * 上次第一个可见元素，用于滚动时记录标识。
	 */
	private int lastFirstVisibleItem = -1;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<GroupMemberBean> sourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emerge_contact);
		initViews();
	}

	private void initViews() {

		titleLayout = (LinearLayout) findViewById(R.id.title_layout);
		title = (TextView) this.findViewById(R.id.title_layout_catalog);
		tvNofriends = (TextView) this
				.findViewById(R.id.title_layout_no_friends);
		mTitle= (TitleView) findViewById(R.id.title);
		mTitle.setTitle(R.string.emerge_title);
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				Tools.mToast(getApplication(),
						((GroupMemberBean) adapter.getItem(position)).getName());
				String phone = ((GroupMemberBean) adapter.getItem(position))
						.getPhone();
				showManageDialog(manage, phone);
			}
		});

		Intent intent = getIntent();
		String role = intent.getStringExtra("role");
		Log.i("mytag", "role===" + role);

		if (role.equals("leader")) {
			Log.i("mytag", "------------0");
			sourceDateList = getLeaderListItems();
			Log.i("mytag", "------------1");
		} else if (role.equals("teacher")) {
			sourceDateList = getTeacherListItems();
		} else if (role.equals("student")) {
			sourceDateList = getStudentListItems();
		} else {
			sourceDateList = getListItems();
		}

		if (sourceDateList.size() > 1) {
			// 根据a-z进行排序源数据
			Collections.sort(sourceDateList, pinyinComparator);
			adapter = new SortGroupMemberAdapter(this, sourceDateList);
			sortListView.setAdapter(adapter);
			sortListView.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					int section = getSectionForPosition(firstVisibleItem);
					int nextSection = getSectionForPosition(firstVisibleItem + 1);
					int nextSecPosition = getPositionForSection(+nextSection);
					if (firstVisibleItem != lastFirstVisibleItem) {
						MarginLayoutParams params = (MarginLayoutParams) titleLayout
								.getLayoutParams();
						params.topMargin = 0;
						titleLayout.setLayoutParams(params);
						title.setText(sourceDateList.get(
								getPositionForSection(section))
								.getSortLetters());
					}
					if (nextSecPosition == firstVisibleItem + 1) {
						View childView = view.getChildAt(0);
						if (childView != null) {
							int titleHeight = titleLayout.getHeight();
							int bottom = childView.getBottom();
							MarginLayoutParams params = (MarginLayoutParams) titleLayout
									.getLayoutParams();
							if (bottom < titleHeight) {
								float pushedDistance = bottom - titleHeight;
								params.topMargin = (int) pushedDistance;
								titleLayout.setLayoutParams(params);
							} else {
								if (params.topMargin != 0) {
									params.topMargin = 0;
									titleLayout.setLayoutParams(params);
								}
							}
						}
					}
					lastFirstVisibleItem = firstVisibleItem;
				}
			});
		} else {
			adapter = new SortGroupMemberAdapter(this, sourceDateList);
			sortListView.setAdapter(adapter);
		}
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 这个时候不需要挤压效果 就把他隐藏掉
				titleLayout.setVisibility(View.GONE);
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 联系领导初始化
	 */
	private List<GroupMemberBean> getLeaderListItems() {
		List<GroupMemberBean> mSortList = new ArrayList<GroupMemberBean>();
		Cursor cursor_leader = Leader.getLeaderName(emergencyHelper);

		Log.i("mytag", "cursorleader===" + cursor_leader.getCount());
		// 领导
		for (int i = 1; i <= cursor_leader.getCount(); i++) {
			Cursor cursorName = Leader.getLeaderName(emergencyHelper, i);
			GroupMemberBean sortModel = new GroupMemberBean();
			sortModel.setName(cursorName.getString(cursorName
					.getColumnIndex("leadername")));
			sortModel.setPhone(cursorName.getString(cursorName
					.getColumnIndex("leaderphone")));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(cursorName
					.getString(cursorName.getColumnIndex("leadername")));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;
	}

	/**
	 * 联系导员初始化
	 */
	private List<GroupMemberBean> getTeacherListItems() {
		List<GroupMemberBean> mSortList = new ArrayList<GroupMemberBean>();

		Cursor cursor_teacher = HeadTeacher.getTeacherName(emergencyHelper);

		Log.i("mytag", "cursor_teacher===" + cursor_teacher.getCount());
		// 老师
		for (int i = 1; i <= cursor_teacher.getCount(); i++) {
			Cursor cursorName = HeadTeacher.getTeacherName(emergencyHelper, i);
			GroupMemberBean sortModel = new GroupMemberBean();
			sortModel.setName(cursorName.getString(cursorName
					.getColumnIndex("tname")));
			sortModel.setPhone(cursorName.getString(cursorName
					.getColumnIndex("tphone")));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(cursorName
					.getString(cursorName.getColumnIndex("tname")));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;
	}

	/**
	 * 联系同班同学初始化
	 */
	private List<GroupMemberBean> getStudentListItems() {
		List<GroupMemberBean> mSortList = new ArrayList<GroupMemberBean>();

		Cursor cursor_student = ClassMates.getStudentName(emergencyHelper);

		Log.i("mytag", "cursorstudent===" + cursor_student.getCount());
		for (int i = 1; i <= cursor_student.getCount(); i++) {
			Cursor cursorName = ClassMates.getStudentName(emergencyHelper, i);
			GroupMemberBean sortModel = new GroupMemberBean();
			sortModel.setName(cursorName.getString(cursorName
					.getColumnIndex("studentname")));
			sortModel.setPhone(cursorName.getString(cursorName
					.getColumnIndex("studentphone")));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(cursorName
					.getString(cursorName.getColumnIndex("studentname")));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;
	}

	/**
	 * 联系所有人初始化
	 */
	private List<GroupMemberBean> getListItems() {
		List<GroupMemberBean> mSortList = new ArrayList<GroupMemberBean>();

		Cursor cursor_leader = Leader.getLeaderName(emergencyHelper);
		Cursor cursor_teacher = HeadTeacher.getTeacherName(emergencyHelper);
		Cursor cursor_student = ClassMates.getStudentName(emergencyHelper);

		Log.i("mytag", "cursorleader===" + cursor_leader.getCount());
		Log.i("mytag", "cursorteacher===" + cursor_teacher.getCount());
		Log.i("mytag", "cursorstudent===" + cursor_student.getCount());
		// 领导
		for (int i = 1; i <= cursor_leader.getCount(); i++) {
			Cursor cursorName = Leader.getLeaderName(emergencyHelper, i);
			GroupMemberBean sortModel = new GroupMemberBean();
			sortModel.setName(cursorName.getString(cursorName
					.getColumnIndex("leadername")));
			sortModel.setPhone(cursorName.getString(cursorName
					.getColumnIndex("leaderphone")));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(cursorName
					.getString(cursorName.getColumnIndex("leadername")));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		// 老师
		for (int i = 1; i <= cursor_teacher.getCount(); i++) {
			Cursor cursorName = HeadTeacher.getTeacherName(emergencyHelper, i);
			GroupMemberBean sortModel = new GroupMemberBean();
			sortModel.setName(cursorName.getString(cursorName
					.getColumnIndex("tname")));
			sortModel.setPhone(cursorName.getString(cursorName
					.getColumnIndex("tphone")));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(cursorName
					.getString(cursorName.getColumnIndex("tname")));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		// 学生
		for (int i = 1; i <= cursor_student.getCount(); i++) {
			Cursor cursorName = ClassMates.getStudentName(emergencyHelper, i);
			GroupMemberBean sortModel = new GroupMemberBean();
			sortModel.setName(cursorName.getString(cursorName
					.getColumnIndex("studentname")));
			sortModel.setPhone(cursorName.getString(cursorName
					.getColumnIndex("studentphone")));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(cursorName
					.getString(cursorName.getColumnIndex("studentname")));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<GroupMemberBean> filterDateList = new ArrayList<GroupMemberBean>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = sourceDateList;
			tvNofriends.setVisibility(View.GONE);
		} else {
			filterDateList.clear();
			for (GroupMemberBean sortModel : sourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
		if (filterDateList.size() == 0) {
			tvNofriends.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return sourceDateList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < sourceDateList.size(); i++) {
			String sortStr = sourceDateList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	private String[] manage = new String[] { "拨打", "短信" };

	private void showManageDialog(final String[] arg, final String phone) {
		new AlertDialog.Builder(EmergeContactActivity.this).setTitle(R.string.emerge_toast)
				.setItems(arg, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0:
							phone(phone);
							break;
						case 1:
							sms(phone);
							break;
						}
					}
				}).show();
	}

	public void phone(String phone) {
		Intent intentCall = new Intent();
		intentCall.setAction("android.intent.action.CALL");
		intentCall.setData(Uri.parse("tel:" + phone));
		startActivity(intentCall);
	}

	public void sms(String phone) {
		Uri uri = Uri.parse("smsto:" + phone);
		Intent intentsms = new Intent(Intent.ACTION_SENDTO, uri);
		intentsms.putExtra("sms_body", " ");
		startActivity(intentsms);
	}

}