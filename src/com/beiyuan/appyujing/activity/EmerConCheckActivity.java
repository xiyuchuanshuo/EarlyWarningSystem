package com.beiyuan.appyujing.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.adapter.SortGroupMemberAdapter;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.data.GroupMemberBean;
import com.beiyuan.appyujing.data.PinyinComparator;
import com.beiyuan.appyujing.database.EmergencyHelper;
import com.beiyuan.appyujing.database.table.ClassMates;
import com.beiyuan.appyujing.database.table.HeadTeacher;
import com.beiyuan.appyujing.database.table.Leader;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.CharacterParser;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.ClearEditText;
import com.beiyuan.appyujing.view.EditClick;
import com.beiyuan.appyujing.view.EditClick.OnButtonClickListener;
import com.beiyuan.appyujing.view.SideBar;
import com.beiyuan.appyujing.view.SideBar.OnTouchingLetterChangedListener;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;

/**
 * 紧急情况分为三个角色：领导、辅导员、学生
 * 
 * @author juan
 * 
 */
public class EmerConCheckActivity extends MyActivity implements SectionIndexer {

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

	private ArrayList<String> listCollege = new ArrayList<String>();
	private ArrayList<String> listGrade = new ArrayList<String>();
	private ArrayList<String> listProfession = new ArrayList<String>();
	private ArrayList<String> listClass = new ArrayList<String>();

	private ArrayList<String> listGradeId = new ArrayList<String>();
	private ArrayList<String> listsendId = new ArrayList<String>();

