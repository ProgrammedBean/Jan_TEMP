package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Observable;

public class Server extends Observable{

	private static SQL_Connection sql_conn;
	private static Statement sql_st;
	private static int guests;
	
	public static void main(String[] args) {
		// initialize sql connection
		sql_conn = new SQL_Connection(sql_st);
		
		// initialize server
		new Server().run_server();
	}
	
	private void run_server() {
		try {
			set_up_networking();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	private void set_up_networking() throws Exception {
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
	protected void process_message(String gsonMessage) {
		try {
			// notify all clients in server
			this.setChanged();
			this.notifyObservers(gsonMessage);
		} catch (Exception e) {
			System.out.println(sql_conn.RED + "Not able to send to client" + sql_conn.DEFAULT);
		}
	}
	
	@SuppressWarnings("static-access")
	protected boolean login_attempt(String username, String password) {
			
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
	
	@SuppressWarnings("static-access")
	protected int login_guest(String username) {
		
		// generate new guest value
		guests++;
		
		// determine if name already exists in db
		String sql = "SELECT name FROM user_list "
				   + "WHERE name LIKE \"" + (username + guests) + "\";";
		String res = this.sql_conn.read_one_sql_cmd(sql);
		
		try {
			if(res.equals("")) // available guest value
				return guests;
			else // generate a new guest value
				return login_guest(username);
		}
		catch(Exception e) {
			return -1; // no guest values are available
		}
	}
	
	@SuppressWarnings("static-access")
	protected boolean register_attempt(String username, String password) {
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
//			e.printStackTrace();
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
	
	@SuppressWarnings("static-access")
	protected synchronized int bid_attempt(String bid, String bidItem, String id) {
		
		// lookup item
		String sql = "SELECT * FROM product_list ";
		sql += "WHERE name LIKE \"";
		if(bidItem != null)
			sql += bidItem;
		sql += "\"";
		if(id != null && !id.equals(""))
			sql += " OR id LIKE \"" + id + "\";";
		else
			sql += ";";
		String[] auction = this.sql_conn.read_row_sql_cmd(sql, new String[] {"id", "name", "sold", "price", "description", "start_time", "end_time"});
		if(auction == null)
			return 4;
		
//		System.out.println(Arrays.toString(auction));
		String itemId = auction[0];
		String itemName = auction[1];
		Boolean isSold = auction[2].equals("0");
		Double currPrice = Double.parseDouble(auction[3]);
		String endTime = auction[6];
		Double bidValue = Double.parseDouble(bid);
		
		// check if the auction has not been sold
		if(isSold) {
			System.out.println("Auction available!");
			
			// check if the bidding period has expired
			LocalDateTime bidDate = LocalDateTime.parse(endTime.replace(" ","T"));
			LocalDateTime now = LocalDateTime.now();
			if(now.isBefore(bidDate)) {
				System.out.print("Current time: ");
				System.out.println(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				
				// check if the bid is more than the current price
				if(bidValue > currPrice + 0.99) {
					change_auction_status(itemId, itemName, null, bid, null, null, null);
					return 0; // success
				}
				return 1; // failed
			}
			return 2; // time has passed
		}
		return 3; // Item has been sold. Auction has finalized.
	}
	
	@SuppressWarnings("static-access")
	private void change_auction_status(String id, String name, String sold, String price, String desc, String start_time, String end_time) {
		if((name != null) && (id != null)) {
			Boolean single_cmd = false;
			String cmd = "UPDATE product_list\nSET ";
			if(sold != null) {
				single_cmd = true;
				cmd += "sold = " + sold;
			}
			if(price != null) {
				if(single_cmd)
					cmd += ", ";
				single_cmd = true;
				cmd += "price = " + price;
			}
			if(desc != null) {
				if(single_cmd)
					cmd += ", ";
				single_cmd = true;
				cmd += "description = '" + desc + "'";
			}
			if(start_time != null) {
				if(single_cmd)
					cmd += ", ";
				single_cmd = true;
				cmd += "start_date = '" + start_time + "'";
			}
			if(end_time != null) {
				if(single_cmd)
					cmd += ", ";
				single_cmd = true;
				cmd += "end_date = '" + start_time + "'";
			}
			cmd += "\nWHERE name = '" + name + "'";
			cmd += "\nOR id = " + id + ";";
//			System.out.println(cmd);
			this.sql_conn.write_sql_cmd(cmd);
		}
	}

//	protected String getDesc(String bidItem) {
////		return auction.get(bidItem).description;
//		return "";
//	}
	
	protected void removeClient(ClientHandler handler) {
		this.deleteObserver(handler);
		this.setChanged();
		System.out.println("Observer count: " + this.countObservers());
	}
}
