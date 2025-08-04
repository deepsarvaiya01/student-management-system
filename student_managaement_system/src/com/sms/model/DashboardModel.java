package com.sms.model;

public class DashboardModel {

	private int srNo;

	// For Student General Dashboard
	private int studentId;
	private String name;
	private String course;
	private double paidFee;
	private double pendingFee;
	private double totalFee;
	private String subjects;
	private String teachers;

	// For Course Dashboard
	private int totalStudents;

	// For Subject Dashboard
	private String subject;

	// For Teacher Load Dashboard
	private int teacherId;
	private String teacherName;
	private int totalSubjects;

	public int getSrNo() {
		return srNo;
	}
	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public int getStudentId() {
		return studentId;
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}

	public double getPaidFee() {
		return paidFee;
	}
	public void setPaidFee(double paidFee) {
		this.paidFee = paidFee;
	}

	public double getPendingFee() {
		return pendingFee;
	}
	public void setPendingFee(double pendingFee) {
		this.pendingFee = pendingFee;
	}

	public double getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}

	public String getSubjects() {
		return subjects;
	}
	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public String getTeachers() {
		return teachers;
	}
	public void setTeachers(String teachers) {
		this.teachers = teachers;
	}

	public int getTotalStudents() {
		return totalStudents;
	}
	public void setTotalStudents(int totalStudents) {
		this.totalStudents = totalStudents;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public int getTotalSubjects() {
		return totalSubjects;
	}
	public void setTotalSubjects(int totalSubjects) {
		this.totalSubjects = totalSubjects;
	}
}
