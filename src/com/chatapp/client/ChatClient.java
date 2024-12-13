package com.chatapp.client;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;
import java.net.*;

public class ChatClient {
	
	private Socket socket = null;
	private Consumer<String> onMessageReceived;
	private PrintWriter out = null;
	private BufferedReader in = null;

	public ChatClient(String serverAddress, int serverPort, Consumer<String> onMessageReceived) throws IOException{
		//try {
			this.socket = new Socket(serverAddress, serverPort);
			System.out.println("Connected to the chat server");
			
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.onMessageReceived = onMessageReceived;
			/*
			String line = "";
			while(!line.equals("exit")) {
				line = inputConsole.readLine();
				out.println(line);
				System.out.println(in.readLine());
			}
			socket.close();
			inputConsole.close();
			out.close();
		} catch(IOException i) {
			System.out.println("Unexpected exception: " + i.getMessage());
		}
		*/
	}
	
	public void sendMessage(String msg) {
		out.println(msg);
	}
	
	public void startClient() {
		new Thread(() -> {
			try {
				String line;
				while((line = in.readLine()) != null) {
					onMessageReceived.accept(line);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
/*
	public static void main(String args[]) {
		ChatClient client = new ChatClient("127.0.0.1", 5000);
	}
	*/
}
