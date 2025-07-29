package com.sms.model;

public class Course {
	private int course_id;
	private String course_name;
	private int no_of_semester;

	public int getCourse_id() {
		return course_id;
	}

	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}

	public String getCourse_name() {
		return course_name;
	}

	public void setCourse_name(String course_name) {
		this.course_name = course_name;
	}

	public int getNo_of_semester() {
		return no_of_semester;
	}

	public void setNo_of_semester(int no_of_semester) {
		this.no_of_semester = no_of_semester;
	}

	public Course(int course_id, String course_name, int no_of_semester) {
		super();
		this.course_id = course_id;
		this.course_name = course_name;
		this.no_of_semester = no_of_semester;
	}

	public Course() {
		super();
		// TODO Auto-generated constructor stub
	}

}
