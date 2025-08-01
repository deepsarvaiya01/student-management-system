package com.sms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static Connection connection = null;
	private static final Object lock = new Object();

	public DBConnection() {
	}

	public static Connection connect() throws SQLException {
		synchronized (lock) {
			if (connection == null || connection.isClosed() || !isConnectionValid()) {
				try {
					// Close existing connection if it exists
					if (connection != null && !connection.isClosed()) {
						connection.close();
					}
					
					connection = DriverManager.getConnection(DBConfig.DB_URL, DBConfig.DB_USERNAME, DBConfig.DB_PASSWORD);
					// Set auto-commit to true for better transaction management
					connection.setAutoCommit(true);
					System.out.println("Database connection established successfully");
				} catch (SQLException e) {
					System.err.println("Failed to establish database connection: " + e.getMessage());
					throw new SQLException("Failed to establish database connection: " + e.getMessage(), e);
				}
			}
			return connection;
		}
	}
	
	public static void closeConnection() {
		synchronized (lock) {
			if (connection != null) {
				try {
					if (!connection.isClosed()) {
						connection.close();
					}
				} catch (SQLException e) {
					System.err.println("Error closing database connection: " + e.getMessage());
				} finally {
					connection = null;
				}
			}
		}
	}
	
	public static boolean isConnectionValid() {
		if (connection == null) {
			return false;
		}
		try {
			return !connection.isClosed() && connection.isValid(5); // 5 second timeout
		} catch (SQLException e) {
			return false;
		}
	}
}
