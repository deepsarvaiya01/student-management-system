package com.sms.model;

public class DashboardModel {
	private int srNo;
	private int studentId;
	private String name;
	private String city;
	private int age;
	private String course;
	private double totalFee;
	private double paidFee;
	private double pendingFee;
	private String subjects;
	private String subjectType;
	private String teachers;
	private int totalStudents;
	private int totalSubjects;
	private int teacherId;
	private String teacherName;
	private String studentList; // Added for course-wise student names

	// Getters and Setters
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
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

	public String getSubjects() {
		return subjects;
	}

	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
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

	public int getTotalSubjects() {
		return totalSubjects;
	}

	public void setTotalSubjects(int totalSubjects) {
		this.totalSubjects = totalSubjects;
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

	public String getStudentList() {
		return studentList;
	}

	public void setStudentList(String studentList) {
		this.studentList = studentList;
	}
}