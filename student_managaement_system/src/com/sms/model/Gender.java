package com.sms.model;

public enum Gender {
<<<<<<< HEAD
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

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
                return MALE;
            case "F":
                return FEMALE;
            case "O":
                return OTHER;
            default:
                return null;
        }
    }
=======
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
>>>>>>> a25ea17c9c2574802c7838058077a39716e03a1e
}