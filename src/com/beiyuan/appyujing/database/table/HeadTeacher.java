package com.beiyuan.appyujing.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beiyuan.appyujing.database.EmergencyHelper;
import com.beiyuan.appyujing.database.EmergencyHelper.TableCreateInterface;

public class HeadTeacher implements TableCreateInterface {
	private static String tableName = "headteacher";
	private static String _id = "_id";
	private static String tname = "tname";
	private static String tphone = "tphone";
	private static HeadTeacher teacher = new HeadTeacher();

	public static HeadTeacher getInstance() {

		return HeadTeacher.teacher;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + HeadTeacher.tableName + " (  "
				+ HeadTeacher._id + " integer primary key," + HeadTeacher.tname
				+ " TEXT," + HeadTeacher.tphone + " TEXT)";
		db.execSQL(sql);
	}

	// TODO Auto-generated method stub

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion < newVersion) {
			String sql = "DROP TABLE IF EXISTS " + HeadTeacher.tableName;
			db.execSQL(sql);
			this.onCreate(db);
		}
	}

	/**
	 * 插入辅导员
	 * 
	 * @param emergencyHelper
	 * @param name
	 * @param phone
	 */
	public static void insertTeacher(EmergencyHelper emergencyHelper,
			String name, String phone) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(tname, name);
		contentValues.put(tphone, phone);
		db.insert(tableName, null, contentValues);
		db.close();
	}

	/**
	 * 删除辅导员
	 * 
	 * @param emergencyHelper
	 * @param id
	 */
	public void deleteTeacher(EmergencyHelper emergencyHelper, int id) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		db.delete(tableName, HeadTeacher._id + "=?", new String[] { id + "" });
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
	 * 更新辅导员
	 * 
	 * @param emergencyHelper
	 * @param name
	 * @param phone
	 * @param id
	 */
	public void updateTeacher(EmergencyHelper emergencyHelper, String name,
			String phone, int id) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(tname, name);
		contentValues.put(tphone, phone);
		db.update(tableName, contentValues, "_id = ?", new String[] { id + "" });
	}

	/**
	 * 获取辅导员联系人
	 * 
	 * @param emergencyHelper
	 * @return
	 */
	public static Cursor getTeacherName(EmergencyHelper emergencyHelper) {
		SQLiteDatabase db = emergencyHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, null, null, null, null,
				HeadTeacher.tname + " COLLATE LOCALIZED ASC");
		cursor.moveToFirst();
		return cursor;
	}

	/**
	 * 根据名字获取辅导员联系人
	 * 
	 * @param emergencyHelper
	 * @return
	 */
	public static Cursor getTeacherName(EmergencyHelper emergencyHelper,
			String name) {
		SQLiteDatabase db = emergencyHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null,
				HeadTeacher.tname + " like ?",
				new String[] { "%" + name + "%" }, null, null,
				HeadTeacher.tname + " COLLATE LOCALIZED ASC");
		cursor.moveToFirst();
		return cursor;
	}

	/**
	 * 根据id获取辅导员联系人
	 * 
	 * @param emergencyHelper
	 * @return
	 */
	public static Cursor getTeacherName(EmergencyHelper emergencyHelper, int id) {
		SQLiteDatabase db = emergencyHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, HeadTeacher._id + "=?",
				new String[] { id + "" }, null, null, HeadTeacher.tname
						+ " COLLATE LOCALIZED ASC");
		cursor.moveToFirst();
		return cursor;
	}

}
