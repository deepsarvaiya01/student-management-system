package com.sms.model;

public class Student {
	private int student_id;
	private String name;
	private int age;
	private int gr_number;
	private String email;
	private String city;
	private String mobile_no;
	private boolean is_active;
	private Gender gender;

	public Student() {
	}

	public Student(int student_id, String name, int age, int gr_number, String email, String city, String mobile_no,
			Gender gender) {
		this.student_id = student_id;
		this.name = name;
		this.age = age;
		this.gr_number = gr_number;
		this.email = email;
		this.city = city;
		this.mobile_no = mobile_no;
		this.gender = gender;
	}

	public Student(int student_id, String name, String email, int gr_number, Gender gender) {
		this.student_id = student_id;
		this.name = name;
		this.gr_number = gr_number;
		this.email = email;
		this.gender = gender;
	}

	public int getStudent_id() {
		return student_id;
	}

	public void setStudent_id(int student_id) {
		this.student_id = student_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getGr_number() {
		return gr_number;
	}

	public void setGr_number(int gr_number) {
		this.gr_number = gr_number;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getMobile_no() {
		return mobile_no;
	}

	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}

	public boolean isIs_active() {
		return is_active;
	}

	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public static void printHeader() {
		System.out.printf("\n%-5s %-20s %-5s %-10s %-25s %-15s %-15s %-10s\n", "ID", "Name", "Age", "GR No", "Email",
				"City", "Mobile No", "Gender");
		System.out.println(
				"---------------------------------------------------------------------------------------------------");
	}

	@Override
	public String toString() {
		return String.format("%-5d %-20s %-5d %-10d %-25s %-15s %-15s %-10s", student_id, name, age, gr_number, email,
				city, mobile_no, gender != null ? gender.getDisplayName() : "N/A");
	}
}