package com.beiyuan.appyujing.data;

public class StudentData {
	private String id;
	private String name;
	private String number;
	private String photo;
	private String status;
	private String oldStatus;

	private String dateMonth;
	private String date;
	private String courseName;
	private String teacherName;

	public StudentData() {
		super();
	}

	public StudentData(String id, String name, String number, String photo,
			String status,String newStatus) {
		super();
		this.id = id;
		this.name = name;
		this.number = number;
		this.photo = photo;
		this.status = status;
		
	}

	public StudentData(String date, String number, String courseName,
			String teacherName) {
		super();
		this.date = date;
		this.number = number;
		this.courseName = courseName;
		this.teacherName = teacherName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	

	public String getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(String oldStatus) {
		this.oldStatus = oldStatus;
	}

	public String getDateMonth() {
		return dateMonth;
	}

	public void setDateMonth(String dateMonth) {
		this.dateMonth = dateMonth;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "StudentData [id=" + id + ", name=" + name + ", number="
				+ number + ", photo=" + photo + ", status=" + status + "]";
	}

}
