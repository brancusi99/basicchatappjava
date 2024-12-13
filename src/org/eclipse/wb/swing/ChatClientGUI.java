package org.eclipse.wb.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import com.chatapp.client.ChatClient;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClientGUI extends JFrame {
	
	private JTextArea messageArea;
	private JTextField textField;
	private ChatClient client;
	
	private JButton exitButton;

	public ChatClientGUI() {
		// TODO Auto-generated constructor stub
		super("Chat Application");
		setSize(400, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Color backgroundColor = new Color(240, 240, 240);
		Color buttonColor = new Color(75, 75, 75);
		Color textColor = new Color(50, 50, 50);
		Font textFont = new Font("Arial", Font.PLAIN, 14);
		Font buttonFont = new Font("Arial", Font.BOLD, 12);
		
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		messageArea.setBackground(backgroundColor);
		messageArea.setFont(textFont);
		add(new JScrollPane(messageArea), BorderLayout.CENTER);
		
		textField = new JTextField();
		textField.setFont(textFont);
		textField.setForeground(textColor);
		textField.setBackground(backgroundColor);
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.sendMessage(textField.getText());
				textField.setText("");
			}
		});
		add(textField, BorderLayout.SOUTH);
		
		//when the client starts, it prompts the user to enter their name in a dialog box
		//we update the app window's title and we reflect the user's identity using given name
		
		String name = JOptionPane.showInputDialog(this, "Enter your name: ", "Name Entry", JOptionPane.PLAIN_MESSAGE);
		//set window title to include name
		this.setTitle("Chat Application - " + name);
		
		//modify actionperformed to include the user name and time stamp
		textField.addActionListener(e -> {
			//when pressed enter = construct message that includes a current timestamp and the user's name
			String message = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]" + name + ": " + textField.getText();
			//sending to server
			client.sendMessage(message);
			//after sending message, text field is cleared
			textField.setText("");
		});
		
		//created exit button to close application
		exitButton = new JButton("Exit");
		exitButton.setFont(buttonFont);
		exitButton.setBackground(buttonColor);
		exitButton.setForeground(Color.BLUE);
		exitButton.addActionListener(e -> {
			//when client exits app, it sends a constructed departure
			//message to the server
			String departureMessage = name + " has left the chat";
			client.sendMessage(departureMessage);
			//app waits one second to ensure message was successfullly sent
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
			
			System.exit(0);
		});
		//holds text input field and exit button
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBackground(backgroundColor);
		bottomPanel.add(textField, BorderLayout.CENTER);
		bottomPanel.add(exitButton, BorderLayout.EAST);
		add(bottomPanel, BorderLayout.SOUTH);
		
		try {
			this.client = new ChatClient("127.0.0.1", 5000, this::onMessageReceived);
			client.startClient();
		} catch(IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error connecting to the server", "Connection error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
	}
	
	private void onMessageReceived(String message) {
		SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new ChatClientGUI().setVisible(true);
		});
	}

}
