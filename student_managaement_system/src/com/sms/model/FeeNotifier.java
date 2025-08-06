package com.sms.model;

public class FeeNotifier {

	private int studentId;
	private boolean smsEnabled;
	private boolean emailEnabled;
	private boolean whatsappEnabled;

	public FeeNotifier() {
	}

	public FeeNotifier(int studentId, boolean smsEnabled, boolean emailEnabled, boolean whatsappEnabled) {
		this.studentId = studentId;
		this.smsEnabled = smsEnabled;
		this.emailEnabled = emailEnabled;
		this.whatsappEnabled = whatsappEnabled;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public boolean isSmsEnabled() {
		return smsEnabled;
	}

	public void setSmsEnabled(boolean smsEnabled) {
		this.smsEnabled = smsEnabled;
	}

	public boolean isEmailEnabled() {
		return emailEnabled;
	}

	public void setEmailEnabled(boolean emailEnabled) {
		this.emailEnabled = emailEnabled;
	}

	public boolean isWhatsappEnabled() {
		return whatsappEnabled;
	}

	public void setWhatsappEnabled(boolean whatsappEnabled) {
		this.whatsappEnabled = whatsappEnabled;
	}

}
