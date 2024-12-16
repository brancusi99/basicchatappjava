package com.chatapp.client;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import java.net.*;

public class ChatClient {
	
	private Socket socket;
	private Consumer<String> onMessageReceived;
	private PrintWriter out;
	private BufferedReader in;

	public ChatClient(String serverAddress, int serverPort, Consumer<String> onMessageReceived) throws IOException{
		//connects to server
			this.socket = new Socket(serverAddress, serverPort);
			System.out.println("Connected to the chat server");
			
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.onMessageReceived = onMessageReceived;
			
	}
	
	//sends messages to server
	public void sendMessage(String msg) {
		out.println(msg);
	}
	
	//receive messages in a separate thread
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

}
