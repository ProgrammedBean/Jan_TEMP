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

package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
	private static final String HOST = "localhost"; //10.165.200.55
	private static BufferedReader fromServer;
	private static PrintWriter toServer;
	private Scanner consoleInput;
	private static int clientPort;
	
	// GUI Fields for Login
	private static Stage window;
	private static GridPane grid1;
	private static TextField username;
	private static Label userLbl;
	private static TextField password;
	private static Label passLbl;
	private static Button loginBtn;
	private static Button registerBtn;
	private static Label invalidLog;
	private static Button quitBtn1;
    
	// GUI fields for Auction
	private static GridPane grid2;
	private static ChoiceBox<String> items;
	private Button bidBtn;
	private Label currItemDescLabel;
	private Label currItemDesc;
	private static TextField bidValue;
	private Label highestBidMessage;
	private Label yourBidMessage;
	private Label myBid;
	private static Label highestBid;
	private Button historyBtn;
	private Label historyLabel;
    private TextArea historyDisplay;
    private Label chatLabel;
	private TextArea chatDisplay;
	private TextArea chatInput;
	private Button logoutBtn;
	private Button quitBtn2;
	
	// GUI fields for Registration
	private static GridPane grid3;
	private TextField usernameRegister;
	private TextField passwordRegister;
	private TextField confirmPasswordRegister;
	private Label missmatchedPassword;
	private static Label userTaken;
	private Button registerMeBtn;
	
	// Client Information Variables
	public static String clientUsername = "";
	public static String clientPassword = "";
	public static Command cmd = new Command(null, null, 0);
	
	// Auction Variables
	public static double currHighestBid;
	public static double currHighestBid1;
	public static String currHighestDesc;
	
	public static ArrayList<Double> bidHistory = new ArrayList<Double>();
	public static ArrayList<String> itemHistory = new ArrayList<String>();
	public static ArrayList<String> bidderHistory = new ArrayList<String>();
	public static ArrayList<String> descHistory = new ArrayList<String>();
	
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
						// obtain message from json
						System.out.println(input);
						cmd = new Gson().fromJson(input, Command.class);
						
						if(cmd.command.equals("register")) { // register command
							if(cmd.input.equals("registration successful")) {
								Platform.runLater(new Runnable() {
										@Override
										public void run() {
											window.setScene(makeLoginScene());
											loginHandler(); // event handler
										}
								}); // Display primaryStage
							}
							else {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										userTaken = new Label(cmd.input);
						 				GridPane.setConstraints(userTaken, 1, 0);
						 				grid3.getChildren().add(userTaken);
									}
								}); // Display invalid
							}
						}
						else if(cmd.command.equals("login")) { // login command
							if(cmd.input.equals("incorrect user/password")) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										invalidLog = new Label(cmd.input);
										GridPane.setConstraints(invalidLog, 1, 2);
										grid1.getChildren().add(invalidLog);
									}
								});
							}
							else {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										clientUsername = cmd.username;
										makeAuctionScene();
									}
								});
							}
						}
						else if(cmd.command.equals("itemList")) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									String[] auction_items = cmd.auction_arr;
//									System.out.println(Arrays.toString(auction_items));
									items.getItems().addAll(auction_items);
									items.setValue(items.getItems().get(0));
									
