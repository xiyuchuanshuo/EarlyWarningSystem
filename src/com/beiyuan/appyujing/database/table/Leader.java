package com.beiyuan.appyujing.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beiyuan.appyujing.database.EmergencyHelper;
import com.beiyuan.appyujing.database.EmergencyHelper.TableCreateInterface;

public class Leader implements TableCreateInterface {

	private static String tableName = "leader";
	private static String _id = "_id";
	private static String leadername = "leadername";
	private static String leaderphone = "leaderphone";
	private static Leader leader = new Leader();

	public static Leader getInstance() {

		return Leader.leader;
	}

	/*
	 * (non-Javadoc)创建学生表
	 * 
	 * @see
	 * com.beiyuan.appyujing.database.EmergencyHelper.TableCreateInterface#onCreate
	 * (android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + Leader.tableName + " (  " + Leader._id
				+ " integer primary key," + Leader.leadername + " TEXT,"
				+ Leader.leaderphone + " TEXT)";
		db.execSQL(sql);
	}

	/*
	 * (non-Javadoc)更新数据库
	 * 
	 * @see
	 * com.beiyuan.appyujing.database.EmergencyHelper.TableCreateInterface#onUpgrade
	 * (android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion < newVersion) {
			String sql = "DROP TABLE IF EXISTS " + Leader.tableName;
			db.execSQL(sql);
			this.onCreate(db);
		}
	}

	/**
	 * 插入领导联系人
	 * 
	 * @param emergencyHelper
	 * @param name
	 * @param phone
	 */
	public static void insertLeader(EmergencyHelper emergencyHelper,
			String name, String phone) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(leadername, name);
		contentValues.put(leaderphone, phone);
		db.insert(tableName, null, contentValues);
		db.close();
	}

	/**
	 * 删除领导联系人
	 * 
	 * @param emergencyHelper
	 * @param id
	 */
	public void deleteLeader(EmergencyHelper emergencyHelper, int id) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		db.delete(tableName, Leader._id + "=?", new String[] { id + "" });
		db.close();
	}

	/**
	 * 删除数据表中的所有数据
	 * 
	 * @param emergencyHelper
	 * @param id
	 */
	public static void deletetable(EmergencyHelper emergencyHelper) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		db.delete(tableName, null, null);
		db.close();
	}

	/**
	 * 更新联系人
	 * 
	 * @param emergencyHelper
	 * @param name
	 * @param phone
	 * @param id
	 */
	public void updateLeader(EmergencyHelper emergencyHelper, String name,
			String phone, int id) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(leadername, name);
		contentValues.put(leaderphone, phone);
		db.update(tableName, contentValues, "_id = ?", new String[] { id + "" });
	}

	/**
	 * 获取领导联系人
	 * 
	 * @param emergencyHelper
	 * @return
	 */
	public static Cursor getLeaderName(EmergencyHelper emergencyHelper) {
		SQLiteDatabase db = emergencyHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, null, null, null, null,
				Leader.leadername + " COLLATE LOCALIZED ASC");
		cursor.moveToFirst();
		return cursor;
	}

	/**
	 * 根据名字获取相应领导联系人
	 * 
	 * @param emergencyHelper
	 * @return
	 */
	public static Cursor getLeaderName(EmergencyHelper emergencyHelper,
			String name) {
		SQLiteDatabase db = emergencyHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null,
				Leader.leadername + " like ?",
				new String[] { "%" + name + "%" }, null, null,
				Leader.leadername + " COLLATE LOCALIZED ASC");
		cursor.moveToFirst();
		return cursor;
	}

	/**
	 * 根据id获取相应领导联系人
	 * 
	 * @param emergencyHelper
	 * @return
	 */
	public static Cursor getLeaderName(EmergencyHelper emergencyHelper, int id) {
		SQLiteDatabase db = emergencyHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, Leader._id + "=?",
				new String[] { id + "" }, null, null, Leader.leadername
						+ " COLLATE LOCALIZED ASC");
		cursor.moveToFirst();
		return cursor;
	}

}
