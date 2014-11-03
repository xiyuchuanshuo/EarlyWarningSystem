package com.beiyuan.appyujing.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.location.core.GeoPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.amap.mapcore.MapCore;
import com.beiyuan.appyujing.HeadteacherActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.TeacherActivity;
import com.beiyuan.appyujing.activity.GaodeMapNaviActivity;
import com.beiyuan.appyujing.activity.MapSendMsgActivity;
import com.beiyuan.appyujing.activity.QueryNameActivity;
import com.beiyuan.appyujing.activity.WeiboActivity;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.service.MainApplication;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.tools.Util;
import com.beiyuan.appyujing.view.DragLayout;
import com.beiyuan.appyujing.view.EditClick;
import com.beiyuan.appyujing.view.GaodeUtils;
import com.beiyuan.appyujing.view.EditClick.OnButtonClickListener;
import com.google.gson.Gson;

@SuppressLint("NewApi")
public class GaodeMapFragment extends Fragment implements AMapNaviListener,
		LocationSource, AMapLocationListener, AMapLocalWeatherListener,
		OnClickListener, OnItemClickListener {
	// 地图和导航资源
	private MapView mMapView;
	private AMap mAMap;
	private AMapNavi mAMapNavi;
	// 起点终点坐标
	private NaviLatLng mNaviStart;
	private NaviLatLng mNaviEnd;
	private String address;

	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	// 规划线路
	private RouteOverLay mRouteOverLay;
	// 是否驾车和是否计算成功的标志
	private boolean mIsDriveMode = true;
	private boolean mIsCalculateRouteSuccess = false;
	// =================
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private double mLat1, mLat2;// 纬度
	private double mLon1, mLon2;// 经度

	// 今天天气-----
	private String mLocation;// 地点
	private String mWeather;// 天气
	private String mWeatherTemperature;// 气温
	private String mWindDirction;// 风向
	private String mWindPower;// 风力
	private String mAirHumidity;// 空气湿度
	private String mWeatherPublish;// 发布时间
	View view, myview;
	private String role;
	private Button left_back, right_more, buttontype, navigation;
	private DragLayout dl;

	Handler handler;
	JSONObject obj;
	List<Map<String, String>> list;
	SimpleAdapter mAdapter;

	private ListView mListView;
	private AlertDialog builder_dlg;
	private ProgressDialog progressDialog = null;

	private EditClick editCollege;
	private EditClick editGrade;
	private EditClick editProfession;
	private EditClick editClass;
	private Dialog builder = null;
	private ArrayList<String> listCollege = new ArrayList<String>();
	private ArrayList<String> listGrade = new ArrayList<String>();
	private ArrayList<String> listProfession = new ArrayList<String>();
	private ArrayList<String> listClass = new ArrayList<String>();
	private ArrayList<String> listCourse = new ArrayList<String>();
	private UrlService urlService = new UrlServiceImpl();
	private String collegeName, gradeName, professionName, className,
			profession, gradeID, classID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		role = GlobalData.getRole();
		view = inflater.inflate(R.layout.fragment3, container, false);
		initView(savedInstanceState);
		MainApplication.getInstance().addActivity(getActivity());
		buttontype = (Button) view.findViewById(R.id.buttontype);
		buttontype.setOnClickListener(this);
		left_back = (Button) view.findViewById(R.id.left_back);
		left_back.setOnClickListener(this);
		right_more = (Button) view.findViewById(R.id.right_more);
		right_more.setOnClickListener(this);

		String s = GlobalData.getRole();
		if (GlobalData.getRole().trim().equals("Role_Teacher")) {
			dl = TeacherActivity.dl;
		} else if (GlobalData.getRole().trim().equals("Role_Student")) {
			dl = StudentActivity.dl;
		} else {
			dl = HeadteacherActivity.dl;
		}

		return view;
	}

	// 初始化View
	private void initView(Bundle savedInstanceState) {
		mAMapNavi = AMapNavi.getInstance(getActivity());
		mAMapNavi.setAMapNaviListener(this);
		mMapView = (MapView) view.findViewById(R.id.simple_route_map);
		mMapView.onCreate(savedInstanceState);
		mAMap = mMapView.getMap();
		setUpMap();
		mRouteOverLay = new RouteOverLay(mAMap, null);
	}

	// 设置地图的属性
	private void setUpMap() {
		mAMap.setLocationSource(this);// 设置定位监听
		// 设置中心点
		// mAMap.setPointToCenter((int) 40.772266, (int) 114.891833);
		mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	// 计算驾车路线
	private void calculateDriveRoute() {
		boolean isSuccess = mAMapNavi.calculateDriveRoute(mStartPoints,
				mEndPoints, null, AMapNavi.DrivingDefault);
		if (!isSuccess) {
			showToast("路线计算失败,检查参数情况");
		}
	}

	private void showToast(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	// 模拟导航
	private void startEmulatorNavi(boolean isDrive) {
		if ((isDrive && mIsDriveMode && mIsCalculateRouteSuccess)
				|| (!isDrive && !mIsDriveMode && mIsCalculateRouteSuccess)) {
			Intent emulatorIntent = new Intent(getActivity(),
					GaodeMapNaviActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(GaodeUtils.ISEMULATOR, true);
			bundle.putInt(GaodeUtils.ACTIVITYINDEX, GaodeUtils.SIMPLEROUTENAVI);
			emulatorIntent.putExtras(bundle);
			emulatorIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(emulatorIntent);

		} else {
			showToast("请先进行相对应的路径规划，再进行导航");
		}
	}

	// 开启实时导航
	private void startGPSNavi(boolean isDrive) {
		if ((isDrive && mIsDriveMode && mIsCalculateRouteSuccess)
				|| (!isDrive && !mIsDriveMode && mIsCalculateRouteSuccess)) {
			Intent gpsIntent = new Intent(getActivity(),
					GaodeMapNaviActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(GaodeUtils.ISEMULATOR, false);
			bundle.putInt(GaodeUtils.ACTIVITYINDEX, GaodeUtils.SIMPLEROUTENAVI);
			gpsIntent.putExtras(bundle);
			gpsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(gpsIntent);
		} else {
			showToast("请先进行相对应的路径规划，再进行导航");
		}
	}

	// --------------------导航监听回调事件-----------------------------
	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		showToast("路径规划出错" + arg0);
		mIsCalculateRouteSuccess = false;
	}

	// 规划路径
	@Override
	public void onCalculateRouteSuccess() {
		AMapNaviPath naviPath = mAMapNavi.getNaviPath();
		if (naviPath == null) {
			return;
		}
		// 获取路径规划线路，显示到地图上
		mRouteOverLay.setRouteInfo(naviPath);
		mRouteOverLay.addToMap();
		mIsCalculateRouteSuccess = true;
	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub

	}

	// ------------------生命周期重写函数---------------------------

	@Override
	public void onDestroy() {
		mMapView.onDestroy();
		// 删除监听
		AMapNavi.getInstance(getActivity()).removeAMapNaviListener(this);
		try {
			builder.dismiss();
			builder.cancel();
		} catch (Exception e) {
			System.out.println("Dialog取消，失败！");
			// TODO: handle exception
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		deactivate();
		super.onPause();
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	// 获取地理信息和坐标
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				// 坐标
				mLat1 = amapLocation.getLatitude();
				mLon1 = amapLocation.getLongitude();
				GlobalData.setLat(mLat1);
				GlobalData.setLon(mLon1);
				Log.i("TAG", mLat1 + "  " + mLon1);
				address = amapLocation.getAddress();
				// 关闭GPS
				mAMapLocationManager.setGpsEnable(false);
			}
		}
	}

	// 天气预报
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy
					.getInstance(getActivity());
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
			// 获取实时天气预报
			mAMapLocationManager.requestWeatherUpdates(
					LocationManagerProxy.WEATHER_TYPE_LIVE, this);

		}
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int button_id = v.getId();
		switch (button_id) {
		case R.id.buttontype:
			int type = mAMap.getMapType();
			if (type == AMap.MAP_TYPE_NORMAL) {
				// 卫星图
				mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
			} else if (type == AMap.MAP_TYPE_SATELLITE) {
				// 夜间图
				mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
			} else if (type == AMap.MAP_TYPE_NIGHT) {
				// 正常图
				mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
			}
			break;

		case R.id.right_more:
			if (role == "Role_HeadTeacher") {
				HeadTeacher_Choice();
			} else if (role == "Role_Teacher") {
				Teacher_Choice();
			} else if (role == "Role_Student") {
				Student_Choice();
			} else {
				// 领导
				getCollegeData();
				showCheckDialog();
			}
			break;
		case R.id.left_back:
			dl.open();
			break;
		case R.id.navigation:
			startGPSNavi(true);
			break;
		}
	}

	@Override
	public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive arg0) {
		// TODO Auto-generated method stub
		if (arg0 != null && arg0.getAMapException().getErrorCode() == 0) {
			// 天气预报成功回调 设置天气信息
			mLocation = arg0.getCity();
			mWeather = arg0.getWeather();
			mWeatherTemperature = arg0.getTemperature() + "℃";
			mWindDirction = arg0.getWindDir() + "风";
			mWindPower = arg0.getWindPower() + "级";
			mAirHumidity = arg0.getHumidity() + "%";
			mWeatherPublish = arg0.getReportTime();
			Log.i("TAG", arg0.toString());
		} else {
			// 获取天气预报失败
			showToast("获取天气预报失败:" + arg0.getAMapException().getErrorMessage());
		}
	}

	// 辅导员权限
	private void HeadTeacher_Choice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("实时情况");
		myview = View.inflate(getActivity(), R.layout.mapgetclass, null);
		builder.setView(myview);
		builder.setCancelable(true);
		getMyView_headteacher();
		builder.setNegativeButton("取消", null);
		builder_dlg = builder.create();
		builder_dlg.show();
		progressDialog = ProgressDialog.show(getActivity(), null, "正在加载列表",
				true);
	}

	// 学生权限
	private void Student_Choice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("更多功能");
		builder.setItems(R.array.student_choice,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {

						case 0:
							showToast(address + "\n坐标:" + mLat1 + " " + mLon1);
							break;
						case 1:
							showToast("今天天气:" + mWeather + "\n"
									+ mWeatherTemperature + mWindDirction + " "
									+ mWindPower);
							break;
						case 2:
							Intent intent = new Intent(getActivity(),
									MapSendMsgActivity.class);
							intent.putExtra("address", address);
							intent.putExtra("longtitude", mLon1);
							intent.putExtra("latitude", mLat1);
							startActivity(intent);
							break;
						}
					}
				});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	// 老师权限
	private void Teacher_Choice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("更多功能");
		builder.setItems(R.array.headteacher_choice,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {

						case 0:
							showToast(address + "\n坐标:" + mLat1 + " " + mLon1);
							break;
						case 1:
							showToast("今天天气:" + mWeather + "\n"
									+ mWeatherTemperature + mWindDirction + " "
									+ mWindPower);
							break;
						}
					}
				});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	// 领导权限

	public void getCollegeData() {
		// 开线程，从服务端获取学院、年级数据
		Thread threadGetCollege = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String jsonToString = urlService
						.getJsonContent(ConstantData.GETCOLLEGEGRA);
				listCollege = (ArrayList<String>) urlService.getCollegeList(
						"college", jsonToString);
				listGrade = (ArrayList<String>) urlService.getCollegeList(
						"grade", jsonToString);
				Log.i("TAG", "listString===" + listCollege);
				Log.i("TAG", "listString===" + listGrade);

			}
		});
		threadGetCollege.start();
	}

	/**
	 * 教师选择对话框
	 */
	private void showCheckDialog() {

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewDialog = inflater.inflate(R.layout.gaodemap_leader, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		editCollege = (EditClick) viewDialog.findViewById(R.id.check_college);
		editGrade = (EditClick) viewDialog.findViewById(R.id.check_grade);
		editProfession = (EditClick) viewDialog
				.findViewById(R.id.check_profession);
		editClass = (EditClick) viewDialog.findViewById(R.id.check_class);
		editCollege.setHint("请选择学院");
		editGrade.setHint("请选择年级");
		editProfession.setHint("请选择专业");
		editClass.setHint("请选择班级");
		builder.setCancelable(false);
		builder.setTitle("选择班级对话框");
		builder.setView(viewDialog);
		editCollege.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showCollegeDialog();
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

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 确定
				getMyView_leader();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.show();

	}

	// =====================
	public void showCollegeDialog() {

		builder = new Dialog(getActivity());
		builder.setTitle("学院列表");
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterCollege = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_expandable_list_item_1,
				listCollege);

		listView_college.setAdapter(adapterCollege);
		adapterCollege.notifyDataSetChanged();
		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				collegeName = text.getText().toString();
				editCollege.setText(collegeName);
				builder.dismiss();
			}
		});
	}

	// ===========================
	@SuppressLint("NewApi")
	public void showGradeDialog() {

		builder = new Dialog(getActivity());
		builder.setTitle("年级列表");
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_grade = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterGrade = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_expandable_list_item_1,
				listGrade);

		listView_grade.setAdapter(adapterGrade);
		adapterGrade.notifyDataSetChanged();
		listView_grade.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				gradeName = text.getText().toString();
				editGrade.setText(gradeName);
				builder.dismiss();
				getProfessionData();
			}
		});

	}

	public void getProfessionData() {
		obj = new JSONObject();
		try {
			if (editCollege.getText().toString().equals("")) {
				showToast("学院不能为空");
				return;
			}
			if (editGrade.getText().toString().equals("")) {
				showToast("年级不能为空");
				return;
			}
			obj.put("collegeName", editCollege.getText().toString().trim());
			obj.put("yearClass", editGrade.getText().toString().trim());

			Thread threadQuery = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonResult = urlService.sentParams2DataServer(
							obj, ConstantData.GETPROFESSION);
					Log.i("JSON", "jsonRegeditRst" + jsonResult);

					listProfession.clear();
					try {
						JSONArray jsonArray = jsonResult
								.getJSONArray("profession");
						for (int i = 0; i < jsonArray.length(); i++) {
							String msg = jsonArray.getString(i);

							listProfession.add(msg);
						}
						Log.i("mytag", "listProfession=========="
								+ listProfession);

					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}
				}
			});
			threadQuery.start();

		} catch (Exception e) {
			showToast("连接服务器失败···");
			e.printStackTrace();
		}
	}

	// ==========================
	/**
	 * 教师选择专业
	 */
	@SuppressLint("NewApi")
	public void showProfessionDialog() {
		if (editCollege.getText().toString().equals("")) {
			showToast("学院不能为空");
			return;
		}
		if (editGrade.getText().toString().equals("")) {
			showToast("年级不能为空");
			return;
		}
		builder = new Dialog(getActivity());
		builder.setTitle("专业列表");
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_profession = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterProfession = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_expandable_list_item_1,
				listProfession);

		listView_profession.setAdapter(adapterProfession);
		adapterProfession.notifyDataSetChanged();
		listView_profession.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				professionName = text.getText().toString();
				editProfession.setText(professionName);
				builder.dismiss();
				getClassData();
			}
		});
	}

	public void getClassData() {

		obj = new JSONObject();
		try {
			if (editCollege.getText().toString().equals("")) {

				showToast("学院不能为空");
				return;
			}
			if (editGrade.getText().toString().equals("")) {
				showToast("年级不能为空");
				return;
			}
			if (editProfession.getText().toString().equals("")) {
				return;
			}
			obj.put("collegeName", editCollege.getText().toString().trim());
			obj.put("yearClass", editGrade.getText().toString().trim());
			obj.put("profession", editProfession.getText().toString().trim());

			Thread threadQuery = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonRegisterResult = urlService
							.sentParams2DataServer(obj, ConstantData.GETCLASS);
					Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

					listClass.clear();
					try {
						JSONArray jsonArray = jsonRegisterResult
								.getJSONArray("classID");
						for (int i = 0; i < jsonArray.length(); i++) {
							String msg = jsonArray.getString(i);
							listClass.add(msg);
						}

					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}
				}
			});
			threadQuery.start();
		} catch (Exception e) {
			showToast("连接服务器失败···");
			e.printStackTrace();
		}
	}

	// =========================
	@SuppressLint("NewApi")
	public void showClassDialog() {

		if (editCollege.getText().toString().equals("")) {
			showToast("学院不能为空");
			return;
		}
		if (editGrade.getText().toString().equals("")) {
			showToast("年级不能为空");
			return;
		}
		if (editProfession.getText().toString().equals("")) {
			showToast("专业不能为空");
			return;
		}
		builder = new Dialog(getActivity());
		builder.setTitle("班级列表");
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_class = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_expandable_list_item_1,
				listClass);

		listView_class.setAdapter(adapterClass);
		adapterClass.notifyDataSetChanged();
		listView_class.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				className = text.getText().toString();
				editClass.setText(className);
				builder.dismiss();
				getCourseData();
			}
		});
	}

	public void getCourseData() {

		obj = new JSONObject();
		try {
			if (editGrade.getText().toString().equals("")) {
				showToast("年级不能为空");
				return;
			}
			if (editProfession.getText().toString().equals("")) {
				showToast("专业不能为空");
				return;
			}
			if (editClass.getText().toString().equals("")) {
				showToast("班级不能为空");
				return;
			}
			obj.put("grade", editGrade.getText().toString().trim());
			obj.put("profession", editProfession.getText().toString().trim());
			obj.put("classID", editClass.getText().toString().trim());

			Thread threadQuery = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonRegisterResult = urlService
							.sentParams2DataServer(obj, ConstantData.GETCOURSE);
					Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

					JSONArray jsonArray;
					listCourse.clear();
					try {
						jsonArray = jsonRegisterResult
								.getJSONArray("className");
						for (int i = 0; i < jsonArray.length(); i++) {
							String msg = jsonArray.getString(i);
							listCourse.add(msg);
						}

					} catch (JSONException e) {
						e.printStackTrace();

						handler.sendEmptyMessage(5);
						return;
					}
					if (jsonArray.equals("")) {
						listCourse.add("");
						handler.sendEmptyMessage(1);
					}
				}
			});
			threadQuery.start();

		} catch (Exception e) {
			showToast("连接服务器失败···");
			e.printStackTrace();
		}
	}

	private void getMyView_leader() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("classID", className);
		bundle.putString("gradeID", gradeID);
		bundle.putString("profession", professionName);
		bundle.putString("gradeName", gradeName);
		intent.putExtras(bundle);
		Log.i("test", "gradeID=" + gradeID);
		intent.setClass(GaodeMapFragment.this.getActivity(),
				WeiboActivity.class);
		startActivityForResult(intent, 2);
	}

	// 辅导员紧急情况------------
	private void getMyView_headteacher() {
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case -1:
					Toast.makeText(GaodeMapFragment.this.getActivity(),
							"网络状态不可用！\n请先设置网络", Toast.LENGTH_LONG).show();
					break;
				case 1:
					mAdapter.notifyDataSetChanged();
					break;
				case 2:
					Toast.makeText(GaodeMapFragment.this.getActivity(),
							"获取数据异常", Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
		list = new ArrayList<Map<String, String>>();
		if (Util.isNetworkAvailable(GaodeMapFragment.this.getActivity())) {
			Thread threadSet = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					obj = new JSONObject();
					try {
						obj.put("userNickname", GlobalData.getUsername());
						Log.i("test", obj.toString());
						Log.i("test", "http=" + ConstantData.MAPGETCLASS);
						JSONObject jsonObject = urlService
								.sentParams2DataServer(obj,
										ConstantData.MAPGETCLASS);
						Log.i("test", "---------------------------");

						Log.i("test", jsonObject.toString());
						Log.i("test", "---------------------------");

						for (int i = 0; i < jsonObject.length(); i++) {
							HashMap<String, String> map = new HashMap<String, String>();
							String classarray = jsonObject.getString("" + i);
							JSONObject jsonStudentInfo = new JSONObject(
									classarray);
							String length = jsonStudentInfo
									.getString("longLatLength");
							String grade = jsonStudentInfo.getString("grade");
							JSONObject jsonGrade = new JSONObject(grade);
							gradeID = jsonGrade.getString("gradeID");
							gradeName = jsonGrade.getString("gradeName");
							profession = jsonGrade.getString("profession");
							classID = jsonGrade.getString("classID");
							String className = gradeName + "级" + profession
									+ classID + "班" + "       " + length;
							map.put("ClassName", className);
							map.put("gradeID", gradeID);
							map.put("classID", classID);
							map.put("profession", profession);
							map.put("gradeName", gradeName);
							list.add(map);
						}
						progressDialog.dismiss();
						for (int i = 0; i < list.size(); i++) {
							Log.i("test", "list====" + list.get(i).toString());
						}
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);

					} catch (Exception e) {
						Message message = new Message();
						message.what = -1;
						handler.sendMessage(message);
						Log.i("MAPTEACHER", "e=" + e.toString());
					}
				}
			});
			threadSet.start();

		} else {
			Toast.makeText(GaodeMapFragment.this.getActivity(),
					"网络状态不可用！\n请先设置网络", Toast.LENGTH_LONG).show();
		}
		mListView = (ListView) myview.findViewById(R.id.class_lv);

		mListView.setOnItemClickListener(this);
		mAdapter = new SimpleAdapter(GaodeMapFragment.this.getActivity(), list,
				R.layout.mapclassinfo, new String[] { "ClassName",
						"longLatLength" }, new int[] { R.id.classinfo_name,
						R.id.classinfo_num });
		mListView.setAdapter(mAdapter);

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HashMap<String, Object> map = (HashMap<String, Object>) parent
				.getItemAtPosition(position);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("classID", (String) map.get("classID"));
		bundle.putString("gradeID", (String) map.get("gradeID"));
		bundle.putString("profession", (String) map.get("profession"));
		bundle.putString("gradeName", (String) map.get("gradeName"));
		intent.putExtras(bundle);
		Log.i("test", "gradeID=" + gradeID);
		intent.setClass(GaodeMapFragment.this.getActivity(),
				WeiboActivity.class);
		startActivityForResult(intent, 2);
		builder_dlg.dismiss();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("onActivityResult", "===========" + requestCode + "---"
				+ resultCode);
		if (requestCode == 2) {
			if (resultCode == 200) {
				// 接收坐标
				String lon = data.getStringExtra("lon");
				String lat = data.getStringExtra("lat");
				if (lon != null && lat != null) {
					mLon2 = Double.parseDouble(lon);
					mLat2 = Double.parseDouble(lat);

					mLat1 = GlobalData.getLat();
					mLon1 = GlobalData.getLon();

					// mNaviStart = new NaviLatLng(40.772269, 114.891873);
					// mNaviEnd = new NaviLatLng(41.772456, 114.8924950);

					mNaviStart = new NaviLatLng(mLat1, mLon1);
					mNaviEnd = new NaviLatLng(mLat2, mLon2);

					mStartPoints.add(mNaviStart);
					mEndPoints.add(mNaviEnd);
					mIsCalculateRouteSuccess = false;
					mIsDriveMode = true;
					calculateDriveRoute();

					navigation = (Button) view.findViewById(R.id.navigation);
					navigation.setOnClickListener(this);
					navigation.setVisibility(0);
				}

			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