//									System.out.println(items.getItems());
//									Gson gsonMessage = new Gson();
//									cmd.input = items.getItems().get(0);
//									cmd.command = "itemInfo";
//									sendToServer(gsonMessage.toJson(cmd));
								}
							});
						}
						else if(cmd.command.equals("message")) { // message command
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									chatDisplay.appendText(cmd.input + "\n");
									chatInput.clear();
								}
							});
						}
						else if(cmd.command.equals("logout")) { // logout command
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									clientUsername = "";
									clientPassword = "";
									window.setScene(makeLoginScene());
									loginHandler(); // event handler
								}
							});
						}
						else if(cmd.command.equals("itemInfo")) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									String[] product_info = cmd.auction_arr;
									highestBid.setText("$" + product_info[1]);
									currItemDesc.setText(product_info[2]);
								}
							});
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
					Command cmd = new Command(input, "message", 0);
					Gson gsonMessage = new Gson();
					sendToServer(gsonMessage.toJson(cmd));
				}
			}
		});
		
		// run reading and writing tasks
		readerThread.start();
		writerThread.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// initialize stage
		window = primaryStage;
		window.setTitle("Auction Client - login");
		
		// Initialize primary condition of primary Stage
		
		Scene scene1 = makeLoginScene();
		
		// Set up secondary condition of primaryStage
		
		// makeBiddingServerScene();
		
		// Start first GUI
		
		loginHandler(); // event handler
		window.setScene(scene1); // Display scene1 at beginning
		window.show();
	}
	
	private Scene makeLoginScene() { // initialize login window
		// Login window registration
		window.setTitle("Chat Room - login");
		
		// grid1 scene format
		grid1 = new GridPane();
		grid1.setPadding(new Insets(10, 10, 10, 10));
		grid1.setVgap(10);
		grid1.setHgap(10);
		
		// user name field
		userLbl = new Label("username:");
		GridPane.setConstraints(userLbl, 0, 0);
		username = new TextField();
		username.setPromptText("username");
		GridPane.setConstraints(username, 1, 0);
		
		// password field
		passLbl = new Label("password:");
		GridPane.setConstraints(passLbl, 0, 1);
		password = new TextField();
		password.setPromptText("password");
		GridPane.setConstraints(password, 1, 1);
		
		// login button
		loginBtn = new Button("login");
		GridPane.setConstraints(loginBtn, 0, 2);
		
		// login as guest button
		registerBtn = new Button("register");
		GridPane.setConstraints(registerBtn, 0, 3);
		
		// quit button
		quitBtn1 = new Button("quit");
		GridPane.setConstraints(quitBtn1, 0, 4);
		
		// put labels, buttons, and text fields in grid
		grid1.getChildren().addAll(username, userLbl, password, passLbl, loginBtn, registerBtn, quitBtn1);
		
		// fill stage with scene
		return new Scene(grid1, 300, 200);
	}

	private void loginHandler() { // button set up
		
		// login
		loginBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!username.getText().trim().equals("") && !password.getText().trim().equals("")) {
					Gson gsonMessage = new Gson();
					cmd.command = "login";
					cmd.username = username.getText();
					cmd.password = password.getText();
					sendToServer(gsonMessage.toJson(cmd));
				}
				else
					System.out.println("Cannot login user, one or more fields missing.");
			}
		});
		
		// login and pressed
		loginBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				loginBtn.fire(); // TEST THIS
			}
		});
		
		// password enter
		password.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER && !username.getText().equals("") && !password.getText().equals("")) {
				Gson gsonMessage = new Gson();
				cmd.command = "login";
				cmd.username = username.getText();
				cmd.password = password.getText();
				sendToServer(gsonMessage.toJson(cmd)); // register button
			}
		});
		
		// register
		registerBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				makeRegisterScene();
			}
		});
		
		// register pressed
		registerBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				registerBtn.fire(); // TEST THIS
			}
		});
		
		// quit button
		quitBtn1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					Gson gsonMessage = new Gson();
					sendToServer(gsonMessage.toJson(new Command(null, "quit", 0)));
					fromServer.close();
					toServer.close();
					System.exit(0);
				} catch (IOException e) {
					System.exit(0); // just exit
				}
				window.close();
			}
		});
		
		// exit button
		window.setOnCloseRequest(e -> {
			quitBtn1.fire();
		});
	}

	private void makeRegisterScene() {
		// Registration window title
		window.setTitle("Chat Room - Registration");
		
		// grid1 scene format
		grid3 = new GridPane();
		grid3.setPadding(new Insets(10, 10, 10, 10));
		grid3.setVgap(10);
		grid3.setHgap(10);
		
		// user name field
		usernameRegister = new TextField();
		usernameRegister.setPromptText("new username");
		GridPane.setConstraints(usernameRegister, 0, 0);
	
		// password field
		passwordRegister = new TextField();
		passwordRegister.setPromptText("new password");
		GridPane.setConstraints(passwordRegister, 0, 1);
		
		// password field
		confirmPasswordRegister = new TextField();
		confirmPasswordRegister.setPromptText("confirm password");
		GridPane.setConstraints(confirmPasswordRegister, 0, 2);
		
		// register button
	    registerMeBtn = new Button("register me");
	    GridPane.setConstraints(registerMeBtn, 0, 3);
	 	registerMeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!usernameRegister.getText().equals("") && !passwordRegister.getText().equals("") && !confirmPasswordRegister.getText().equals("")) {
					if(passwordRegister.getText().equals(confirmPasswordRegister.getText())) {
						Gson gsonMessage = new Gson();
	 					cmd.command = "register";
	 					cmd.username = usernameRegister.getText();
	 					cmd.password = passwordRegister.getText();
	 					sendToServer(gsonMessage.toJson(cmd));
					}
					else {
						missmatchedPassword = new Label("passwords must match");
						GridPane.setConstraints(missmatchedPassword, 1, 2);
						grid3.getChildren().add(missmatchedPassword);
					}
				}
			}
		});
	    
	    // put labels, buttons, and text fields in grid
	    grid3.getChildren().addAll(usernameRegister, passwordRegister, confirmPasswordRegister, registerMeBtn, quitBtn1);
	    
	    // fill stage with scene
	    window.setScene(new Scene(grid3, 400, 200));
	}

	private void makeAuctionScene() {
		// Chat Room window title
		window.setTitle("Jan's Auction - Welcome, " + username.getText() + "!");
		
		// grid2 scene format
		grid2 = new GridPane();
		grid2.setPadding(new Insets(10, 10, 10, 10));
		grid2.setVgap(10);
		grid2.setHgap(10);
		
		// bid label
		Label bidLabel = new Label("Items to Bid: ");
		bidLabel.setStyle("-fx-font-weight: bold");
		GridPane.setConstraints(bidLabel, 0, 0);
		grid2.getChildren().add(bidLabel);
		
		// drop menu of items to bid
		Gson gsonMessage = new Gson();
		cmd.command = "itemList";
		sendToServer(gsonMessage.toJson(cmd));	// access the auction items
		items = new ChoiceBox<String>();
		GridPane.setConstraints(items, 1, 0);
		grid2.getChildren().add(items);
		
		// amount of bid field
		bidValue = new TextField();
		bidValue.setPromptText("value of bid");
		GridPane.setConstraints(bidValue, 2, 0);
		grid2.getChildren().add(bidValue);
		
		// bid button
		bidBtn = new Button("bid item!");
		GridPane.setConstraints(bidBtn, 3, 0);
		grid2.getChildren().add(bidBtn);
		
		// item description
		currItemDescLabel = new Label("Description");
		currItemDescLabel.setStyle("-fx-font-weight: bold");
		GridPane.setConstraints(currItemDescLabel, 0, 1);
		currItemDesc = new Label("example description of this product.");
		GridPane.setConstraints(currItemDesc, 1, 1);
//		GridPane.setRowIndex(currItemDesc, 8);
		GridPane.setColumnSpan(currItemDesc, 3);
		grid2.getChildren().addAll(currItemDescLabel, currItemDesc);
		
		// current Highest Bid Label
		highestBidMessage = new Label("Highest Bid:");
		highestBidMessage.setStyle("-fx-font-weight: bold");
		GridPane.setConstraints(highestBidMessage, 0, 2);
		highestBid = new Label("n/a");
		GridPane.setConstraints(highestBid, 1, 2);
		grid2.getChildren().addAll(highestBidMessage, highestBid);
		
		// client's Current Bid Label
		yourBidMessage = new Label("Your Bid:");
		yourBidMessage.setStyle("-fx-font-weight: bold");
		GridPane.setConstraints(yourBidMessage, 0, 3);
		myBid = new Label("none");
		GridPane.setConstraints(myBid, 1, 3);
		grid2.getChildren().addAll(yourBidMessage, myBid);
		
		// logout button
		logoutBtn = new Button("logout");
		GridPane.setConstraints(logoutBtn, 0, 5);
		grid2.getChildren().add(logoutBtn);
		
		// quit button
		quitBtn2 = new Button("quit");
		GridPane.setConstraints(quitBtn2, 0, 6);
		grid2.getChildren().add(quitBtn2);
		
		// bid log (history)
		historyLabel = new Label("Bidding Log");
		historyLabel.setStyle("-fx-font-weight: bold");
		GridPane.setRowIndex(historyLabel, 7);
		GridPane.setColumnSpan(historyLabel, 4);
		GridPane.setHalignment(historyLabel, javafx.geometry.HPos.CENTER);
		historyDisplay = new TextArea();
		historyDisplay.setPrefHeight(100.0);
		historyDisplay.setEditable(false);
		GridPane.setRowIndex(historyDisplay, 8);
		GridPane.setColumnSpan(historyDisplay, 4);
//		GridPane.setFillWidth(historyDisplay, true);
		historyBtn = new Button("history");
		GridPane.setConstraints(historyBtn, 0, 4);
		grid2.getChildren().addAll(historyLabel, historyDisplay, historyBtn);
		
		// current highest bid display 
//		bidDisplay = new TextArea("Current Highest Bid");
//		bidDisplay.setPrefHeight(40.0);
//		bidDisplay.setEditable(false);
//		GridPane.setRowIndex(bidDisplay, 8);
//		GridPane.setColumnSpan(bidDisplay, 4);
//		GridPane.setFillWidth(bidDisplay, true);
//		grid2.getChildren().add(bidDisplay);
		
		// chat display
		chatLabel = new Label("Chat");
		chatLabel.setStyle("-fx-font-weight: bold");
		GridPane.setRowIndex(chatLabel, 9);
		GridPane.setColumnSpan(chatLabel, 4);
		GridPane.setHalignment(chatLabel, javafx.geometry.HPos.CENTER);
		chatDisplay = new TextArea();
		chatDisplay.setPrefHeight(80.0);
		chatDisplay.setEditable(false);
		GridPane.setRowIndex(chatDisplay, 10);
		GridPane.setColumnSpan(chatDisplay, 4);
		GridPane.setFillWidth(chatDisplay, true);
		grid2.getChildren().addAll(chatLabel, chatDisplay);
		
		// text display 
		chatInput = new TextArea("");
		chatInput.setPrefHeight(55.0);
		chatInput.setEditable(true);
		chatInput.setWrapText(true);
		GridPane.setRowIndex(chatInput, 11);
		GridPane.setColumnSpan(chatInput, 4);
		GridPane.setFillWidth(chatInput, true);
		grid2.getChildren().add(chatInput);
		
//		refresh = new Button("refresh");
//		GridPane.setConstraints(refresh, 3, 2);
		
		// fill stage with scene
		window.setScene(new Scene(grid2, 500, 500));
		
		// start handler
		auctionHandler();
		
	}

	private void auctionHandler() {
		
		// exit button
		window.setOnCloseRequest(e -> {
			quitBtn2.fire();
		});
		
		// item selection
		items.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Gson gsonMessage = new Gson();
				cmd.input = items.getValue();
				cmd.command = "itemInfo";
				sendToServer(gsonMessage.toJson(cmd));
			}
		});
		
		// logout button
		logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Gson gsonMessage = new Gson();
				cmd.command = "logout";
				sendToServer(gsonMessage.toJson(cmd));
			}
		});
		
		// chat button
		chatInput.setOnKeyPressed(e -> { // at "sendToServer" it erases clientUsername for some reason!
			if(e.getCode() == KeyCode.ENTER) {
				String message = chatInput.getText().trim();
				if(!message.equals("")) {
					Gson gsonMessage = new Gson();
					cmd.username = clientUsername;
					cmd.command = "message";
					cmd.input = message;
					sendToServer(gsonMessage.toJson(cmd)); // register button
				}
			}
		});
		
		// quit button
		quitBtn2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				Gson gsonMessage = new Gson();
				cmd.command = "logout";
				sendToServer(gsonMessage.toJson(cmd));
				
				try {
					gsonMessage = new Gson();
					sendToServer(gsonMessage.toJson(new Command(null, "quit", 0)));
					fromServer.close();
					toServer.close();
					System.exit(0);
				} catch (IOException e) {
					System.exit(0); // just exit
				}
				window.close();
			}
		});
	}
	
}
