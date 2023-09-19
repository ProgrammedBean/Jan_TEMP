package Client;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Registration_Scene {
	
	public static final String stylesheet = "General_Scene.css";
	
	public Button quitBtn;
	private static Label userLbl;
	public TextField username;
	private static Label passLbl;
	public TextField password;
	public static Label confPassLbl;
	public TextField confirmPassword;
	private Label missmatchedPassword;
	private Label userTaken;
	public Button registerMeBtn;
	private Label fieldMissing;
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
		userLbl = new Label("Username:");
		GridPane.setConstraints(userLbl, 0, 0);
		username = new TextField();
		username.setPromptText("new username");
		GridPane.setConstraints(username, 1, 0);
		userTaken = new Label("this username is already taken");
		userTaken.setStyle("-fx-text-fill: #222222;");
		GridPane.setConstraints(userTaken, 2, 0);
		grid.getChildren().addAll(userLbl, username, userTaken);
		
		// password field
		passLbl = new Label("Password:");
		GridPane.setConstraints(passLbl, 0, 1);
		password = new TextField();
		password.setPromptText("new password");
		GridPane.setConstraints(password, 1, 1);
		confPassLbl = new Label("Confirm Password:");
		GridPane.setConstraints(confPassLbl, 0, 2);
		confirmPassword = new TextField();
		confirmPassword.setPromptText("confirm password");
		GridPane.setConstraints(confirmPassword, 1, 2);
	    missmatchedPassword = new Label(""); // passwords must match
		GridPane.setConstraints(missmatchedPassword, 2, 2);
		grid.getChildren().addAll(passLbl, password, confPassLbl, confirmPassword, missmatchedPassword);
		
		// register button
	    registerMeBtn = new Button("register me");
	    GridPane.setConstraints(registerMeBtn, 0, 3);
	    fieldMissing = new Label("");
	    GridPane.setColumnSpan(fieldMissing, 3);
	    GridPane.setConstraints(fieldMissing, 1, 3);
	    grid.getChildren().addAll(registerMeBtn, fieldMissing);
		
		// back to login button
		backToLogin = new Button("back to login");
		GridPane.setConstraints(backToLogin, 0, 4);
		grid.getChildren().add(backToLogin);
		
		// quit button
		GridPane.setConstraints(quitBtn, 0, 5);
		grid.getChildren().add(quitBtn);
		
		// add stylesheet to register scene
		grid.getStylesheets().add(stylesheet);
	    
	    return grid;
	}

	public void password_missmatch() {
		missmatchedPassword.setText("passwords must match");
	}
	
	public void password_missmatch_clear() {
		missmatchedPassword.setText("");
	}

	public void user_taken(String serverMsg) {
		userTaken.setStyle("-fx-text-fill: #FFFFFF;");
		userTaken.setText(serverMsg);
	}
	
	public void user_taken_clear() {
		userTaken.setStyle("-fx-text-fill: #FFFFFF;");
		userTaken.setText("");
	}
	
	public void empty_field() {
		fieldMissing.setText("Cannot register user, one or more fields missing.");
	}
	
	public void empty_field_clear() {
		fieldMissing.setText("");
	}

}
