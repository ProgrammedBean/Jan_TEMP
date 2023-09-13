package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;
import java.util.Observable;

public class Server extends Observable{

	private static SQL_Connection sql_conn;
	private static Statement sql_st;
	private static int guests;
	
	public static void main(String[] args) {
		// initialize sql connection
		sql_conn = new SQL_Connection(sql_st);
		
		// initialize server
		new Server().runServer();
	}
	
	private void runServer() {
		try {
			setUpNetworking();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	private void setUpNetworking() throws Exception {
		// create server socket on port
		ServerSocket serverSocket = new ServerSocket(4242);
		
		System.out.println("Waiting for clients...");
		
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
	
	@SuppressWarnings("static-access")
	protected void processMessage(String gsonMessage) {
		try {
			// notify all clients in server
			this.setChanged();
			this.notifyObservers(gsonMessage);
		} catch (Exception e) {
			System.out.println(sql_conn.RED + "Not able to send to client" + sql_conn.DEFAULT);
		}
	}
	
	@SuppressWarnings("static-access")
	protected boolean loginAttempt(String username, String password) {
			
		// create SQL command
		String sql = "SELECT * FROM user_list ";
		sql += "WHERE name LIKE \"" + username + "\" ";
		sql += "AND password LIKE \"" + password + "\";";
		
		// execute and receive an SQL response
		String res = this.sql_conn.read_one_sql_cmd(sql);
		if((res != null) && (!res.equals("")))
			return true;
		return false;
	}
	
	protected int loginGuest() {
		guests++;
		return guests;
	}
	
	@SuppressWarnings("static-access")
	protected boolean registerAttempt(String username, String password) {
		// determine if name already exists in db
		String sql = "SELECT name FROM user_list "
				   + "WHERE name LIKE \"" + username + "\";";
		String res = this.sql_conn.read_one_sql_cmd(sql);
		
		// register (if username doesn't exist)
		try {
			if(res.equals("")) {
				sql = "INSERT INTO user_list (name,password) "
						   + "VALUES ('" + username + "','" + password +"');";
				this.sql_conn.write_sql_cmd(sql);
				return true;
			}
			else {
//				System.out.println("User name not available.");
				return false;
			}
				
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("static-access")
	public String[] get_auction_items() {
		String[] auction_items = this.sql_conn.read_col_sql_cmd("SELECT name FROM product_list");
		if(auction_items != null)
			return auction_items;
		return new String[] {"no items in server"};
	}
	
	@SuppressWarnings("static-access")
	public String[] get_auction_info_of(String item) {
		
		// prepare sql command and labels to search for
		String sql = "SELECT * FROM product_list WHERE name LIKE \"" + item + "\";";
		String[] lbls = new String[] {"name", "price", "description", "start_time", "end_time"};
		
		// put info from db into json
		try {
			return this.sql_conn.read_row_sql_cmd(sql, lbls);
		}
		catch(Exception e) {
			return null;
		}
	}
	
	protected synchronized int validBid(String bid, String bidItem) {
//		Item newItem = auction.get(bidItem);
//		if(Double.parseDouble(bid) > newItem.price) {
//			// check if the limit is met
//			if(Double.parseDouble(bid) > newItem.limit) {
//				return 3;
//			}
//			// proceed to update the value
//			auction.replace(bidItem, auction.get(bidItem), new Item(newItem.description, Double.parseDouble(bid), newItem.limit));
//			return 2;
//		}
		return 1;
	}
	
	protected String getDesc(String bidItem) {
//		return auction.get(bidItem).description;
		return "";
	}
	
	protected void removeClient(ClientHandler handler) {
		this.deleteObserver(handler);
		this.setChanged();
		System.out.println("Observer count: " + this.countObservers());
	}
}
