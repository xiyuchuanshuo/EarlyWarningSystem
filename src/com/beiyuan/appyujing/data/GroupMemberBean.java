package com.beiyuan.appyujing.data;

public class GroupMemberBean {

	private String name; // 显示的名字
	private String phone; // 显示的电话
	private String sortLetters; // 显示数据拼音的首字母

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