	private EditClick editCollege;
	private EditClick editGrade;
	private EditClick editProfession;
	private EditClick editClass;
	private String leaderArray;
	private UrlService urlService = new UrlServiceImpl();
	/**
	 * 上次第一个可见元素，用于滚动时记录标识。
	 */
	private int lastFirstVisibleItem = -1;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<GroupMemberBean> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private String role;
	private Dialog builder = null;
	private Handler handler;
	private JSONObject obj;
	private final static String TAG = "EmerConCheckActivity";
	private Bundle data;
	private ArrayList<String> listData = new ArrayList<String>();
	private ArrayAdapter<String> adapterCollege;
	private ListView listView_college;
	private ProgressDialog pdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emerge_contact);
		initViews();
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					Tools.mToast(EmerConCheckActivity.this, R.string.emerge_refresh);
					SourceDateList = getLeaderListItems();
					Collections.sort(SourceDateList, pinyinComparator);
					adapter.updateListView(SourceDateList);

					break;
				case 2:
					showCollegeDialog();
					break;
				case 3:
					Tools.mToast(EmerConCheckActivity.this,
							R.string.emerge_refresh);
					SourceDateList = getTeacherListItems();
					Collections.sort(SourceDateList, pinyinComparator);
					adapter.updateListView(SourceDateList);
					break;
				case 4:
					Tools.mToast(EmerConCheckActivity.this,
							R.string.emerge_refresh);
					SourceDateList = getStudentListItems();
					Collections.sort(SourceDateList, pinyinComparator);
					adapter.updateListView(SourceDateList);
					break;
				case 5:
					Tools.mToast(EmerConCheckActivity.this,
							R.string.server_error);
					break;
				case 6:
					showGradeClassDialog();
					break;

				}
			}
		};

	}
	
	/**
	 * 刷新
	 */
	private Handler handlerTool = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			data = msg.getData();
			listData = (ArrayList<String>) data.get("listData");
			Log.i(TAG, "listData===" + listData.size());
			adapterCollege = new ArrayAdapter<String>(
					EmerConCheckActivity.this,
					android.R.layout.simple_expandable_list_item_1, listData);
			listView_college.setAdapter(adapterCollege);
			pdialog.dismiss();
		}
	};

	private void initViews() {
		// 初始化title
		initTitle();
		titleLayout = (LinearLayout) findViewById(R.id.title_layout);
		title = (TextView) this.findViewById(R.id.title_layout_catalog);
		tvNofriends = (TextView) this
				.findViewById(R.id.title_layout_no_friends);
		Intent intent = getIntent();
		role = intent.getStringExtra("role");

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
		if (role.equals("leader")) {
			SourceDateList = getLeaderListItems();
		} else if (role.equals("teacher")) {
			SourceDateList = getTeacherListItems();

		} else if (role.equals("student")) {

			SourceDateList = getStudentListItems();
		}
		if (SourceDateList.size() > 1) {
			// 根据a-z进行排序源数据
			Collections.sort(SourceDateList, pinyinComparator);
			adapter = new SortGroupMemberAdapter(this, SourceDateList);
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
						title.setText(SourceDateList.get(
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
			adapter = new SortGroupMemberAdapter(this, SourceDateList);
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
	 * 初始化标题栏
	 */
	public void initTitle() {

		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle(R.string.emerge_title);
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		Drawable rightImg = getResources().getDrawable(R.drawable.map_upload_img);
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}

		});
		mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				if (role.equals("leader")) {
					// 领导刷新按钮
					Leader.deletetable(emergencyHelper);
					getLeaderContact();
				} else if (GlobalData.getRole().trim().equals("Role_Leader")
						&& role.equals("teacher")) {
					// 领导获取辅导员按钮
					HeadTeacher.deletetable(emergencyHelper);
					initCollegeDate();
				} else if (GlobalData.getRole().trim().equals("Role_Leader")
						&& role.equals("student")) {
					// 领导获取学生按钮
					ClassMates.deletetable(emergencyHelper);
					showCheckDialog();
				} else if (role.equals("leader")
						&& GlobalData.getRole().trim()
								.equals("Role_HeadTeacher")) {
					// 辅导员刷新领导按钮
					Leader.deletetable(emergencyHelper);
					getLeaderContact();

				} else if (GlobalData.getRole().trim()
						.equals("Role_HeadTeacher")
						&& role.equals("teacher")) {
					// 辅导员刷新辅导员按钮
					HeadTeacher.deletetable(emergencyHelper);
					getHeadTeacherData();

				} else {
					// 辅导员获取学生数据
					ClassMates.deletetable(emergencyHelper);
					getClassByTeacher();
				}
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
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<GroupMemberBean> filterDateList = new ArrayList<GroupMemberBean>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
			tvNofriends.setVisibility(View.GONE);
		} else {
			filterDateList.clear();
			for (GroupMemberBean sortModel : SourceDateList) {
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
		return SourceDateList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < SourceDateList.size(); i++) {
			String sortStr = SourceDateList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 领导选择学生对话框
	 */
	private void showCheckDialog() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater
				.inflate(R.layout.emerge_teacher_dialog, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		editCollege = (EditClick) viewDialog.findViewById(R.id.check_college);
		editGrade = (EditClick) viewDialog.findViewById(R.id.check_grade);
		editProfession = (EditClick) viewDialog
				.findViewById(R.id.check_profession);
		editClass = (EditClick) viewDialog.findViewById(R.id.check_class);
		editCollege.setHint(R.string.emerge_colleage);
		editGrade.setHint(R.string.emerge_grade);
		editProfession.setHint(R.string.emerge_profession);
		editClass.setHint(R.string.emerge_class);
		editCollege.setEditable(false);
		editGrade.setEditable(false);
		editProfession.setEditable(false);
		editClass.setEditable(false);
		builder.setCancelable(false);
		builder.setTitle(R.string.emerge_class_dialog);
		builder.setView(viewDialog);

		editCollege.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showCollegeStudentDialog();

			}
		});
		editGrade.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showGradeDialog();

			}
		});
		editProfession.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showProfessionDialog();
			}
		});
		editClass.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showClassDialog();
			}
		});

		builder.setPositiveButton(R.string.emerge_sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						getStudentContacts();
					}
				});
		builder.setNegativeButton(R.string.emerge_cancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		builder.show();
	}

	/**
	 * 领导通过选择学院来获取相应的辅导员
	 */
	@SuppressLint("NewApi")
	public void showCollegeDialog() {

		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_colleage_check);
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterCollege = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, listCollege);

		listView_college.setAdapter(adapterCollege);
		adapterCollege.notifyDataSetChanged();
		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String collegename = text.getText().toString();
				builder.dismiss();
				getHeadTeacherByCollege(collegename);
			}

		});

	}

	/**
	 * 领导通过选择学院来获取相应的学生
	 */
	@SuppressLint("NewApi")
	public void showCollegeStudentDialog() {

		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_colleage_check);
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);

