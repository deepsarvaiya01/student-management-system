package com.sms.model;

public enum Gender {
	M("M"), F("F"), O("O");

	private final String displayName;

	Gender(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static Gender fromString(String input) {
		if (input == null) {
			return null;
		}
		switch (input.toUpperCase()) {
		case "M":
			return M;
		case "F":
			return F;
		case "O":
			return O;
		default:
			return null;
		}
	}
}