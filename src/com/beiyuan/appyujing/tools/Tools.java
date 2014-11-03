package com.beiyuan.appyujing.tools;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * 该方法用于 android程序无返回值类型的tools
 * 
 */
public class Tools {

	public static void mToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	public static void mToast(Context context, int msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 进度框提示
	 * 
	 * @param con
	 * @param msg
	 *            提示的消息内容
	 * @return
	 */
	public static ProgressDialog pd(Context con) {
		return ProgressDialog.show(con, "数据加载中...", "请稍后....", true, true);
	}

	public static ProgressDialog pd(Context con, String msg) {
		return ProgressDialog.show(con, "请稍后", msg, true, true);
	}
	public static ProgressDialog pd(Context con, int msg) {
		return ProgressDialog.show(con, "请稍后", msg+"", true, true);
	}

	//

	/**
	 * 
	 * @param activity
	 */
	private static Boolean isExit = false;

	public static void exitBy2Click(Context context) {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			System.exit(0);
		}
	}
}
