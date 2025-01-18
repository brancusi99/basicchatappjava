package com.chatapp.operations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.chatapp.server.Database.*;

public class MessageOperations {

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

    public static void deleteMessage(int messageId) {

        String sql = "DELETE FROM messages WHERE id = ?";
        try (
                Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, messageId);
            preparedStatement.executeUpdate();
            System.out.println("Mesaj șters cu succes!");
        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea mesajului: " + e.getMessage());
        }
    }
}