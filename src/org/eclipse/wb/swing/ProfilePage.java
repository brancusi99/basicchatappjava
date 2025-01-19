package org.eclipse.wb.swing;

import static com.chatapp.server.Database.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;

public class ProfilePage extends JFrame {

		private JLabel profileImageLabel;
		private File selectedImageFile;
	
    public ProfilePage(String username) {
        super("Profile - " + username);

        // Set up the frame
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Profile information panel
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

         profileImageLabel = new JLabel();
        profileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profileImageLabel.setPreferredSize(new Dimension(100, 100));
        profileImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        loadProfileImage(username); // Load the image from the database
        
        JButton uploadButton = new JButton("Add Profile Picture");
        uploadButton.addActionListener(e -> chooseAndUploadImage(username));
        
        // Add user information
        JLabel userLabel = new JLabel("Username: " + username);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel descPanel = new JPanel(new BorderLayout());
        JTextArea descriere = new JTextArea(5, 20); // 5 rows, 20 columns (adjust as needed)
        descriere.setLineWrap(true); // Enable line wrap
        descriere.setWrapStyleWord(true); // Wrap by words, not characters

        // Add a scroll pane to the text area
        JScrollPane scrollPane = new JScrollPane(descriere);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add a character limit
        //descriere.setDocument(new LimitedDocument(200)); // Limits to 200 characters

        descPanel.add(new JLabel("Descriere: "), BorderLayout.WEST);
        descPanel.add(scrollPane, BorderLayout.CENTER);
        descPanel.setAlignmentX(LEFT_ALIGNMENT);

        
        // Add more fields if necessary, e.g., email or join date
        /*
        JLabel additionalInfo = new JLabel("Email: example@example.com");
        additionalInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        additionalInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        */

        String creationTime = getCreationTimestamp(username);
       /*
        if (creationTime != null) {
            System.out.println("Account created on: " + creationTime);
        } else {
            System.out.println("User not found or an error occurred.");
        }
        */
        JLabel joinDateLabel = new JLabel("Joined in: " + creationTime);
        joinDateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        joinDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add fields to the panel
        profilePanel.add(profileImageLabel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        profilePanel.add(uploadButton);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        profilePanel.add(userLabel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        profilePanel.add(descPanel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        profilePanel.add(joinDateLabel);

        // Add the profile panel to the frame
        add(profilePanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the frame
    }

    private void chooseAndUploadImage(String username) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedImageFile);
                profileImageLabel.setIcon(new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                uploadImageToDatabase(username, selectedImageFile);
                JOptionPane.showMessageDialog(this, "Profile picture updated successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void uploadImageToDatabase(String username, File imageFile) {
        String sql = "UPDATE users SET profile_image = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
             PreparedStatement statement = connection.prepareStatement(sql);
             FileInputStream fis = new FileInputStream(imageFile)) {
            statement.setBlob(1, fis);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to update profile picture: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProfileImage(String username) {
        String sql = "SELECT profile_image FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob("profile_image");
                if (blob != null) {
                    InputStream is = blob.getBinaryStream();
                    BufferedImage img = ImageIO.read(is);
                    profileImageLabel.setIcon(new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                } else {
                    // If no profile image exists, set a default icon
                    profileImageLabel.setIcon(new ImageIcon("path/to/default/image.png")); // Replace with your default image path
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load profile picture: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


