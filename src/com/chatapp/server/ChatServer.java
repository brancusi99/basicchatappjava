package com.chatapp.server;

import java.io.*;
import java.sql.*;
import java.net.*;
import java.util.*;

//the server listens in a loop for incoming connections

/**
 * represents the server side of the application
 * listens in a loop for incoming connections and manages multiple clients simultaneously
 */

public class ChatServer {

	//list holds all connected clients
	
	private static List<ClientHandler> clients = new ArrayList<>();
	
	/**
	 * main method that starts the server and listens for incoming client connections
	 * @param args
	 * @throws IOException
	 */
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
/**
 * Class handles communication with a connected client and runs in a separate
 * thread to allow handling of multiple clients
 */
class ClientHandler implements Runnable{
	private Socket clientSocket;
	private List<ClientHandler> clients;
	private PrintWriter out;
	private BufferedReader in;
	private String username;
	
	//reads messages sent by client and broadcasts to other clients
	
	/**
	 * Creates a new ClientHandles to handle communication with a client
	 * @param socket the client socket
	 * @param clients the list of connected clients
	 * @throws IOException
	 */
	public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException{
		this.clientSocket = socket;
		this.clients = clients;
		this.out = new PrintWriter(clientSocket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	/**
	 * The main method that handles communication and message broadcasting
	 */
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
	
}



