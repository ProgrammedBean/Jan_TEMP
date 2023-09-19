package Client;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Login_Scene {
	
	public static final String stylesheet = "General_Scene.css";

	public Button quitBtn;
	public TextField username;
	private static Label userLbl;
	public PasswordField password;
	private static Label passLbl;
	public Button loginBtn;
	private Label fieldMissing;
	public Button guestLoginBtn;
	public Button registerBtn;
	private static Label invalidLog;
	
	public Login_Scene(Button quitBtn) {
		this.quitBtn = quitBtn;
	}
	
	public GridPane make_scene() {
		
		GridPane grid = new GridPane();
		
		// grid scene format
		grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);
		grid.setHgap(10);
		
		// user name field
		userLbl = new Label("Username:");
		GridPane.setConstraints(userLbl, 0, 0);
		username = new TextField();
		username.setPromptText("username");
		GridPane.setConstraints(username, 1, 0);
		grid.getChildren().addAll(userLbl, username);
		
		// password field
		passLbl = new Label("Password:");
		GridPane.setConstraints(passLbl, 0, 1);
		password = new PasswordField();
		password.setPromptText("password");
		GridPane.setConstraints(password, 1, 1);
		grid.getChildren().addAll(passLbl, password);
		
		// login button
		loginBtn = new Button("login");
		GridPane.setConstraints(loginBtn, 0, 2);
		fieldMissing = new Label("Cannot login user, one or more fields missing.");
		fieldMissing.setStyle("-fx-text-fill: #222222;");
	    GridPane.setColumnSpan(fieldMissing, 3);
	    GridPane.setConstraints(fieldMissing, 1, 2);
		grid.getChildren().addAll(loginBtn, fieldMissing);
		
		// incorrect login label
		invalidLog = new Label(""); // cmd.input
		invalidLog.setStyle("-fx-text-fill: #222222;");
		GridPane.setConstraints(invalidLog, 1, 2);
		grid.getChildren().add(invalidLog);
		
		// guest login button
		guestLoginBtn = new Button("guest login");
		GridPane.setConstraints(guestLoginBtn, 0, 3);
		grid.getChildren().add(guestLoginBtn);
		
		// register button
		registerBtn = new Button("register");
		GridPane.setConstraints(registerBtn, 0, 4);
		grid.getChildren().add(registerBtn);
		
		// quit button
		GridPane.setConstraints(quitBtn, 0, 5);
		grid.getChildren().add(quitBtn);
		
		// add stylesheet to login scene
		grid.getStylesheets().add(stylesheet);
		
		return grid;
	}

	public void incorrect_login() {
		invalidLog.setStyle("-fx-text-fill: #FFFFFF;");
		invalidLog.setText("incorrect user/password");
	}
	
	public void incorrect_login_clear() {
		invalidLog.setStyle("-fx-text-fill: #FFFFFF;");
		invalidLog.setText("");
	}
	
	public void empty_field() {
		fieldMissing.setStyle("-fx-text-fill: #FFFFFF;");
		fieldMissing.setText("Cannot login user, one or more fields missing.");
	}
	
	public void empty_field_clear() {
		fieldMissing.setStyle("-fx-text-fill: #FFFFFF;");
		fieldMissing.setText("");
	}
}
