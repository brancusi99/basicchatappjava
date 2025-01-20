package com.chatapp.server;

import java.io.*;
import java.sql.*;
import java.net.*;
import java.util.*;

import com.chatapp.server.Database.*;


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
		try (//class starts a server that listens on port 5000
		ServerSocket serverSocket = new ServerSocket(5000)) {
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
	
	
	@Override
	public void run() {
	    try { 
	        String inputLine;
	        // Continuously reads messages from this client and broadcasts to others
	        while ((inputLine = in.readLine()) != null) {
	            if (inputLine.isEmpty()) {
	                continue;
	            }

	            // Validate the input message format
	            if (!inputLine.contains("]") || !inputLine.contains(":")) {
	                out.println("Invalid message format. Ensure the message includes a timestamp and colon.");
	                continue;
	            }

	            // Extract timestamp, username, and message content
	            try {
	                int timestampEnd = inputLine.indexOf("]") + 1; // Position after closing bracket
	                String timestamp = inputLine.substring(1, timestampEnd - 1); // Extract timestamp
	                String restOfMessage = inputLine.substring(timestampEnd).trim();

	                int colonIndex = restOfMessage.indexOf(":");
	                if (colonIndex == -1) {
	                    out.println("Invalid message format. A colon is required.");
	                    continue;
	                }

	                String senderUsername = restOfMessage.substring(0, colonIndex).trim();
	                String messageContent = restOfMessage.substring(colonIndex + 1).trim();

	                // Log the extracted message
	                System.out.println("[" + timestamp + "] " + senderUsername + ": " + messageContent);

	                // Broadcast the message to all clients
	                for (ClientHandler aClient : clients) {
	                    aClient.out.println("[" + timestamp + "] " + senderUsername + ": " + messageContent);
	                }
	            } catch (IndexOutOfBoundsException e) {
	                out.println("Failed to parse the message. Ensure it follows the correct format.");
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("An error occurred: " + e.getMessage());
	    } finally {
	        try {
	            in.close();
	            out.close();
	            clientSocket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	
	/*
	public void run() {
		try { 
			String inputLine;
			//continuously reads messages from this client and 
			//broadcasts to others
			while((inputLine = in.readLine()) != null) {
				
				if (inputLine.isEmpty()) {
					continue;
				}
				/*
				String[] messageParts = inputLine.split(":", 2);
	            String receiverUsername = messageParts[0].trim();  // First part as receiver's username
	            String messageContent = messageParts[1].trim();    // Second part as message content
*/
	            // Fetch the receiverId from the database
	         // Fetch the receiverId from the database
	            //int receiverId = Database.getUserId(receiverUsername.substring(receiverUsername.indexOf("]") + 1));

	            // Now you can insert the message into the database
	           // MessageOperations.insertMessage(senderId, receiverId, messageContent);

		/*		
				for(ClientHandler aClient : clients)
					aClient.out.println(inputLine);
//					aClient.out.println(username + ": " + inputLine);
			}
			
			
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
    */
	
}



