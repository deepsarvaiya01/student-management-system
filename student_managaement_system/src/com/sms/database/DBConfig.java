package com.sms.database;

import io.github.cdimascio.dotenv.Dotenv;

public class DBConfig {

	// Load environment variables from .env
	final static Dotenv dotenv = Dotenv.load();

	static final String DB_URL = getRequiredEnvVar("DB_URL");
	static final String DB_USERNAME = getRequiredEnvVar("DB_USERNAME");
	static final String DB_PASSWORD = getRequiredEnvVar("DB_PASSWORD");
	
	private static String getRequiredEnvVar(String key) {
		String value = dotenv.get(key);
		if (value == null || value.trim().isEmpty()) {
			throw new RuntimeException("Required environment variable '" + key + "' is not set in .env file");
		}
		return value;
	}

}
