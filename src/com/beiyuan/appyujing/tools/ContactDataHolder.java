package com.beiyuan.appyujing.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.ContactsContract;

@SuppressWarnings("deprecation")
public class ContactDataHolder {

	private static boolean init = false;
	private static List<ContactItemData> contactDataList;

	public static List<ContactItemData> getContactList(Context context) {
		if (!init) {
			contactDataList = buildContactList(context);
			init = true;
		}
		return contactDataList;
	}

	public static List<ContactItemData> buildContactList(Context context) {
		List<ContactItemData> phoneList = new ArrayList<ContactItemData>();
		List<ContactItemData> simList = new ArrayList<ContactItemData>();
		try {
			phoneList = getFromPhone(context);
		} catch (Exception e) {
			// do nothing
		}
		try {
			simList = getFromSim(context);
		} catch (Exception e) {
			// do nothing
		}
		// 去重，重新组合，排序
		return buildContactList(phoneList, simList);
	}

	private static List<ContactItemData> buildContactList(
			List<ContactItemData> phoneList, List<ContactItemData> simList) {
		List<ContactItemData> result = new ArrayList<ContactItemData>();
		if (phoneList != null) {
			result.addAll(phoneList);
		}
		if (simList != null) {
			for (ContactItemData simData : simList) {
				if (!contains(simData, phoneList)) {
					result.add(simData);
				}
			}
		}
		// 根据a-z进行排序源数据
		Collections.sort(result, new Comparator<ContactItemData>() {
			public int compare(ContactItemData o1, ContactItemData o2) {
				if (o1.getSortLetters().equals("@")
						|| o2.getSortLetters().equals("#")) {
					return -1;
				} else if (o1.getSortLetters().equals("#")
						|| o2.getSortLetters().equals("@")) {
					return 1;
				} else {
					return o1.getSortLetters().compareTo(o2.getSortLetters());
				}
			}
		});
		return result;
	}

	private static boolean contains(ContactItemData simData,
			List<ContactItemData> phoneList) {
		if (phoneList == null || phoneList.size() <= 0) {
			return false;
		}
		for (ContactItemData phoneData : phoneList) {
			if (phoneData.getName().equals(simData.getName())) {
				return true;
			}
		}
		return false;
	}

	public static List<ContactItemData> getFromPhone(Context context) {
		ContentResolver resolver = context.getContentResolver();
		// 读取手机通讯录
		List<ContactItemData> list = new ArrayList<ContactItemData>();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor cur = resolver.query(uri, new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME }, null, null,
				ContactsContract.Contacts.SORT_KEY_PRIMARY);
		// String select = "((" + Contacts.HAS_PHONE_NUMBER + "=1) AND (" +
		// Contacts.DISPLAY_NAME + " NOTNULL) AND (" + Contacts.DISPLAY_NAME +
		// " != '' ))";
		// Cursor cur = resolver.query(ContactsContract.Contacts.CONTENT_URI,
		// new String[]{ContactsContract.Contacts._ID,
		// ContactsContract.Contacts.DISPLAY_NAME}, select, null, null);
		while (cur.moveToNext()) {
			String _id = cur.getString(cur
					.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cur.getString(cur
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			Cursor pcur = resolver
					.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=" + _id, null, null);
			// 处理多个号码的情况
			while (pcur.moveToNext()) {
				String number = pcur
						.getString(pcur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				ContactItemData data = new ContactItemData();
				// id、name都是一样的，号码不一样
				data.setId(_id);
				data.setName(name);
				number = formatNumber(number);
				if (number == null) {
					continue;
				}
				data.setNumber(number);

				// 汉字转换成拼音
				String pinyin = CharacterParser.getInstance().getSelling(name);
				String sortString = pinyin.substring(0, 1).toUpperCase(
						Locale.CHINA);
				// 判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					data.setSortLetters(sortString.toUpperCase(Locale.CHINA));
				} else {
					data.setSortLetters("#");
				}

				list.add(data);
			}
			pcur.close();
		}
		cur.close();
		return list;
	}

	public static List<ContactItemData> getFromSim(Context context) {
		ContentResolver resolver = context.getContentResolver();
		List<ContactItemData> list = new ArrayList<ContactItemData>();
		Uri uri = Uri.parse("content://icc/adn");
		Cursor cursor = resolver.query(uri, new String[] { People._ID,
				People.NAME, People.NUMBER }, null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex(People._ID));
			String name = cursor.getString(cursor.getColumnIndex(People.NAME));
			String number = cursor.getString(cursor
					.getColumnIndex(People.NUMBER));
			ContactItemData data = new ContactItemData();
			data.setId(id);
			data.setName(name);
			number = formatNumber(number);
			if (number == null) {
				continue;
			}
			data.setNumber(number);
			// 汉字转换成拼音
			String pinyin = CharacterParser.getInstance().getSelling(name);
			String sortString = pinyin.substring(0, 1)
					.toUpperCase(Locale.CHINA);
			// 判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				data.setSortLetters(sortString.toUpperCase(Locale.CHINA));
			} else {
				data.setSortLetters("#");
			}
			list.add(data);
		}
		cursor.close();

		return list;
	}

	public static String formatNumber(String number) {
		if (number == null || number.length() <= 0) {
			return null;
		}
		if (number.startsWith("+")) {
			number = number.substring(3);
		}
		number = number.replaceAll("\\s+", "");
		if (number.matches("1[3458]\\d{9}")) {
			return number;
		}
		return null;
	}

	public static void destroy() {
		init = false;
		if (contactDataList != null) {
			contactDataList.clear();
			contactDataList = null;
		}
	}
}
