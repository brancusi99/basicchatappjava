package com.chatapp.server;

import java.io.*;
import java.sql.*;
import java.net.*;
import java.util.*;

//the server listens in a loop for incoming connections

public class ChatServer {

	//list holds all connected clients
	private static List<ClientHandler> clients = new ArrayList<>();
	public static void main(String[] args) throws IOException{
		//class starts a server that listens on port 5000
		ServerSocket serverSocket = new ServerSocket(5000);
		System.out.println("Server started. Waiting for clients...");
		
		//accepts connections continuously 
		while(true) {
			Socket clientSocket = serverSocket.accept();
			System.out.println("Client connected: " + clientSocket);
			
			//spawn a new thread for each connected client
			//ClientHandler class represents new thread
			ClientHandler clientThread = new ClientHandler(clientSocket, clients);
			clients.add(clientThread);
			new Thread(clientThread).start();
		}
	}
}

//class handles communication with a connected client
class ClientHandler implements Runnable{
	private Socket clientSocket;
	private List<ClientHandler> clients;
	private PrintWriter out;
	private BufferedReader in;
	private String username;
	
	//reads messages sent by client and broadcasts to other clients
	public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException{
		this.clientSocket = socket;
		this.clients = clients;
		this.out = new PrintWriter(clientSocket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	public void run() {
		try {
			//prompt for username and password
			out.println("Please enter your username: ");
			String inputUsername = in.readLine();
			
			out.println("Please enter your password: ");
			String inputPassword = in.readLine();
			
			if(!Database.authenticateUser(inputUsername, inputPassword)) {
				out.println("Authentication failed. Closing connection");
				clientSocket.close();
				return;
			}
			
			this.username = inputUsername;
			out.println("Welcome " + username);
			
			String inputLine;
			//continuously reads messages from this client and 
			//broadcasts to others
			while((inputLine = in.readLine()) != null) 
				for(ClientHandler aClient : clients)
					aClient.out.println(username + ": " + inputLine);
			
			
	} catch(IOException e) {
		System.out.println("An error has occured: " + e.getMessage());
	} finally {
		try {
			in.close();
			out.close();
			clientSocket.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
 }

	public class Database{
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
	
}

