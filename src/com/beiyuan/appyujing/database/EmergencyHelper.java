package com.beiyuan.appyujing.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.beiyuan.appyujing.database.table.ClassMates;
import com.beiyuan.appyujing.database.table.HeadTeacher;
import com.beiyuan.appyujing.database.table.Leader;

public class EmergencyHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "Emergency.db";
	private static final int DB_VESION = 1;

	public EmergencyHelper(Context context) {

		super(context, DB_NAME, null, DB_VESION);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 创建接口 实现各表的创建
	 */
	public static interface TableCreateInterface {
		/**
		 * 创建表
		 * 
		 * @param db
		 */
		public void onCreate(SQLiteDatabase db);

		/**
		 * 更新表
		 * 
		 * @param db
		 * @param oldVersion
		 * @param newVersion
		 */
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Leader.getInstance().onCreate(db);
		HeadTeacher.getInstance().onCreate(db);
		ClassMates.getInstance().onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Leader.getInstance().onUpgrade(db, oldVersion, newVersion);
		HeadTeacher.getInstance().onUpgrade(db, oldVersion, newVersion);
		ClassMates.getInstance().onUpgrade(db, oldVersion, newVersion);
	}

}
