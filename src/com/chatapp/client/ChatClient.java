package com.chatapp.client;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import java.net.*;

/**
 * class represents the client side of the application
 * it connects, sends and receives messages from the server
 */

public class ChatClient {
	
	private Socket socket;
	private Consumer<String> onMessageReceived;
	private PrintWriter out;
	private BufferedReader in;

	/**
	 * Creates a new client and connects to the server
	 * @param serverAddress the server's address (IP or hostname)
	 * @param serverPort the server's port number
	 * @param onMessageReceived callback function to handle received messages
	 * @throws IOException
	 */
	
	public ChatClient(String serverAddress, int serverPort, Consumer<String> onMessageReceived) throws IOException{
		//connects to server
			this.socket = new Socket(serverAddress, serverPort);
			System.out.println("Connected to the chat server");
			
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.onMessageReceived = onMessageReceived;
			
	}
	
	//sends messages to server
	/**
	 * Sends messages to the server
	 * @param msg the message to send
	 */
	public void sendMessage(String msg) {
		out.println(msg);
	}
	
	//receive messages in a separate thread
	
	/**
	 * Starts the client to listen for messages from the server 
	 * Runs in a separate thread to handle a magnitude of messages
	 */
	public void startClient() {
		new Thread(() -> {
			try {
				String line;
				//reads messages from server amd passes to 
				//callback function which will display the
				//message in the gui
				while((line = in.readLine()) != null) {
					onMessageReceived.accept(line);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * Handles the login process by prompting the user for credentials and
	 * authenticating them with the server
	 * @param serverAddress
	 * @param serverPort
	 * @param onMessageReceived
	 * @return a ChatClient instance if successful, null if not
	 */
	/*
	public static ChatClient login(String serverAddress, int serverPort, Consumer<String> onMessageReceived) {
		try {
			Socket socket = new Socket(serverAddress, serverPort);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			String username = JOptionPane.showInputDialog("Enter your username: ");
			String password = JOptionPane.showInputDialog("Enter your username: ");
			
			out.println(username);
			out.println(password);
			
			String response = in.readLine();
			if(response.equals("Authentication failed. Closing connection")) {
				JOptionPane.showMessageDialog(null, "Authentication failed. Please try again");
				socket.close();
				return null;
			}
			else {
				JOptionPane.showMessageDialog(null, "Welcome");
				return new ChatClient(serverAddress, serverPort, onMessageReceived);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	*/

}
