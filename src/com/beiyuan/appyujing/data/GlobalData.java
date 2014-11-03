package com.beiyuan.appyujing.data;

public class GlobalData {
	// private static String username = "leader";
	// private static String role = "Role_Leader";
	private static String username = "";
	private static String role = "";
	private static double Lat1, Lon1;

	// private static String username = "娟";
	// private static String role = "Role_Student";

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		GlobalData.username = username;
	}

	public static String getRole() {
		return role;
	}

	public static void setRole(String role) {
		GlobalData.role = role;
	}

	public static void setLat(double Lat1) {
		GlobalData.Lat1 = Lat1;
	}

	public static double getLat() {
		return Lat1;
	}

	public static void setLon(double Lon1) {
		GlobalData.Lon1 = Lon1;
	}

	public static double getLon() {
		return Lon1;
	}
}
