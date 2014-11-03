package com.beiyuan.appyujing.data;

public class ConstantData {


	public static final String IP = "sc1.hebeinu.edu.cn";


	public static final String SENDIMAGEIP = "http://" + IP
			+ "/school5/uploadFile.html";
	public static final String SENDMAPMESSAGE = "http://" + IP
			+ "/school5/importData.html";
	public static final String SENDMAPSTUDENTINFO = "http://" + IP
			+ "/school5/mapInStudent.html";
	public static final String SENDLOGININFO = "http://" + IP
			+ "/school5/loginCheck.html";

	public static final String TEST = "http://" + IP
			+ "/school5/checkStuCallOver.html";

	public static final String GETCOLLEGEGRA = "http://" + IP
			+ "/school5/requestCollGra.html";
	public static final String GETPROFESSION = "http://" + IP
			+ "/school5/requestProfession.html";
	public static final String GETCLASS = "http://" + IP
			+ "/school5/requestClass.html";
	public static final String GETCOURSE = "http://" + IP
			+ "/school5/getClassName.html";
	public static final String GETATTENDENCESTUDENT = "http://" + IP
			+ "/school5/checkStuCallOver.html";
	public static final String SENDATTENDENCESHEET = "http://" + IP
			+ "/school5/saveOutcome.html";

	public static final String GETQUERYATTENDSTUDENT = "http://" + IP
			+ "/school5/checkOutcome.html";

	public static final String REGISTERSTUDENT = "http://" + IP
			+ "/school5/registerStudent.html";

	public static final String GETTEACHERCOLLEGE = "http://" + IP
			+ "/school5/requestCollege.html";
	public static final String GETTEACHERGRADE = "http://" + IP
			+ "/school5/requestGrade.html";

	public static final String REGISTERHEADTEACHER = "http://" + IP
			+ "/school5/registerHeadteacher.html";
	public static final String REGISTERTEACHER = "http://" + IP
			+ "/school5/registerTeacher.html";
	public static final String STUDENTGETEMERGE = "http://" + IP
			+ "/school5/emerStudent.html";

	public static final String CHECKNEWS = "http://" + IP
			+ "/school5/newsSearch/client.html";

	public static final String EMEGETLEADER = "http://" + IP
			+ "/school5/emerGetLeader.html";
	public static final String EMEGETLEADERTEACHER = "http://" + IP
			+ "/school5/getHeadteacherLeader.html";
	// 领导根据学院名字得到辅导员信息
	public static final String EMEGETTEACHERBYCOLLEGE = "http://" + IP
			+ "/school5/emerGetHeadTeacherByCollegeName.html";
	// 领导、辅导员根据班级名字获取学生和辅导员数据
	public static final String EMELEADERGETSTUDENT = "http://" + IP
			+ "/school5/emerStudentAndTeacher.html";

	public static final String EMEGETLEADERBYTEACHER = "http://" + IP
			+ "/school5/emerGetLeader.html";
	// 根据辅导员的昵称给客户端提交年级、专业、班级信息
	public static final String EMEGETCLASSBYTEACHER = "http://" + IP
			+ "/school5/getGPC.html";

	public static final String STUDENTINFOCHANGE = "http://" + IP
			+ "/school5/studentChange.html";
	public static final String TEACHERINFOCHANGE = "http://" + IP
			+ "/school5/teacherChange.html";
	public static final String HEADTEACHERINFOCHANGE = "http://" + IP
			+ "/school5/headTeacherChange.html";

	// 根据学号、节数返回 当天该学生的考勤情况
	public static final String ATTENDUPDATE = "http://" + IP
			+ "/school5/updateOutcome.html";
	// 更新后上传数据
	public static final String SENDATTENDUPDATE = "http://" + IP
			+ "/school5/updateStudentOutcome.html";

	// 领导快速查询
	public static final String QUICKLEADER = "http://" + IP
			+ "/school5/checkOutcomeByCollegeName.html";
	// 通过学号返回该学生的详细班级信息
	public static final String QUICKLEADERBYSTUNUM = "http://" + IP
			+ "/school5/getStudentInformation.html";

	// 辅导员快速查看考勤
	public static final String QUICKTEACHER = "http://" + IP
			+ "/school5/checkOutcomeByGradeID.html";

	public static final String MAPGETCLASS = "http://" + IP
			+ "/school5/mapOutNumber.html"; // 地图教师端获取班级和信息数量
	public static final String SENDHEADPHOTO = "http://" + IP
			+ "/school5/uploadHeadSculpture.html";

	// 地图教师端获取班级和信息数量
	public static final String MAPGETWEIBO = "http://" + IP
			+ "/school5/getLongLatByGrade.html";

	// 学生查看个人信息
	public static final String SHOWSTUINFO = "http://" + IP
			+ "/school5/studentShow.html";
	// 学生修改个人信息
	public static final String MODYSTUINFO = "http://" + IP
			+ "/school5/mapOutLongLatNumber.html";
	// 教师查看个人信息
	public static final String SHOWTEAINFO = "http://" + IP
			+ "/school5/teacherShow.html";
	// 教师修改个人信息
	public static final String MODYTEAINFO = "http://" + IP
			+ "/school5/teacherChange.html";
	// 辅导员查看个人信息
	public static final String SHOWHEADTEAINFO = "http://" + IP
			+ "/school5/headteacherShow.html";
	// 辅导员修改个人信息
	public static final String MODYHEADTEAINFO = "http://" + IP
			+ "/school5/headTeacherChange.html";
	 //领导查看个人信息
	 public static final String SHOWLEADERINFO = "http://" + IP
	 + "/school5/leaderShow.html";


}
