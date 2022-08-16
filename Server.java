/*
 * FinalProject_Server Server.java
 * EE422C Final Project submission by
 * Replace <...> with your actual data.
 * Jan Rubio
 * jcr4698
 * 17125
 * Slip days used: <1>
 * Spring 2021
 */

package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import com.google.gson.Gson;

public class Server extends Observable{
	
	private static HashMap<String, Item> auction;
	private static HashMap<String, String> accounts;
	private static Scanner items;
	private static int guests;
	
	public static void main(String[] args) {
		try {
			// initialize the auction with items to sell
			items = new Scanner(new File("AuctionItems.txt"));
			auction = new HashMap<String, Item>();
			String itemArr[];
			while(items.hasNext()) {
				itemArr = items.nextLine().trim().split(",");
				auction.put(itemArr[0].trim(), new Item(itemArr[1].trim(), Double.parseDouble(itemArr[2]), Double.parseDouble(itemArr[3])));
			}
			
			// initialize the auction with some accounts
			accounts = new HashMap<String, String>();
			accounts.put("password1", "JanRubio21");
			accounts.put("password2", "user2");
			accounts.put("password3", "user3");
			accounts.put("password4", "user4");
			accounts.put("password5", "user5");
			
			// initialize server
			new Server().runServer();
		} catch (FileNotFoundException e) {
			System.out.println("No valid or 'items.dat' File Found");
		}
	}
	
	private void runServer() {
		try {
			setUpNetworking();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setUpNetworking() throws Exception {
		// create server socket on port
		ServerSocket serverSocket = new ServerSocket(4242);
		
		// keep listening
		while(true) {
			// wait for client socket connection
			Socket clientSocket = serverSocket.accept();
			System.out.println("Connecting to..." + clientSocket);
			
			// initialize client and start communication
			ClientHandler handler = new ClientHandler(this, clientSocket, clientSocket.getPort());
			this.addObserver(handler); //add to the observer's list
			Thread t = new Thread(handler);
			t.start();
		}
	}
	
	protected String processMessage(String gsonMessage, String speaker) {
		String output = "";
		try {
			// message to gson
			Gson gson = new Gson();
			Command cmd = gson.fromJson(gsonMessage, Command.class);
			cmd.input = speaker + ": " + cmd.input;
			
			// determine output
			output = gson.toJson(cmd);
			
			// notify all clients
			this.setChanged();
			this.notifyObservers(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	protected boolean loginAttempt(String username, String password) {
		if(username.equals(accounts.get(password))) {
			return true;
		}
		return false;
	}
	
	protected int loginGuest() {
		guests++;
		return guests;
	}
	
	protected synchronized int validBid(String bid, String bidItem) {
		Item newItem = auction.get(bidItem);
		if(Double.parseDouble(bid) > newItem.price) {
			// check if the limit is met
			if(Double.parseDouble(bid) > newItem.limit) {
				return 3;
			}
			// proceed to update the value
			auction.replace(bidItem, auction.get(bidItem), new Item(newItem.description, Double.parseDouble(bid), newItem.limit));
			return 2;
		}
		return 1;
	}
	
	protected synchronized Command refreshItem(String bidItem, int port) {
		Command cmd = new Command(auction.get(bidItem).price.toString(), "refresh", port);
		cmd.setDescription(auction.get(bidItem).description);
		return cmd;
	}
	
	protected String getDesc(String bidItem) {
		return auction.get(bidItem).description;
	}
	
	protected void removeClient(ClientHandler handler) {
		this.deleteObserver(handler);
		this.setChanged();
	}
}
