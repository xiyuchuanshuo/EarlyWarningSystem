package com.beiyuan.appyujing.data;

public class StudentInfo {

	private String longLatDate;
	private String geography;
	private String speechURL;
	private String studentNumber;
	private String studentName;
	private String word;
	private String longitude;
	private String latitude;
	private String thumbnailPictureURL;
	private String pictureURL;

	public StudentInfo(String longLatDate, String geography, String speechURL,
			String studentNumber, String studentName, String word,
			String longitude, String latitude, String thumbnailPictureURL,
			String pictureURL) {
		super();
		this.longLatDate = longLatDate;
		this.geography = geography;
		this.speechURL = speechURL;
		this.studentNumber = studentNumber;
		this.studentName = studentName;
		this.word = word;
		this.longitude = longitude;
		this.latitude = latitude;
		this.thumbnailPictureURL = thumbnailPictureURL;
		this.pictureURL = pictureURL;
	}

	public String getLongLatDate() {
		return longLatDate;
	}

	public void setLongLatDate(String longLatDate) {
		this.longLatDate = longLatDate;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getSpeechURL() {
		return speechURL;
	}

	public void setSpeechURL(String speechURL) {
		this.speechURL = speechURL;
	}

	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getThumbnailPictureURL() {
		return thumbnailPictureURL;
	}

	public void setThumbnailPictureURL(String thumbnailPictureURL) {
		this.thumbnailPictureURL = thumbnailPictureURL;
	}

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

}
