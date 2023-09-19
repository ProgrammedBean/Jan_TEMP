package Client;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Auction_Scene {
	
	public static final String gen_stylesheet = "General_Scene.css";
	public static final String auc_stylesheet = "Auction_Scene.css";

	public ChoiceBox<String> items;
	private Label currItemDescLabel;
	public Label currItemDesc;
	private Label currItemStartDateLabel;
	public Label currItemStartDate;
	private Label currItemEndDateLabel;
	public Label currItemEndDate;
	private Label bidValLbl;
	public TextField bidValue;
	public Button bidBtn;
	private Label highestBidMessage;
	private Label yourBidMessage;
	public Label myBid;
	public Label highestBid;
//	private Button historyBtn;
	private Label historyLabel;
    private TextArea historyDisplay;
    private Label chatLabel;
	public TextArea chatDisplay;
	public TextArea chatInput;
	public Button logoutBtn;
	public Button quitBtn;
	
	public Auction_Scene(Button quitBtn) {
		this.quitBtn = quitBtn;
	}

	public GridPane make_scene() {
		
		GridPane grid = new GridPane();
		
		// grid scene format
		grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);
		grid.setHgap(10);
		
		// bid label
		Label bidLabel = new Label("Items to Bid: ");
		GridPane.setConstraints(bidLabel, 0, 0);
		grid.getChildren().add(bidLabel);
		
		// drop menu of items to bid
		items = new ChoiceBox<String>();
		GridPane.setConstraints(items, 1, 0);
		grid.getChildren().add(items);
		
		// amount of bid field
		bidValLbl = new Label("$");
		GridPane.setConstraints(bidValLbl, 2, 0);
		bidValue = new TextField();
		bidValue.setPromptText("value of bid");
		GridPane.setConstraints(bidValue, 3, 0);
		bidBtn = new Button("bid item!");
		GridPane.setConstraints(bidBtn, 4, 0);
		grid.getChildren().addAll(bidValLbl, bidValue, bidBtn);
		
		// item description
		currItemDescLabel = new Label("Description:");
		GridPane.setConstraints(currItemDescLabel, 0, 1);
		currItemDesc = new Label("");
		currItemDesc.getStyleClass().add("label-desc");
		GridPane.setConstraints(currItemDesc, 1, 1);
		GridPane.setColumnSpan(currItemDesc, 3);
		grid.getChildren().addAll(currItemDescLabel, currItemDesc);
		
		// bid creation date
		currItemStartDateLabel = new Label("Start Date:");
		GridPane.setConstraints(currItemStartDateLabel, 0, 2);
		currItemStartDate = new Label("0000-00-00 00:00:00");
		currItemStartDate.getStyleClass().add("label-desc");
		GridPane.setConstraints(currItemStartDate, 1, 2);
		grid.getChildren().addAll(currItemStartDateLabel, currItemStartDate);
		
		// bid end date
		currItemEndDateLabel = new Label("End Date:");
		GridPane.setConstraints(currItemEndDateLabel, 0, 3);
		currItemEndDate = new Label("0000-00-00 00:00:00");
		currItemEndDate.getStyleClass().add("label-desc");
		GridPane.setConstraints(currItemEndDate, 1, 3);
		grid.getChildren().addAll(currItemEndDateLabel, currItemEndDate);
		
		// current highest bid label
		highestBidMessage = new Label("Highest Bid:");
		GridPane.setConstraints(highestBidMessage, 0, 4);
		highestBid = new Label("");
		highestBid.getStyleClass().add("label-desc");
		GridPane.setConstraints(highestBid, 1, 4);
		grid.getChildren().addAll(highestBidMessage, highestBid);
		
		// client's current bid label
		yourBidMessage = new Label("Your Bid:");
		GridPane.setConstraints(yourBidMessage, 0, 5);
		myBid = new Label("none");
		myBid.getStyleClass().add("label-desc");
		GridPane.setConstraints(myBid, 1, 5);
		grid.getChildren().addAll(yourBidMessage, myBid);
		
		// logout button
		logoutBtn = new Button("logout");
		GridPane.setConstraints(logoutBtn, 0, 6);
		grid.getChildren().add(logoutBtn);
		
		// quit button
		GridPane.setConstraints(quitBtn, 1, 6);
		grid.getChildren().add(quitBtn);
		
		// bid log (history)
		historyLabel = new Label("Bidding Log");
		historyLabel.setStyle("-fx-font-weight: bold");
		GridPane.setRowIndex(historyLabel, 7);
		GridPane.setColumnSpan(historyLabel, 5);
		GridPane.setHalignment(historyLabel, javafx.geometry.HPos.CENTER);
		historyDisplay = new TextArea();
		historyDisplay.setPrefHeight(100.0);
		historyDisplay.setEditable(false);
		GridPane.setRowIndex(historyDisplay, 9);
		GridPane.setColumnSpan(historyDisplay, 5);
//		GridPane.setFillWidth(historyDisplay, true);
//		historyBtn = new Button("history");
//		GridPane.setConstraints(historyBtn, 0, 4);
		grid.getChildren().addAll(historyLabel, historyDisplay);
		
		// current highest bid display 
//		bidDisplay = new TextArea("Current Highest Bid");
//		bidDisplay.setPrefHeight(40.0);
//		bidDisplay.setEditable(false);
//		GridPane.setRowIndex(bidDisplay, 8);
//		GridPane.setColumnSpan(bidDisplay, 4);
//		GridPane.setFillWidth(bidDisplay, true);
//		grid.getChildren().add(bidDisplay);
		
		// chat display
		chatLabel = new Label("Chat");
		chatLabel.setStyle("-fx-font-weight: bold");
		GridPane.setRowIndex(chatLabel, 10);
		GridPane.setColumnSpan(chatLabel, 5);
		GridPane.setHalignment(chatLabel, javafx.geometry.HPos.CENTER);
		chatDisplay = new TextArea();
		chatDisplay.setPrefHeight(80.0);
		chatDisplay.setEditable(false);
		GridPane.setRowIndex(chatDisplay, 11);
		GridPane.setColumnSpan(chatDisplay, 5);
//		GridPane.setFillWidth(chatDisplay, true);
		grid.getChildren().addAll(chatLabel, chatDisplay);
		
		// text display 
		chatInput = new TextArea("");
		chatInput.setPrefHeight(55.0);
		chatInput.setEditable(true);
		chatInput.setWrapText(true);
		GridPane.setRowIndex(chatInput, 12);
		GridPane.setColumnSpan(chatInput, 5);
		GridPane.setFillWidth(chatInput, true);
		grid.getChildren().add(chatInput);
		
//		refresh = new Button("refresh");
//		GridPane.setConstraints(refresh, 3, 2);
		
		// add stylesheet to auction scene
		grid.getStylesheets().add(gen_stylesheet);
		grid.getStylesheets().add(auc_stylesheet);
		
		return grid;
		
	}
}