//		ArrayAdapter<String> adapterCollege = new ArrayAdapter<String>(this,
//				android.R.layout.simple_expandable_list_item_1, listCollege);
//
//		listView_college.setAdapter(adapterCollege);
		getCollegeData();
		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String collegeName = text.getText().toString();
				Log.i(TAG, "collegeName======" + collegeName);
				editCollege.setText(collegeName);
				builder.dismiss();

			}

		});
	}

	/**
	 * 教师选择年级
	 */
	@SuppressLint("NewApi")
	public void showGradeDialog() {

		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_grade_check);
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_grade = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterGrade = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, listGrade);

		listView_grade.setAdapter(adapterGrade);

		listView_grade.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String gradeName = text.getText().toString();
				Log.i(TAG, "gradeName======" + gradeName);
				editGrade.setText(gradeName);
				builder.dismiss();
			}

		});
		adapterGrade.notifyDataSetChanged();
	}

	/**
	 * 教师选择专业
	 */
	@SuppressLint("NewApi")
	public void showProfessionDialog() {
		if (editCollege.getText().toString().equals("")) {
			Tools.mToast(EmerConCheckActivity.this,
					R.string.emerge_colleage_empty);
			return;
		}
		if (editGrade.getText().toString().equals("")) {
			Tools.mToast(EmerConCheckActivity.this, R.string.emerge_grade_empty);
			return;
		}
		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_profession_check);
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

	    listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);
		getProfessionData();

		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String professionName = text.getText().toString();
				Log.i(TAG, "professionName======" + professionName);
				editProfession.setText(professionName);
				builder.dismiss();
			}

		});
	}
	/**
	 * 教师选择班级
	 */
	@SuppressLint("NewApi")
	public void showClassDialog() {

		if (editCollege.getText().toString().equals("")) {
			Tools.mToast(EmerConCheckActivity.this,
					R.string.emerge_colleage_empty);
			return;
		}
		if (editGrade.getText().toString().equals("")) {
			Tools.mToast(EmerConCheckActivity.this, R.string.emerge_grade_empty);
			return;
		}
		if (editProfession.getText().toString().equals("")) {
			Tools.mToast(EmerConCheckActivity.this,
					R.string.emerge_profession_empty);
			return;
		}
		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_class_check);
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		getClassData();
		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String className = text.getText().toString();
				Log.i(TAG, "className======" + className);
				editClass.setText(className);
				builder.dismiss();

			}

		});
	}

	/**
	 * 辅导员选择班级来获取自己所教班级学生数据
	 */
	@SuppressLint("NewApi")
	public void showGradeClassDialog() {

		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_class_check);
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_class = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, listGrade);

		listView_class.setAdapter(adapterClass);
		adapterClass.notifyDataSetChanged();
		listView_class.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				builder.dismiss();
				getStudentByTeacher(position);

			}

		});

	}

	/**
	 * 领导获取学院数据
	 */
	public void initCollegeDate() {
		// 开线程，从服务端获取学院、年级数据
		Thread threadGetCollege = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					String jsonToString = urlService
							.getJsonContent(ConstantData.GETTEACHERCOLLEGE);
					listCollege = (ArrayList<String>) urlService
							.getCollegeList("college", jsonToString);
					Log.i(TAG, "listString===" + listCollege);
					handler.sendEmptyMessage(2);

				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(5);
					return;
				}

			}
		});
		threadGetCollege.start();
	}

	/**
	 * 学生向服务端发送学院年级,获取专业
	 */
	public void getProfessionData() {
		obj = new JSONObject();
		try {
			if (!editCollege.getText().toString().equals("")
					&& !editGrade.getText().toString().equals("")) {
				obj.put("collegeName", editCollege.getText().toString());
				obj.put("yearClass", editGrade.getText().toString());

				Log.i(TAG, "obj=====" + obj.toString());
				pdialog = Tools.pd(EmerConCheckActivity.this,"正在获取");
				Thread threadLogin = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();

						// strLoginRst = "success";

						JSONObject jsonRegisterResult = urlService
								.sentParams2DataServer(obj,
										ConstantData.GETPROFESSION);
						Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

						System.out.println("json = "
								+ jsonRegisterResult.toString());
						listProfession.clear();
						try {
							JSONArray jsonArray = jsonRegisterResult
									.getJSONArray("profession");
							for (int i = 0; i < jsonArray.length(); i++) {
								String msg = jsonArray.getString(i);

								listProfession.add(msg);
							}
							Log.i("mytag", "listProfession=========="
									+ listProfession);
							
							Message msg = new Message();
							Bundle b = new Bundle();// 存放数据
							b.putSerializable("listData",
									(Serializable) listProfession);
							msg.setData(b);
							handlerTool.sendMessage(msg);

						} catch (JSONException e) {
							e.printStackTrace();
							handler.sendEmptyMessage(5);
							pdialog.dismiss();
							return;
						}
					}
				});
				threadLogin.start();

			}
		} catch (Exception e) {
			Tools.mToast(EmerConCheckActivity.this, R.string.server_error);
			e.printStackTrace();
			pdialog.dismiss();
		}
	}


	/**
	 * 向服务端发送学院年级专业,获取班级
	 */
	public void getClassData() {

		obj = new JSONObject();
		try {
			if (!editCollege.getText().toString().equals("")
					&& !editGrade.getText().toString().equals("")
					&& !editProfession.getText().toString().equals("")) {
				obj.put("collegeName", editCollege.getText().toString());
				obj.put("yearClass", editGrade.getText().toString());
				obj.put("profession", editProfession.getText().toString());

				Log.i(TAG, "obj=====" + obj.toString());
				pdialog = Tools.pd(EmerConCheckActivity.this,"正在获取");
				Thread threadLogin = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();

						JSONObject jsonRegisterResult = urlService
								.sentParams2DataServer(obj,
										ConstantData.GETCLASS);
						Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

						System.out.println("json = "
								+ jsonRegisterResult.toString());
						listClass.clear();
						try {
							JSONArray jsonArray = jsonRegisterResult
									.getJSONArray("classID");
							for (int i = 0; i < jsonArray.length(); i++) {
								String msg = jsonArray.getString(i);
								listClass.add(msg);
							}
							Log.i(TAG, "listClass===" + listClass);
							Message msg = new Message();
							Bundle b = new Bundle();// 存放数据
							b.putSerializable("listData",
									(Serializable) listClass);
							msg.setData(b);
							handlerTool.sendMessage(msg);

						} catch (JSONException e) {
							e.printStackTrace();
							handler.sendEmptyMessage(5);
							pdialog.dismiss();
							return;
						}
					}
				});
				threadLogin.start();
			}
		} catch (Exception e) {
			Tools.mToast(EmerConCheckActivity.this, "连接服务器失败···");
			e.printStackTrace();
			pdialog.dismiss();
		}
	}

	/**
	 * 辅导员向服务端发送辅导员昵称,获取班级
	 */
	public void getClassByTeacher() {

		obj = new JSONObject();
		try {
			obj.put("headTeacherNickname", GlobalData.getUsername().toString()
					.trim());

			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadGetClassByTea = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonGradeResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.EMEGETCLASSBYTEACHER);
					Log.i("JSON", "jsonRegeditRst" + jsonGradeResult);

					listGrade.clear();
					listGradeId.clear();
					try {
						String gradeByTeacher = jsonGradeResult
								.getString("grade");
						JSONObject jsonGrade = new JSONObject(gradeByTeacher);
						Log.i("mytag", "jsonGrade====" + jsonGrade.toString());

						for (int i = 0; i < jsonGrade.length(); i++) {
							String gradearray = jsonGrade.getString("" + i);
							JSONObject jsongrade = new JSONObject(gradearray);
							String gradeId = jsongrade.getString("gradeID");
							String grade = jsongrade.getString("grade");
							String profession = jsongrade
									.getString("profession");
							String classId = jsongrade.getString("classID");
							String name = grade + profession + classId + "班";

							listGrade.add(name);
							listGradeId.add(gradeId + "");
						}
						Log.i(TAG, "listGrade==========" + listGrade);
						Log.i(TAG, "listGradeId==========" + listGradeId);
						handler.sendEmptyMessage(6);
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}
				}
			});
			threadGetClassByTea.start();

		} catch (Exception e) {
			Tools.mToast(EmerConCheckActivity.this, R.string.server_error);
			e.printStackTrace();
		}
	}

	/**
	 * 获取领导的联系方式
	 */
	public void getLeaderContact() {
		obj = new JSONObject();
		try {

			Thread threadEmergency = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					try {
						UrlServiceImpl urlService = new UrlServiceImpl();
						if (GlobalData.getRole().trim().equals("Role_Leader")) {
							leaderArray = urlService
									.getJsonContent(ConstantData.EMEGETLEADER);
						} else {
							leaderArray = urlService
									.getJsonContent(ConstantData.EMEGETLEADERBYTEACHER);
						}
						JSONObject jsonLeader = new JSONObject(leaderArray);// 防止非json数据传入
						for (int i = 0; i < jsonLeader.length(); i++) {
							String leaderarray = jsonLeader.getString("" + i);
							JSONObject jsonleader = new JSONObject(leaderarray);
							String name = jsonleader.getString("leaderName");
							String phone = jsonleader.getString("phone");
							Log.i(TAG, "leaderName==" + name);
							Log.i(TAG, "leaderPhone==" + phone);
							Leader.insertLeader(emergencyHelper, name, phone);
						}
						handler.sendEmptyMessage(1);

					} catch (Exception e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}
				}

			});
			threadEmergency.start();

		} catch (Exception e) {
			Tools.mToast(EmerConCheckActivity.this, R.string.server_error);
			e.printStackTrace();
		}

	}

	/**
	 * 领导获取辅导员的联系方式
	 */
	public void getHeadTeacherByCollege(String name) {
		obj = new JSONObject();
		try {
			obj.put("collegeName", name);

			Log.i("mytag", obj.toString());

			Thread threadEmergency = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					JSONObject jsonEmergeResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.EMEGETTEACHERBYCOLLEGE);
					Log.i("JSON",
							"jsonRegeditRst====" + jsonEmergeResult.toString());

					try {
						String teacherArray = jsonEmergeResult
								.getString("headTeacher");

						Log.i(TAG, "teacherTemp===" + teacherArray);

						JSONObject jsonTeacher = new JSONObject(teacherArray);// 防止非json数据传入

						for (int i = 0; i < jsonTeacher.length(); i++) {
							String classarray = jsonTeacher.getString("" + i);
							JSONObject jsonteacher = new JSONObject(classarray);
							String name = jsonteacher
									.getString("headTeacherName");
							String phone = jsonteacher.getString("phone");
							Log.i(TAG, "teacherName==" + name);
							Log.i(TAG, "teacherPhone==" + phone);
							HeadTeacher.insertTeacher(emergencyHelper, name,
									phone);
						}
						handler.sendEmptyMessage(3);
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);

						return;
					}
				}

			});
			threadEmergency.start();

		} catch (Exception e) {
			Tools.mToast(EmerConCheckActivity.this, R.string.server_error);
			e.printStackTrace();
		}

	}

	/**
	 * 辅导员获取辅导员的联系方式
	 */
	public void getHeadTeacherData() {
		obj = new JSONObject();
		try {
			obj.put("headTeacherNickname", GlobalData.getUsername().toString()
					.trim());
			Thread threadEmergency = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					JSONObject jsonEmergeResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.EMEGETLEADERTEACHER);
					Log.i("JSON",
							"jsonRegeditRst====" + jsonEmergeResult.toString());

					try {
						String teacherArray = jsonEmergeResult
								.getString("headTeacher");

						Log.i(TAG, "teacherTemp===" + teacherArray);

						JSONObject jsonTeacher = new JSONObject(teacherArray);// 防止非json数据传入

						for (int i = 0; i < jsonTeacher.length(); i++) {
							String classarray = jsonTeacher.getString("" + i);
							JSONObject jsonteacher = new JSONObject(classarray);
							String name = jsonteacher
									.getString("headTeacherName");
							String phone = jsonteacher.getString("phone");
							Log.i(TAG, "teacherName==" + name);
							Log.i(TAG, "teacherPhone==" + phone);
							HeadTeacher.insertTeacher(emergencyHelper, name,
									phone);
						}
						handler.sendEmptyMessage(3);

					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);

						return;
					}
				}

			});
			threadEmergency.start();

		} catch (Exception e) {
			Tools.mToast(EmerConCheckActivity.this, R.string.server_error);
			e.printStackTrace();
		}

	}

	/**
	 * 领导获取学生联系方式
	 */
	public void getStudentContacts() {

		obj = new JSONObject();
		try {
			obj.put("grade", editGrade.getText().toString().trim());

			obj.put("profession", editProfession.getText().toString().trim());

			obj.put("classID", editClass.getText().toString().trim());

			Log.i("mytag", obj.toString());

			Thread threadEmergency = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonEmergeResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.EMELEADERGETSTUDENT);
					Log.i("JSON",
							"jsonRegeditRst====" + jsonEmergeResult.toString());

					try {
						String classmateArray = jsonEmergeResult
								.getString("student");
						String teacherArray = jsonEmergeResult
								.getString("headTeacher");

						JSONObject jsonClass = new JSONObject(classmateArray);// 防止非json数据传入

						JSONObject jsonTeacher = new JSONObject(teacherArray);

						for (int i = 0; i < jsonClass.length(); i++) {
							String classarray = jsonClass.getString("" + i);
							JSONObject jsonclass = new JSONObject(classarray);
							String name = jsonclass.getString("studentName");
							String phone = jsonclass.getString("phone");
							Log.i(TAG, "className==" + name);
							Log.i(TAG, "classPhone==" + phone);
							ClassMates.insertStudent(emergencyHelper, name,
									phone);
						}

						String name = jsonTeacher.getString("headTeacherName");
						String phone = jsonTeacher.getString("phone");
						Log.i(TAG, "className==" + name);
						Log.i(TAG, "classPhone==" + phone);
						ClassMates.insertStudent(emergencyHelper, "辅导员    "
								+ name, phone);
						handler.sendEmptyMessage(4);
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);

						return;
					}
				}

			});
			threadEmergency.start();

		} catch (Exception e) {
			Tools.mToast(EmerConCheckActivity.this, R.string.server_error);
			e.printStackTrace();
		}

	}

	/**
	 * 辅导员获取学生联系方式
	 */
	public void getStudentByTeacher(int position) {

		obj = new JSONObject();
		try {

			obj.put("gradeID", listGradeId.get(position));

			Log.i("mytag", obj.toString());

			Thread threadEmergency = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					JSONObject jsonEmergeResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.EMELEADERGETSTUDENT);
					Log.i("JSON",
							"jsonRegeditRst====" + jsonEmergeResult.toString());

					try {
						String classmateArray = jsonEmergeResult
								.getString("student");
						String teacherArray = jsonEmergeResult
								.getString("headTeacher");

						JSONObject jsonClass = new JSONObject(classmateArray);// 防止非json数据传入

						JSONObject jsonTeacher = new JSONObject(teacherArray);

						for (int i = 0; i < jsonClass.length(); i++) {
							String classarray = jsonClass.getString("" + i);
							JSONObject jsonclass = new JSONObject(classarray);
							String name = jsonclass.getString("studentName");
							String phone = jsonclass.getString("phone");
							Log.i(TAG, "className==" + name);
							Log.i(TAG, "classPhone==" + phone);
							ClassMates.insertStudent(emergencyHelper, name,
									phone);
						}

						String name = jsonTeacher.getString("headTeacherName");
						String phone = jsonTeacher.getString("phone");
						Log.i(TAG, "className==" + name);
						Log.i(TAG, "classPhone==" + phone);
						ClassMates.insertStudent(emergencyHelper, "辅导员    "
								+ name, phone);
						handler.sendEmptyMessage(4);
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);

						return;
					}
				}

			});
			threadEmergency.start();

		} catch (Exception e) {
			Tools.mToast(EmerConCheckActivity.this, R.string.server_error);
			e.printStackTrace();
		}

	}
	/**
	 * 获取学院、年级数据
	 */
	public void getCollegeData() {
		// 开线程，从服务端获取学院、年级数据
		pdialog = Tools.pd(EmerConCheckActivity.this,"正在获取");
		Thread threadRegister = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				String jsonToString = urlService
						.getJsonContent(ConstantData.GETCOLLEGEGRA);
				listCollege = (ArrayList<String>) urlService.getCollegeList(
						"college", jsonToString);
				listGrade = (ArrayList<String>) urlService.getCollegeList(
						"grade", jsonToString);
				
				Message msg = new Message();
				Bundle b = new Bundle();// 存放数据
				b.putSerializable("listData",
						(Serializable) listCollege);
				msg.setData(b);
				handlerTool.sendMessage(msg);
				Log.i(TAG, "listString===" + listCollege);
				Log.i(TAG, "listString===" + listGrade);

			}
		});
		threadRegister.start();
	}


	private String[] manage = new String[] { "拨打", "短信" };

	private void showManageDialog(final String[] arg, final String phone) {
		new AlertDialog.Builder(EmerConCheckActivity.this)
				.setTitle(R.string.emerge_title)
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
