package com.chatapp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	
		private static final String URL = "jdbc:mysql://localhost:3306/ChatAppDB";
		private static final String USER = "root";
		private static final String PASSWORD = "";
		
		//method to authenticate an user
		public static boolean authenticateUser(String username, String password) {
			try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
				String query = "SELECT * FROM users WHERE username = ? AND password = ?";
				try(PreparedStatement statement = connection.prepareStatement(query)){
					statement.setString(1, username);
					statement.setString(2, password);
					ResultSet result = statement.executeQuery();
					return result.next();
				}
			} catch(SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
	
}
