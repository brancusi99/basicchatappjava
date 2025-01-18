package com.chatapp.operations;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.chatapp.server.Database.*;

public class UserOperations {

    public static void insertUser(String username, String email, String password, String details, String imagePath) {

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

    public static void deleteUser(int userId) {

        String sql = "DELETE FROM users WHERE id = ?";
        try (
                Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            System.out.println("User șters cu succes!");
        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea userului: " + e.getMessage());
        }
    }
    
}