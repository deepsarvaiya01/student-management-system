package com.sms.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
	private static final Object lock = new Object();

	public static Connection connect() throws SQLException {
		synchronized (lock) {
			try {
				Connection connection = DBConfig.getDataSource().getConnection();
				connection.setAutoCommit(true); // Maintain your auto-commit setting
				System.out.println("Database connection obtained from HikariCP pool");
				return connection;
			} catch (SQLException e) {
				System.err.println("Failed to obtain database connection: " + e.getMessage());
				throw new SQLException("Failed to obtain database connection: " + e.getMessage(), e);
			}
		}
	}

	public static void closeConnection(Connection connection) {
		synchronized (lock) {
			if (connection != null) {
				try {
					if (!connection.isClosed()) {
						connection.close(); // Returns connection to the pool
						System.out.println("Database connection returned to HikariCP pool");
					}
				} catch (SQLException e) {
					System.err.println("Error closing database connection: " + e.getMessage());
				}
			}
		}
	}

	public static boolean isConnectionValid(Connection connection) {
		if (connection == null) {
			return false;
		}
		try {
			return !connection.isClosed() && connection.isValid(5); // 5-second timeout
		} catch (SQLException e) {
			return false;
		}
	}
}