package com.sms.model;

public enum Gender {
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
}