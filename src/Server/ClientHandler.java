package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Client.Command;

public class ClientHandler implements Runnable, Observer {

	private Server server;
	private Socket clientSocket;
	private BufferedReader fromClient;
	private PrintWriter toClient;
	private int port;
	private String user;
	
//	Gson gsonMessage;
	Command cmd;
	
	protected ClientHandler(Server server, Socket clientSocket, int port) {
		this.server = server;
		this.clientSocket = clientSocket;
		try {
			fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			toClient = new PrintWriter(this.clientSocket.getOutputStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
		this.port = port;
		this.user = "";
//		sendToPortClient(port);
	}
	
	protected void send_to_port_client(int port) {
		toClient.println(port);
		toClient.flush();
	}
	
	protected void send_to_client(Command cmd) {		
		Gson gsonMessage = (new GsonBuilder()).create();
//		System.out.println("Server Sent: " + gsonMessage.toJson(cmd));
		toClient.println(gsonMessage.toJson(cmd));
		toClient.flush();
	}
	
	@Override
	public void run() {
		String input;
		try {
			while((input = fromClient.readLine()) != null) {
				
//				System.out.println("Server Received: " + input);
				
				// obtain message from json
				cmd = new Gson().fromJson(input, Command.class);
				
				// if the command is quit/exit
				if(cmd.command.equals("quit")) {
					server.removeClient(this);
					this.clientSocket.close();
				}
				
				// if the command is message
				if(cmd.command.equals("message")) {
					Command cmdSend = new Command(cmd.input, cmd.command, port);
					Gson gsonMessageSend = (new GsonBuilder()).create();
					
					if(user.equals("")) {
						cmdSend.input = cmd.port + ": " + cmd.input;
						server.process_message(gsonMessageSend.toJson(cmdSend));
					}
					else {
						cmdSend.input = user + ": " + cmd.input;
						server.process_message(gsonMessageSend.toJson(cmdSend));
					}
				}
				
				// if the command is register attempt
				if(cmd.command.equals("register")) {
					String regStatus = "this username is already taken";
					if(server.register_attempt(cmd.get_username(), cmd.get_password()))
						regStatus = "registration successful";
					cmd.input = regStatus;
					send_to_client(cmd);
				}
				
				// if the command is login attempt
				if(cmd.command.equals("login")) {
					if(server.login_attempt(cmd.get_username(), cmd.get_password())) {
						user = cmd.get_username();
						cmd.input = "2";
						send_to_client(cmd);
					}
					else { // attempt failed
						cmd.input = "incorrect user/password";
						send_to_client(cmd);
					}
				}
				
				// if the command is login guest
				if(cmd.command.equals("guest")) {
					int guest = server.login_guest("Guest_" + port);
					if(guest < Integer.MAX_VALUE - 1) {
						user = "Guest_" + port + guest;
						cmd.input = "success";
						cmd.set_username(user);
						send_to_client(cmd);
					}
					else {
						cmd.input = "fail";
						send_to_client(cmd);
					}
				}
				
				// if the command is guestList
				if(cmd.command.equals("itemList")) {
					String auction_items[] = server.get_auction_items();
					cmd.input = "";
					cmd.auction_arr = auction_items;
					send_to_client(cmd);
				}
				
				// if the command is logout
				if(cmd.command.equals("logout")) {
					user = ""; // user field is empty
					cmd.input = "logout";
					send_to_client(cmd);
				}
				
				// if the command is selection
				if(cmd.command.equals("itemInfo")) {
					String auction_info[] = server.get_auction_info_of(cmd.input);
					cmd.auction_arr = auction_info;
					cmd.input = "success";
					if(auction_info == null)
						cmd.input = "fail";
					send_to_client(cmd);
				}
				
				// TODO: if the command is a bid
				if(cmd.command.equals("bid")) {
					int bidResult = server.bid_attempt(cmd.input, cmd.item, null);
					if(bidResult == 0) { // SUCCESS
						// inform 
						cmd.input = "Your bid was successful!";
						send_to_client(cmd);
						// update user auction info
						cmd.auction_arr = server.get_auction_info_of(cmd.item);
						cmd.command = "itemInfo";
						cmd.input = "success";
						if(cmd.auction_arr == null)
							cmd.input = "fail";
						send_to_client(cmd);
					}
					else if(bidResult == 1) { // FAIL
						cmd.input = "Bid failed. Bid must be >$1.00 than the current bid.";
						send_to_client(cmd);
					}
					else if(bidResult == 2) { // FAIL
						cmd.input = "Bid failed. Auction has expired.";
						send_to_client(cmd);
					}
					else if(bidResult == 3) { // FAIL
						cmd.input = "Bid failed. Item has been sold. Auction has finalized.";
						send_to_client(cmd);
					}
					else {
						cmd.input = "Bid failed. An error has occurred.";
						send_to_client(cmd);
					}
//					// notify the clients
//					cmd.description = server.getDesc(cmd.item);
//					if(user.equals(""))
//						server.processMessage(gsonMessageSend.toJson(cmdSend), ""+cmd.port);
//					else
//						server.processMessage(gsonMessageSend.toJson(cmdSend), user);
//					// send the client back the results
//					cmd.input = "2";
//					sendToClient(cmd);
				}
				
				// if the command is refresh
//				if(cmd.command.equals("refresh")) {
////					Command cmdSend = server.refreshItem(cmd.input, port);
//					cmdSend.item = cmd.input;
//					Gson gsonMessageSend = (new GsonBuilder().create());
//					sendToClient(gsonMessageSend.toJson(cmdSend));
//				}
				
			}
			clientSocket.close();
		} catch(IOException e) {
			
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		Gson gsonSend = new Gson();
		Command cmd = gsonSend.fromJson((String)arg, Command.class);		
		// if command is message
		if(cmd.command.equals("message")) {
			send_to_client(cmd);
		}
		// if command is update
		if(cmd.command.equals("bid")) {
			cmd.command = "update";
			send_to_client(cmd);
		}
	}
}
