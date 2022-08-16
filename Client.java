/*
 * FinalProject_Client Client.java
 * EE422C Final Project submission by
 * Replace <...> with your actual data.
 * Jan Rubio
 * jcr4698
 * 17125
 * Slip days used: <1>
 * Spring 2021
 */

package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.awt.*;
import java.awt.event.KeyEvent;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class Client extends Application{
	
	// Socket Variables
	private static Socket socket;
	private static final String HOST = "localhost"; //localhost
	private BufferedReader fromServer;
	private PrintWriter toServer;
	private Scanner consoleInput;
	private static int buttonPressed;
	
	private Lock lock = new ReentrantLock();
	private int clientPort;
	private static int loadStatus;
	
	// GUI variables
	private Stage window;
	private GridPane grid1;
	private TextField username;
	private TextField password;
	private Button loginBtn;
	private Button guestBtn;
    private Button quitBtn1;
    private static GridPane grid2;
    private static ChoiceBox<String> items; /////////////////////////
    private Button bidBtn;
    private static TextField bidValue;
    private Label bidMessage;
    private Label yourBidMessage;
    private Label myBid;
    private static Label highestBid;
    private Label bidItem;
    //private static String itemToBeBidded;/////////////////////////////////
    private Button historyBtn;
    private Button logoutBtn;
    private Button quitBtn2;
    private TextArea historyDisplay;
    private TextArea bidDisplay;
    private TextArea chatDisplay;
    private TextArea textDisplay;
    private static Button refresh;
	
	// Client Information Variables  
    public static String clientUsername = "";
    public static String clientPassword = "";
    
    public static double currHighestBid;
    public static double currHighestBid1;
    public static String currHighestDesc;
    
    public static ArrayList<Double> bidHistory = new ArrayList<Double>();
    public static ArrayList<String> itemHistory = new ArrayList<String>();
    public static ArrayList<String> bidderHistory = new ArrayList<String>();
    public static ArrayList<String> descHistory = new ArrayList<String>();
    public static String messageOut;
    public static String messageIn;
    public static Runnable chatDisp;
    
    public static String highestBidder;
    public static ArrayList<String> history;
    
	
	public static void main(String[] args) {
		new Client().runClient(); // set up the network of client
		launch(args);
	}
	
	private void runClient() {
		consoleInput = new Scanner(System.in);
		try {
			setUpNetworking();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void sendToServer(String jsonMessage) {
		toServer.println(jsonMessage);
		toServer.flush();
	}
	
	protected void loginToServer(String jsonLoginCommand) {
		toServer.println(jsonLoginCommand);
		toServer.flush();
	}
	
	private void setUpNetworking() throws Exception {
		// Connect socket to server socket
		socket = new Socket(HOST, 4242);
		System.out.println("Connecting to..." + socket);
		
		// initialize input and output
		InputStreamReader readSock = new InputStreamReader(socket.getInputStream());
		fromServer = new BufferedReader(readSock);
		toServer = new PrintWriter(socket.getOutputStream());
		
		// create a reader task
		Thread readerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String input;
				try { // wait for output from server
					while((input = fromServer.readLine()) != null) {
						
						if(clientPort != 0) { // has port been assigned
							
							// obtain message from json
							Gson gsonMessage = (new GsonBuilder()).create();
							Command cmd = gsonMessage.fromJson(input, Command.class);
							
							// login attempt results
							if(loadStatus == 0 && cmd.command.equals("login")) {
								loadStatus = Integer.parseInt(cmd.input); // attempt finished: success(2)/denied(1)
							}
							
							// login guest attempt
							else if(loadStatus == 0 && cmd.command.equals("guest")) {
								clientUsername = cmd.username;
								loadStatus = 2; // attempt finished: success(2)
							}
							
							// logout
							else if(loadStatus == 0 && cmd.command.equals("logout")) {
								clientUsername = "";
								
								bidHistory.clear();
								bidderHistory.clear();
								
								loadStatus = 3; // attempt finished: logout(3)
							}
							
							// bid attempt
							else if(loadStatus == 0 && cmd.command.equals("bid")) {
								loadStatus = Integer.parseInt(cmd.input);
							}
							
							// highest bid has been found
							else if(cmd.command.equals("update")) {
								String highestBidArr[] = cmd.input.trim().split(":");
								if(cmd.item.equals(items.getValue())) { // update current client
									highestBidder = highestBidArr[0].trim();
									currHighestBid1 = Double.parseDouble(highestBidArr[1].trim());
								}
								
								bidHistory.add(Double.parseDouble(highestBidArr[1].trim()));
								itemHistory.add(cmd.item.trim());
								bidderHistory.add(highestBidArr[0].trim());
								descHistory.add(cmd.description.trim());
							}
							
							// refresh
							else if(loadStatus == 0 && cmd.command.equals("refresh")) {
								currHighestBid = Double.parseDouble(cmd.input);
								currHighestDesc = cmd.description;
								if(!cmd.input.equals("")) {
									loadStatus = 1;
								}
							}
							
							// message received
							else {
								System.out.println(cmd.input);
								messageIn = cmd.input;
								Platform.runLater(chatDisp);
							}
						}
						else { // port will be assigned
							clientPort = Integer.parseInt(input);
						}
						
					}
				} catch(IOException e) {
					System.exit(0); // if server crashes, just close.
				}
			}
		});
		
		// create a writer task
		Thread writerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					String input = consoleInput.nextLine();
					Command cmd = new Command(input, "message", clientPort);
					Gson gsonMessage = (new GsonBuilder()).create();
					sendToServer(gsonMessage.toJson(cmd));
				}
			}
		});
		
		// create a login task
		Thread statusThread = new Thread(new Runnable() {
			@Override
			public void run() {
				buttonPressed = 0; // initially no button is pressed
				while(true) {
					lock.lock();
					if(buttonPressed != 0) {
						if(buttonPressed == 1) { // login with username
							Command cmd = new Command("", "login", clientPort);
							cmd.tryToLogin(clientUsername, clientPassword);
							Gson gsonMessage = (new GsonBuilder()).create();
							sendToServer(gsonMessage.toJson(cmd));
							buttonPressed = 0; // button has been handled
						}
						else if(buttonPressed == 2) { // login as guest
							Command cmd = new Command("", "guest", clientPort);
							Gson gsonMessage = (new GsonBuilder()).create();
							sendToServer(gsonMessage.toJson(cmd));
							buttonPressed = 0;
						}
						else if(buttonPressed == 3) { // quit or exit
							Command cmd = new Command("quit", "quit", clientPort);
							Gson gsonMessage = (new GsonBuilder()).create();
							sendToServer(gsonMessage.toJson(cmd));
							try {
								fromServer.close();
								toServer.close();
								readSock.close();
								System.exit(0);
							} catch (IOException e) {
								System.exit(0); // just exit
							}
						}
						else if(buttonPressed == 4) { // logout
							Command cmd = new Command("", "logout", clientPort);
							Gson gsonMessage = (new GsonBuilder()).create();
							sendToServer(gsonMessage.toJson(cmd));
							buttonPressed = 0;
						}
						else if(buttonPressed == 5) { // make a bid
							Command cmd = new Command(bidValue.getText(), "bid", clientPort);
							cmd.bidItem(items.getValue());
							Gson gsonMessage = (new GsonBuilder()).create();
							sendToServer(gsonMessage.toJson(cmd));
							buttonPressed = 0;
						}
						else if(buttonPressed == 6) { // refresh
							Command cmd = new Command(items.getValue(), "refresh", clientPort);
							Gson gsonMessage = (new GsonBuilder()).create();
							sendToServer(gsonMessage.toJson(cmd));
							buttonPressed = 0;
						}
						else if(buttonPressed == 7) { // get items on action
							Command cmd = new Command("", "initialize", clientPort);
							Gson gsonMessage = (new GsonBuilder()).create();
							sendToServer(gsonMessage.toJson(cmd));
							buttonPressed = 0;
						}
						else if(buttonPressed == 8) { // send message
							String input = messageOut;
							Command cmd = new Command(input.replaceAll("\n", ""), "message", clientPort);
							Gson gsonMessage = (new GsonBuilder()).create();
//							System.out.println(input.replaceAll("\n", ""));
							sendToServer(gsonMessage.toJson(cmd));
							buttonPressed = 0;
						}
					}
					lock.unlock();
				}
			}
		});
		
		// run reading and writing tasks
		readerThread.start();
		writerThread.start();
		statusThread.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// initialize stage
		window = primaryStage;
		window.setTitle("Auction Client - login");

		// Set up initial condition of primaryStage
		
		Scene scene1 = makeLoginScene();
		
		// Set up secondary condition of primaryStage
		
		makeBiddingServerScene();
		
	    // Start first GUI
	    
		inputHandler(); // event handler
		window.setScene(scene1); // Display scene1 at beginning
		window.show();
	}

	private void inputHandler() {
		
		///////////////////////////////////// Action Handlers for Scene 1 /////////////////////////////////////
		
		// login button (1)
		loginBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!username.getText().equals("") && !password.getText().equals("")) {
					clientUsername = username.getText();
					clientPassword = password.getText();
					if(!clientUsername.equals("") && !clientPassword.equals("")) {
						buttonPressed = 1; // notify the writer thread
					}
					Scene scene = new Scene(grid2, 500, 500);
					System.out.print("loading... ");
					while(loadStatus == 0) {
						System.out.print("");
					}
					if(loadStatus == 2) {
						System.out.println("Access Granted!");
						window.setTitle("Auction Client - Welcome " + clientUsername + "!"); // update window
						window.setScene(scene); // fill stage with scene
					}
					else {
						System.out.println("Access Denied.");
						try {
							start(window);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					loadStatus = 0;
					refresh.fire();
				}
			}
		});
		
		// login as guest button (2)
		guestBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				buttonPressed = 2;
				
				Scene scene = new Scene(grid2, 500, 500);
				System.out.print("loading... ");
				while(loadStatus == 0) {
					System.out.print("");
				}
				System.out.println("Logged in as Guest");
				window.setTitle("Auction Client - Welcome " + clientUsername + "!"); // update window
				window.setScene(scene); // fill stage with scene
				loadStatus = 0;
			}
		});
		
		// quit button (3)
		quitBtn1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				window.close();
				buttonPressed = 3;
			}
		});
		
		// exit button (3)
		window.setOnCloseRequest(e -> {
			window.close();
			buttonPressed = 3;
		});
		
		// enter key (1)
		password.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER && !username.getText().equals("") && !password.getText().equals("")) {
				clientUsername = username.getText();
				clientPassword = password.getText();
				if(!clientUsername.equals("") && !clientPassword.equals("")) {
					buttonPressed = 1; // notify the writer thread
				}
				Scene scene = new Scene(grid2, 500, 500);
				System.out.print("loading... ");
				while(loadStatus == 0) {
					System.out.print("");
				}
				if(loadStatus == 2) {
					System.out.println("Access Granted!");
					window.setTitle("Auction Client - Welcome " + clientUsername + "!"); // update window
					window.setScene(scene); // fill stage with scene
				}
				else {
					System.out.println("Access Denied.");
					try {
						start(window);
					} catch (Exception e1) {
						System.out.println("Failed to Switch Window");
						buttonPressed = 3;
					}
				}
				loadStatus = 0;
			}
		});
		
		///////////////////////////////////// Action Handlers for Scene 2 /////////////////////////////////////
		
		// bid an item button (5)
		bidBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!bidValue.getText().equals("")) {
					// notify the writer thread a button has been pressed
					buttonPressed = 5;
					
					// determine whether bid is valid or not (give reason)
					System.out.print("loading... ");
					while(loadStatus == 0) {
						System.out.print("");
					}
					System.out.println("Status Updated");
					
					// remove current bid
					grid2.getChildren().removeAll(myBid, highestBid, bidItem);
					
					if(loadStatus == 1) { // invalid bid
						// client's bid value
						myBid = new Label("Too low.");
						GridPane.setConstraints(myBid, 1, 3);
					}
					else if(loadStatus == 2) { // valid bid
						// client's bid value
						String priceStr = Double.toString(Double.parseDouble((bidValue.getText())));
						String priceArr[] = priceStr.trim().split("\\.");
						char decimalArr[] = priceArr[1].toCharArray();
						if(decimalArr.length == 2) {
							myBid = new Label("$" + Double.parseDouble((bidValue.getText())));
						}
						else {
							myBid = new Label("$" + Double.parseDouble((bidValue.getText())) + "0");
						}
						GridPane.setConstraints(myBid, 1, 3);
						
						// update highest bid label
						priceStr = Double.toString(currHighestBid1);
						priceArr = priceStr.trim().split("\\.");
						decimalArr = priceArr[1].toCharArray();
						if(decimalArr.length == 2) {
							highestBid = new Label("$" + priceStr);
						}
						else {
							highestBid = new Label("$" + priceStr + "0");
						}
						GridPane.setConstraints(highestBid, 1, 2);
					}
					else { // limit on bid has been reached
						// client's bid value
						myBid = new Label("Auction Closed.");
						GridPane.setConstraints(myBid, 1, 3);
					}
					
					// item in question
					bidItem = new Label(items.getValue());
					GridPane.setConstraints(bidItem, 0, 1);
					
					// add it to the grid
					grid2.getChildren().addAll(myBid, highestBid, bidItem);
					loadStatus = 0;
					bidValue.clear();
				}
			}
		});
		
		items.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				refresh.fire();
				
				// client's bid value
				grid2.getChildren().remove(myBid);
				myBid = new Label("");
				GridPane.setConstraints(myBid, 1, 3);
				grid2.getChildren().add(myBid);
			}
		});
		
		// quit button (3)
		quitBtn2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				window.close();
				buttonPressed = 3;
			}
		});
		
		// logout button (4)
		logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// notify the writer thread a button has been pressed
				buttonPressed = 4;
				
				System.out.print("loading...");
				while(loadStatus == 0) {
					System.out.print("");
				}
				System.out.println("Successfully logged out");
				try {
					start(window);
				} catch (Exception e) {
					System.out.println("Failed to Logout");
					buttonPressed = 3;
				}
				loadStatus = 0;
			}
		});
		
		// refresh status of bid (6)
		refresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				buttonPressed = 6;
				
				System.out.print("loading... ");
				while(loadStatus == 0) {
					System.out.print("");
				}
				System.out.println("Status Updated");
				
				// remove current bid
				grid2.getChildren().remove(highestBid);
				
				// update bid label
				String priceStr = Double.toString(currHighestBid);
				String priceArr[] = priceStr.trim().split("\\.");
				char decimalArr[] = priceArr[1].toCharArray();
				if(decimalArr.length == 2) {
					highestBid = new Label("$" + priceStr);
				}
				else {
					highestBid = new Label("$" + priceStr + "0");
				}
				GridPane.setConstraints(highestBid, 1, 2);
				
				// add current bid
				grid2.getChildren().add(highestBid);
				
				// update highest bidder view
				if(!bidHistory.isEmpty()) {
					priceStr = bidHistory.get(bidHistory.size()-1).toString();
					priceArr = priceStr.trim().split("\\.");
					decimalArr = priceArr[1].toCharArray();
					if(decimalArr.length == 2) {
						bidDisplay.setText("Current Highest Bid: " + bidderHistory.get(bidderHistory.size()-1) + " bidded $" + bidHistory.get(bidderHistory.size()-1) + " on " + itemHistory.get(itemHistory.size()-1) + " (" + currHighestDesc + ")");
					}
					else {
						bidDisplay.setText("Current Highest Bid: " + bidderHistory.get(bidderHistory.size()-1) + " bidded $" + bidHistory.get(bidderHistory.size()-1) + "0 on " + itemHistory.get(itemHistory.size()-1) + " (" + currHighestDesc + ")");
					}
				}
				
				// loadstatus back to none
				loadStatus = 0;
			}
		});
		
		// give history of client (7)
		historyBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				historyDisplay.setText("                                                            ~History~" + "\n");
				for(int txt = 0; txt < bidHistory.size(); txt++) {
					String priceStr = bidHistory.get(txt).toString();
					String priceArr[] = priceStr.trim().split("\\.");
					char decimalArr[] = priceArr[1].toCharArray();
					if(decimalArr.length == 2) {
						historyDisplay.appendText(bidderHistory.get(txt) + " bidded $" + bidHistory.get(txt) + " on " + itemHistory.get(txt) + " (" + descHistory.get(txt) + ")" +"\n");
					}
					else {
						historyDisplay.appendText(bidderHistory.get(txt) + " bidded $" + bidHistory.get(txt) + "0 on " + itemHistory.get(txt) + " (" + descHistory.get(txt) + ")" + "\n");
					}
				}
			}
			
		});
		
		// send message GUI
		textDisplay.setOnKeyPressed(e -> {
			if(!textDisplay.getText().equals("") && e.getCode() == KeyCode.ENTER) {
				// save and clear textDisplay
				messageOut = textDisplay.getText();
				textDisplay.setText("");
				
				// send the message
				buttonPressed = 8;
			}
		});
		
		// receive messages GUI
		chatDisp = new Runnable() {
			@Override
			public void run() {
				chatDisplay.appendText(messageIn + "\n");
			}
		};
	}
	
	private Scene makeLoginScene() {
		// grid1 scene format
		grid1 = new GridPane();
		grid1.setPadding(new Insets(10, 10, 10, 10));
		grid1.setVgap(10);
		grid1.setHgap(10);
		
		// user name field
		username = new TextField();
		username.setPromptText("username");
		GridPane.setConstraints(username, 0, 0);
		
		// password field
		password = new TextField();
		password.setPromptText("password");
		GridPane.setConstraints(password, 0, 1);
		
		// login button
		loginBtn = new Button("login");
	    GridPane.setConstraints(loginBtn, 0, 2);
	    
	    // login as guest button
	    guestBtn = new Button("login as guest");
	    GridPane.setConstraints(guestBtn, 0, 3);
	    
	    // quit button
	    quitBtn1 = new Button("quit");
	    GridPane.setConstraints(quitBtn1, 0, 4);
	    
	    // put labels, buttons, and text fields in grid
	    grid1.getChildren().addAll(username, password, loginBtn, guestBtn, quitBtn1);
		
		// fill stage with scene
		return new Scene(grid1, 300, 200);
	}
	
	private void makeBiddingServerScene() {
		// grid2 scene format
		grid2 = new GridPane();
		grid2.setPadding(new Insets(10, 10, 10, 10));
		grid2.setVgap(10);
		grid2.setHgap(9);
		
		// bid label
		Label bidLabel = new Label("Items to Bid:       ");
		GridPane.setConstraints(bidLabel, 0, 0);
		
		// drop menu of items to bid
		items = new ChoiceBox<String>();
		String itemArr[];
		int countFile = 0;
		Scanner auctionItems;
		try {
			auctionItems = new Scanner(new File("AuctionItems.txt"));
			
			while(auctionItems.hasNext()) {
				itemArr = auctionItems.nextLine().trim().split(",");
				if(countFile == 0) {
					currHighestBid1 = Double.parseDouble(itemArr[2]);
				}
				items.getItems().add(itemArr[0].trim());
				countFile++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("No 'AuctionItems.dat' File Found in Folder");
			buttonPressed = 3;
		}
		
		items.setValue(items.getItems().get(0));
		GridPane.setConstraints(items, 1, 0);
		
		// amount of bid field
		bidValue = new TextField();
	 	bidValue.setPromptText("value of bid");
	 	GridPane.setConstraints(bidValue, 2, 0);
		
		// bid button
		bidBtn = new Button("bid item!");
	    GridPane.setConstraints(bidBtn, 3, 0);
	    
	    // Current Highest Bid Label
	    bidMessage = new Label("Highest Bid:");
		GridPane.setConstraints(bidMessage, 0, 2);
		
		// initialize client's bid value
		String priceStr = Double.toString(currHighestBid1);
		String priceArr[] = priceStr.trim().split("\\.");
		char decimalArr[] = priceArr[1].toCharArray();
		if(decimalArr.length == 2) {
			highestBid = new Label("$" + currHighestBid1);
		}
		else {
			highestBid = new Label("$" + currHighestBid1 + "0");
		}
		GridPane.setConstraints(highestBid, 1, 2);
		
		// Client's Current Bid Label
	    yourBidMessage = new Label("Your Bid:");
		GridPane.setConstraints(yourBidMessage, 0, 3);
		
		// initialize client's bid value
		myBid = new Label("");
		GridPane.setConstraints(myBid, 1, 3);
		
		// bid log button
		historyBtn = new Button("history");
	    GridPane.setConstraints(historyBtn, 0, 4);
	    
	    // logout button
	    logoutBtn = new Button("logout");
	    GridPane.setConstraints(logoutBtn, 0, 5);
	    
	    // quit button
	    quitBtn2 = new Button("quit");
	    GridPane.setConstraints(quitBtn2, 0, 6);
	    
	    // history display
	    historyDisplay = new TextArea("                                                            ~History~");
	    historyDisplay.setPrefHeight(100.0);
	    historyDisplay.setEditable(false);
	    GridPane.setRowIndex(historyDisplay, 7);
	    GridPane.setColumnSpan(historyDisplay, 4);
	    GridPane.setFillWidth(historyDisplay, true);
	    
	    // current highest bid display 
	    bidDisplay = new TextArea("Current Highest Bid");
	    bidDisplay.setPrefHeight(40.0);
	    bidDisplay.setEditable(false);
	    GridPane.setRowIndex(bidDisplay, 8);
	    GridPane.setColumnSpan(bidDisplay, 4);
	    GridPane.setFillWidth(bidDisplay, true);
	    
	    // chat display 
	    chatDisplay = new TextArea("~Chat~\n"); //TODO
	    chatDisplay.setPrefHeight(80.0);
	    chatDisplay.setEditable(false);
	    GridPane.setRowIndex(chatDisplay, 9);
	    GridPane.setColumnSpan(chatDisplay, 4);
	    GridPane.setFillWidth(chatDisplay, true);
	    
	    // text display 
	    textDisplay = new TextArea("\n");
	    textDisplay.setPrefHeight(55.0);
	    textDisplay.setEditable(true);
	    textDisplay.setWrapText(true);
	    GridPane.setRowIndex(textDisplay, 10);
	    GridPane.setColumnSpan(textDisplay, 4);
	    GridPane.setFillWidth(textDisplay, true);
	    
	    // register
	    
	    refresh = new Button("refresh");
	    GridPane.setConstraints(refresh, 3, 2);
	    
	    // put labels, buttons, and text fields in grid
	    grid2.getChildren().addAll(bidLabel, items, bidBtn, bidValue, bidMessage, yourBidMessage, historyBtn, logoutBtn, quitBtn2, historyDisplay, bidDisplay, chatDisplay, textDisplay, refresh);
	}
}
