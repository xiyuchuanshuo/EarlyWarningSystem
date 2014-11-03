package com.beiyuan.appyujing.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beiyuan.appyujing.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by zky 该工具类中为有返回值的函数
 */
public class Util {
	public final static String TAG = Util.class.getSimpleName();

	/**
	 * 判断网络状态是否可用
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isNetworkAvailable(Activity activity) {
		Context context = activity.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					System.out.println(i + "===状态==="
							+ networkInfo[i].getState());
					System.out.println(i + "===类型==="
							+ networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * get exter store status if state is Environment.MEDIA_MOUNTED the exter
	 * sotre can read/write
	 * 
	 * @return
	 */
	public static String getExternalStorageState() {
		return Environment.getExternalStorageState();
	}

	/**
	 * get exter store is can read/write
	 * 
	 * @return
	 */
	public static boolean getExternalStoreState() {
		if (Environment.MEDIA_MOUNTED.equals(Util.getExternalStorageState()))
			return true;
		return false;
	}

	/**
	 * get exter store file
	 * 
	 * @return
	 */
	public static File getExternalStorageFile() {
		return Environment.getExternalStorageDirectory();
	}

	/**
	 * get exter store path
	 * 
	 * @return
	 */
	public static String getExternalStoragePath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * judge the list is null or empty
	 * 
	 */
	public static boolean isEmpty(List<? extends Object> list) {
		if (list == null || list.isEmpty()) {
			return true;
		}

		return false;
	}

	/**
	 * judge the set is null or empty
	 */
	public static boolean isEmpty(Set<? extends Object> set) {
		if (set == null || set.isEmpty())
			return true;
		return false;
	}

	/**
	 * judge the map is null or empty
	 */
	public static boolean isEmpty(Map<? extends Object, ? extends Object> map) {
		if (map == null || map.isEmpty())
			return true;
		return false;
	}

	/**
	 * get the width of the device screen
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * get the height of the device screen
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * get the density of the device screen
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * dip to px
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}

	/**
	 * hide softinput method
	 * 
	 * @param view
	 */
	public static void hideSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
		}
	}

	/**
	 * show softinput method
	 * 
	 * @param view
	 */
	public static void showSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0);
	}

	// public static void initImageLoader(Context context) {
	// DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	// .showImageOnLoading(R.drawable.default_face)
	// .showImageForEmptyUri(R.drawable.default_face)
	// .showImageOnFail(R.drawable.default_face).cacheInMemory(true)
	// .considerExifParams(true)
	// .displayer(new FadeInBitmapDisplayer(300, true, true, true))
	// .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
	// .bitmapConfig(Bitmap.Config.RGB_565).build();
	// ImageLoaderConfiguration.Builder builder = new
	// ImageLoaderConfiguration.Builder(
	// context).defaultDisplayImageOptions(defaultOptions).memoryCache(
	// new WeakMemoryCache());
	// ImageLoaderConfiguration config = builder.build();
	// ImageLoader.getInstance().init(config);
	// }

	@SuppressWarnings("deprecation")
	public static ArrayList<String> getGalleryPhotos(Activity act) {
		ArrayList<String> galleryList = new ArrayList<String>();
		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor imagecursor = act.managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			if (imagecursor != null && imagecursor.getCount() > 0) {
				while (imagecursor.moveToNext()) {
					String item = new String();
					int dataColumnIndex = imagecursor
							.getColumnIndex(MediaStore.Images.Media.DATA);
					item = imagecursor.getString(dataColumnIndex);
					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.reverse(galleryList);
		return galleryList;
	}

	public static Bitmap convertViewToBitmap(View view) {
		Bitmap bitmap = null;
		try {
			int width = view.getWidth();
			int height = view.getHeight();
			if (width != 0 && height != 0) {
				bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				view.layout(0, 0, width, height);
				view.setBackgroundColor(Color.WHITE);
				view.draw(canvas);
			}
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;

	}

	public static boolean saveImageToGallery(Context context, Bitmap bmp,
			boolean isPng) {
		File appDir = new File(Environment.getExternalStorageDirectory(),
				context.getString(R.string.app_name));
		if (!appDir.exists()) {
			if (!appDir.mkdir()) {
				return false;
			}
		}
		String fileName;
		if (isPng) {
			fileName = System.currentTimeMillis() + ".png";
		} else {
			fileName = System.currentTimeMillis() + ".jpg";
		}
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			if (isPng) {
				bmp.compress(CompressFormat.PNG, 100, fos);
			} else {
				bmp.compress(CompressFormat.JPEG, 100, fos);
			}
			bmp.recycle();
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.fromFile(appDir)));
		return true;
	}

	public static Object evaluate(float fraction, Object startValue,
			Object endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;
		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;
		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}

	public static String StrToYear(String str) {

		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy"); // 具体格式可以看API调换
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		Date date = null;
		String year = null;
		try {
			date = simpleDate.parse(str);
			year = sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return year;
	}

	public static String StrToMonth(String str) {

		SimpleDateFormat simpleDate = new SimpleDateFormat("MM"); // 具体格式可以看API调换
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		Date date = null;
		String month = null;
		try {
			date = simpleDate.parse(str);
			month = sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return month;
	}

	public static String StrToDate(String str) {

		SimpleDateFormat simpleDate = new SimpleDateFormat("dd"); // 具体格式可以看API调换
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date date = null;
		String dateTxt = null;
		try {
			date = simpleDate.parse(str);
			dateTxt = sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateTxt;
	}

}
