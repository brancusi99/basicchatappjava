package com.chatapp.server;

import static com.chatapp.server.Database.getPassword;
import static com.chatapp.server.Database.getUrl;
import static com.chatapp.server.Database.getUser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * this class handles the database of the program and data manipulation
 */

public class Database {
	
		private static final String URL = "jdbc:mysql://localhost:3306/ChatAppDB";
		private static final String USER = "root";
		private static final String PASSWORD = "";
		
		public static String getUser() {
			return USER;
		}
		
		public static String getUrl() {
			return URL;
		}
		
		public static String getPassword() {
			return PASSWORD;
		}
		
		//method to authenticate an user
		
		/**
		 * Searches the users table and compares the introduced
		 * username and password to the ones in the table
		 * It stores any matching rows in a "ResultSet" object
		 * @param username
		 * @param password
		 * @return true if there is at least one rows in "ResultSet" that matches the provided username and password
		 */
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
		
		public static String getCreationTimestamp(String username) {
			try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
				String query = "SELECT created_at FROM users WHERE username = ?";
				try(PreparedStatement statement = connection.prepareStatement(query)){
					statement.setString(1, username);
					ResultSet result = statement.executeQuery();
					if(result.next())
						return result.getString("created_at");
					else
						return null;
				}
			} catch(SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		/**
		 * Verifies if the username already exists in the database
		 * @param username
		 * @return true if it already exists, false if not
		 */
		
		public static boolean userExists(String username) {
			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
				String query = "SELECT * FROM users WHERE username = ?";
				try(PreparedStatement statement = connection.prepareStatement(query)){
					statement.setString(1, username);
					ResultSet result = statement.executeQuery();
					return result.next();
				}
			} catch(SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		/**
		 * Inserts the new username and password into the database
		 * @param username new user
		 * @param password new user's password
		 * @return true if successful, false if not
		 */
		
		public static boolean createUser(String username, String password) {
			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
				String query = "INSERT INTO users (username, password) VALUES (?, ?)";
				try(PreparedStatement statement = connection.prepareStatement(query)){
					statement.setString(1, username);
					statement.setString(2, password);
					int rowsAffected = statement.executeUpdate();
					return rowsAffected > 0;
				}
			} catch(SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
/*
		public static void createUser(String username, String email, String password, String details, String imagePath) {

	        String sql = "INSERT INTO users (username, email, password, details, profile_image) VALUES (?, ?, ?, ?, ?)";
	        try (
	                Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
	                PreparedStatement preparedStatement = connection.prepareStatement(sql);
	                InputStream imageStream = new FileInputStream(imagePath)
	        ) {
	            preparedStatement.setString(1, username);
	            preparedStatement.setString(2, email);
	            preparedStatement.setString(3, password);
	            preparedStatement.setString(4, details);
	            preparedStatement.setBlob(5, imageStream);
	            preparedStatement.executeUpdate();
	            System.out.println("User inserat cu succes!");
	        } catch (Exception e) {
	            System.out.println("Eroare la inserarea userului: " + e.getMessage());
	        }
	    }
*/
	    public static int getUserId(String username) {
	    	try(
	    			Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
	    			){
	    			String sql = "SELECT id FROM users WHERE username = ?";
	    			try(
	    					PreparedStatement preparedStatement = connection.prepareStatement(sql)
	    				){
	    				preparedStatement.setString(1, username);
	    				ResultSet result = preparedStatement.executeQuery();
	    				if(result.next())
	    					return result.getInt("id");
	    			}
	    	} catch(SQLException e) {
	    		e.printStackTrace();
	    	}
	    		
	    	return -1;
	    }
	    
	    
	    public static void insertMessage(int senderId, int receiverId, String content) {

	        String sql = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
	        try (
	                Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
	                PreparedStatement preparedStatement = connection.prepareStatement(sql)
	        ) {
	            preparedStatement.setInt(1, senderId);
	            preparedStatement.setInt(2, receiverId);
	            preparedStatement.setString(3, content);
	            preparedStatement.executeUpdate();
	            System.out.println("Mesaj inserat cu succes!");
	        } catch (SQLException e) {
	            System.out.println("Eroare la inserarea mesajului: " + e.getMessage());
	        }
	    }
	    /*
	    public static void updateUserActive(String username) {
			try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
				String query = "UPDATE active FROM users WHERE username = ?";
				try(PreparedStatement statement = connection.prepareStatement(query)){
					statement.setString(1, username);
					ResultSet result = statement.executeQuery();
					return result.next();
				}
			} catch(SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
	*/
}
