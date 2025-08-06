package com.sms.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;

public class DBConfig {
	private static final Dotenv dotenv = Dotenv.load();
	private static final HikariDataSource dataSource;

	static {
		String dbUrl = getRequiredEnvVar("DB_URL");
		String dbUsername = getRequiredEnvVar("DB_USERNAME");
		String dbPassword = getRequiredEnvVar("DB_PASSWORD");

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(dbUrl);
		config.setUsername(dbUsername);
		config.setPassword(dbPassword);
		// Optional: Basic HikariCP settings for a console app
		config.setMaximumPoolSize(10); // Max 10 connections in the pool
		config.setMinimumIdle(2); // Minimum 2 idle connections
		config.setConnectionTimeout(30000); // 30 seconds timeout
		config.setIdleTimeout(600000); // 10 minutes idle timeout
		config.setMaxLifetime(1800000); // 30 minutes max lifetime

		dataSource = new HikariDataSource(config);
	}

	public static HikariDataSource getDataSource() {
		return dataSource;
	}

	private static String getRequiredEnvVar(String key) {
		String value = dotenv.get(key);
		if (value == null || value.trim().isEmpty()) {
			throw new RuntimeException("Required environment variable '" + key + "' is not set in .env file");
		}
		return value;
	}
}