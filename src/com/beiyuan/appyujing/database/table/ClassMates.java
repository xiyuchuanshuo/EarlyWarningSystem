package com.beiyuan.appyujing.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beiyuan.appyujing.database.EmergencyHelper;
import com.beiyuan.appyujing.database.EmergencyHelper.TableCreateInterface;

public class ClassMates implements TableCreateInterface {

	private static String tableName = "classmates";
	private static String _id = "_id";
	private static String studentname = "studentname";
	private static String studentphone = "studentphone";
	private static ClassMates classmates = new ClassMates();

	public static ClassMates getInstance() {

		return ClassMates.classmates;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + ClassMates.tableName + " (  "
				+ ClassMates._id + " integer primary key,"
				+ ClassMates.studentname + " TEXT," + ClassMates.studentphone
				+ " TEXT)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion < newVersion) {
			String sql = "DROP TABLE IF EXISTS " + ClassMates.tableName;
			db.execSQL(sql);
			this.onCreate(db);
		}
	}

	/**
	 * 插入学生联系人
	 * 
	 * @param emergencyHelper
	 * @param name
	 * @param phone
	 */
	public static void insertStudent(EmergencyHelper emergencyHelper,
			String name, String phone) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(studentname, name);
		contentValues.put(studentphone, phone);
		db.insert(tableName, null, contentValues);
		db.close();
	}

	/**
	 * 删除学生
	 * 
	 * @param emergencyHelper
	 * @param id
	 */
	public void deleteStudent(EmergencyHelper emergencyHelper, int id) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		db.delete(tableName, ClassMates._id + "=?", new String[] { id + "" });
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
	 * 更新学生
	 * 
	 * @param emergencyHelper
	 * @param name
	 * @param phone
	 * @param id
	 */
	public void updateStudent(EmergencyHelper emergencyHelper, String name,
			String phone, int id) {
		SQLiteDatabase db = emergencyHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(studentname, name);
		contentValues.put(studentphone, phone);
		db.update(tableName, contentValues, "_id = ?", new String[] { id + "" });
	}

	/**
	 * 获取学生联系人
	 * 
	 * @param emergencyHelper
	 * @return
	 */
	public static Cursor getStudentName(EmergencyHelper emergencyHelper) {
		SQLiteDatabase db = emergencyHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, null, null, null, null,
				ClassMates.studentname + " COLLATE LOCALIZED ASC");
		cursor.moveToFirst();
		return cursor;
	}

	/**
	 * 根据名字获取学生联系人
	 * 
	 * @param emergencyHelper
	 * @return
	 */
	public static Cursor getStudentName(EmergencyHelper emergencyHelper,
			String name) {
		SQLiteDatabase db = emergencyHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, ClassMates.studentname
				+ " like ?", new String[] { "%" + name + "%" }, null, null,
				ClassMates.studentname + " COLLATE LOCALIZED ASC");
		cursor.moveToFirst();
		return cursor;
	}

	/**
	 * 根据id获取学生联系人
	 * 
	 * @param emergencyHelper
	 * @return
	 */
	public static Cursor getStudentName(EmergencyHelper emergencyHelper, int id) {
		SQLiteDatabase db = emergencyHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, ClassMates._id + "=?",
				new String[] { id + "" }, null, null, ClassMates.studentname
						+ " COLLATE LOCALIZED ASC");
		cursor.moveToFirst();
		return cursor;
	}

}
