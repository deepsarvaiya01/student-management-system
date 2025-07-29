package com.sms.database;

import io.github.cdimascio.dotenv.Dotenv;

public class DBConfig {

	// Load environment variables from .env
	final static Dotenv dotenv = Dotenv.load();

	static final String DB_URL = dotenv.get("DB_URL");
	static final String DB_USERNAME = dotenv.get("DB_USERNAME");
	static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

}
