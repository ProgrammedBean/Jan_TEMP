package Client;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Login_Scene {

	public Button quitBtn;
	public TextField username;
	private static Label userLbl;
	public TextField password;
	private static Label passLbl;
	public Button loginBtn;
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
		userLbl = new Label("username:");
		GridPane.setConstraints(userLbl, 0, 0);
		username = new TextField();
		username.setPromptText("username");
		GridPane.setConstraints(username, 1, 0);
		grid.getChildren().addAll(userLbl, username);
		
		// password field
		passLbl = new Label("password:");
		GridPane.setConstraints(passLbl, 0, 1);
		password = new TextField();
		password.setPromptText("password");
		GridPane.setConstraints(password, 1, 1);
		grid.getChildren().addAll(passLbl, password);
		
		// login button
		loginBtn = new Button("login");
		GridPane.setConstraints(loginBtn, 0, 2);
		grid.getChildren().add(loginBtn);
		
		// incorrect login label
		invalidLog = new Label(""); // cmd.input
		GridPane.setConstraints(invalidLog, 1, 2);
		grid.getChildren().add(invalidLog);
		
		// login as guest button
		registerBtn = new Button("register");
		GridPane.setConstraints(registerBtn, 0, 3);
		grid.getChildren().add(registerBtn);
		
		// quit button
		GridPane.setConstraints(quitBtn, 0, 4);
		grid.getChildren().add(quitBtn);
		
		return grid;
	}

	public void incorrect_login(String serverMsg) {
		invalidLog.setText(serverMsg);
	}
}
