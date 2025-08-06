package com.sms.model;

public class Teacher {
	private int teacherId;
	private String name;
	private String qualification;
	private double experience;

	public Teacher() {
	}

	public Teacher(String name, String qualification, double experience) {
		this.name = name;
		this.qualification = qualification;
		this.experience = experience;
	}

	public Teacher(int teacherId, String name, String qualification, double experience) {
		this.teacherId = teacherId;
		this.name = name;
		this.qualification = qualification;
		this.experience = experience;
	}

	public int getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	public String getName() {
		return name;
	}

	public String getQualification() {
		return qualification;
	}

	public double getExperience() {
		return experience;
	}

	@Override
	public String toString() {
		return String.format("%-10d %-20s %-20s %-10.1f", teacherId, name, qualification, experience);
	}
}
