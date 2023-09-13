package Client;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Registration_Scene {
	
	public Button quitBtn;
	public TextField username;
	public TextField password;
	public TextField confirmPassword;
	public Label missmatchedPassword;
	public Label userTaken;
	public Button registerMeBtn;
	public Button backToLogin;
	
	public Registration_Scene(Button quitBtn) {
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
		username = new TextField();
		username.setPromptText("new username");
		GridPane.setConstraints(username, 0, 0);
		grid.getChildren().add(username);
		
		// user already exists label
		userTaken = new Label(""); // cmd.input
		GridPane.setConstraints(userTaken, 1, 0);
		grid.getChildren().add(userTaken);
	
		// password field
		password = new TextField();
		password.setPromptText("new password");
		GridPane.setConstraints(password, 0, 1);
		grid.getChildren().add(password);
		
		// password field
		confirmPassword = new TextField();
		confirmPassword.setPromptText("confirm password");
		GridPane.setConstraints(confirmPassword, 0, 2);
		grid.getChildren().add(confirmPassword);
		
		// register button
	    registerMeBtn = new Button("register me");
	    GridPane.setConstraints(registerMeBtn, 0, 3);
	    grid.getChildren().add(registerMeBtn);
	    
	    // mismatched passwords label
	    missmatchedPassword = new Label(""); // passwords must match
		GridPane.setConstraints(missmatchedPassword, 1, 2);
		grid.getChildren().add(missmatchedPassword);
		
		// back to login button
		backToLogin = new Button("back to login");
		GridPane.setConstraints(backToLogin, 0, 4);
		grid.getChildren().add(backToLogin);
		
		// quit button
		GridPane.setConstraints(quitBtn, 0, 5);
		grid.getChildren().add(quitBtn);
	    
	    return grid;
	}

	public void password_missmatch() {
		missmatchedPassword.setText("passwords must match");
	}

	public void user_is_taken(String serverMsg) {
		userTaken.setText(serverMsg);
	}

}
