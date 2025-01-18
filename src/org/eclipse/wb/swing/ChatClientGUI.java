package org.eclipse.wb.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import com.chatapp.client.ChatClient;
import com.chatapp.server.Database;
import com.chatapp.operations.MessageOperations.*;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This this the GUI of the application
 * Allows users to interact with the chat system with a windows based interface
 * Allows users to log in/sign up, send and received messages, exit the application
 * 
 */

public class ChatClientGUI extends JFrame {
	
	private JTextArea messageArea;
	private JTextField textField;
	private ChatClient client;
	
	private JButton exitButton;

	/**
	 * Constructor to initialize the GUI components
	 */
	public ChatClientGUI() {
		// TODO Auto-generated constructor stub
		//window
		super("Chat Application");
		setSize(400, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Color backgroundColor = new Color(240, 240, 240);
		Color buttonColor = new Color(75, 75, 75);
		Color textColor = new Color(50, 50, 50);
		Font textFont = new Font("Arial", Font.PLAIN, 14);
		Font buttonFont = new Font("Arial", Font.BOLD, 12);
		
		//message display area
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		messageArea.setBackground(backgroundColor);
		messageArea.setFont(textFont);
		add(new JScrollPane(messageArea), BorderLayout.CENTER);
		
		//text input field
		textField = new JTextField();
		textField.setFont(textFont);
		textField.setForeground(textColor);
		textField.setBackground(backgroundColor);
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//sends message to server
				client.sendMessage(textField.getText());
				//clears input field after sending
				textField.setText("");
			}
		});
		add(textField, BorderLayout.SOUTH);
		
		//exit button to close application
		exitButton = new JButton("Exit");
		exitButton.setFont(buttonFont);
		exitButton.setBackground(buttonColor);
		exitButton.setForeground(Color.BLUE);
		/*
		exitButton.addActionListener(e -> {
			//when client exits app, it sends a constructed departure
			//message to the server
			String departureMessage = name + " has left the chat";
			client.sendMessage(departureMessage);
			//app waits one second to ensure message was successfully sent
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		
			System.exit(0);
		});
		*/
		//holds text input field and exit button
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBackground(backgroundColor);
		bottomPanel.add(textField, BorderLayout.CENTER);
		bottomPanel.add(exitButton, BorderLayout.EAST);
		add(bottomPanel, BorderLayout.SOUTH);
		
		loginScreen();
	}
	
	/**
	 * Displays the login screen, prompts the user for a username and password
	 * and authenticates them
	 */
	
	private void loginScreen() {
		String username = JOptionPane.showInputDialog(this, "Enter your username: ", "Login", JOptionPane.PLAIN_MESSAGE);
		if(username == null || username.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Username is required", "Login Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		String password = JOptionPane.showInputDialog(this, "Enter your password: ", "Login", JOptionPane.PLAIN_MESSAGE);
		if(password == null || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Password is required", "Login Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		boolean isAuthenticated = Database.authenticateUser(username, password);
		if(!isAuthenticated) {
			int choice = JOptionPane.showConfirmDialog(this, "Invalid username or password. Sign up?", "Login error", JOptionPane.YES_NO_OPTION);
				if(choice == JOptionPane.YES_OPTION)
					signUpScreen();
				else
					System.exit(1);
		}
		else
			startChat(username);
	}
	
	
	/**
	 * Displays the sign-up screen for new users 
	 */
	private void signUpScreen() {
		String username = JOptionPane.showInputDialog(this, "Enter your new username: ", "Sign Up", JOptionPane.PLAIN_MESSAGE);
		if(username == null || username.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Username is required", "Sign up error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String password = JOptionPane.showInputDialog(this, "Enter your new password: ", "Sign Up", JOptionPane.PLAIN_MESSAGE);
		if(password == null || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Password is required", "Sign up error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		boolean userExists = Database.userExists(username);
		if(userExists) {
			JOptionPane.showMessageDialog(this,  "Username already exists", "Sign up error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		boolean created = Database.createUser(username, password);
		if(created) {
			JOptionPane.showMessageDialog(this,  "Sign up successful.", "Successs", JOptionPane.INFORMATION_MESSAGE);
			loginScreen();
		}
		else
			JOptionPane.showMessageDialog(this, "An error occured. Please try again", "Sign up error", JOptionPane.ERROR_MESSAGE);
	}
		
	/**
	 * Starts the chat session if the login was successful and sets up message handling
	 * @param username username of the logged in user
	 */
	private void startChat(String username) {
		try {
			//connects to server
			this.client = new ChatClient("127.0.0.1", 5000, this::onMessageReceived);
			/*
			if(this.client == null) {
				JOptionPane.showMessageDialog(this, "Login failed. Username or password incorrect", "Login error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			*/
			this.setTitle("Chat - " + username);
			this.setVisible(true);
			

			//modify actionperformed to include the user name and time stamp
			textField.addActionListener(e -> {
				//when pressed enter constructs message that includes a current timestamp and the user's name
				String message = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" + username + ": " + textField.getText();
				//sending to server
				client.sendMessage(message);
				//message.insertMessage();
				//after sending message, text field is cleared
				textField.setText("");
			});

			
			exitButton.addActionListener(e -> {
				//when client exits app, it sends a constructed departure
				//message to the server
				String departureMessage = username + " has left the chat";
				client.sendMessage(departureMessage);
				//app waits one second to ensure message was successfully sent
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
				
				System.exit(0);
			});
			
			//listens for messages
			client.startClient();
		} catch(IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error connecting to the server", "Connection error",
					JOptionPane.ERROR_MESSAGE);
			//if connection fails exits app
			System.exit(1);
		}
	}
	
	//whenever a message is received from the server
	//this method is called
	/**
	 * Appends an inserted message to the text area
	 * It is called whenever a message is received from the server
	 * @param message
	 */
	private void onMessageReceived(String message) {
		//appends message to text area
		SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
	}
	
	/**
	 * This is the main method
	 * It statrs the GUI
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new ChatClientGUI().setVisible(true);
		});
	}

}
