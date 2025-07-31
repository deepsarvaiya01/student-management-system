package com.sms.model;

public class DashboardModel {

	private int srNo;
    private int studentId;
    private String name;
    private String course;
    private double paidFee;
    private double pendingFee;
    private double totalFee;
    private String subjects;
    private String teachers;
   
	public DashboardModel(int srNo, int studentId, String name, String course, double paidFee, double pendingFee,
			double totalFee, String subjects, String teachers) {
		super();
		this.srNo = srNo;
		this.studentId = studentId;
		this.name = name;
		this.course = course;
		this.paidFee = paidFee;
		this.pendingFee = pendingFee;
		this.totalFee = totalFee;
		this.subjects = subjects;
		this.teachers = teachers;
	}
	public DashboardModel() {
		// TODO Auto-generated constructor stub
	}
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
}
