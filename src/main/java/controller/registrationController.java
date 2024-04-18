package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class registrationController implements Initializable {

	@FXML
	private TextField text_username;
	@FXML
	private PasswordField text_password;
	@FXML
	private Button button_login;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
	}

	@FXML
	private void Login(MouseEvent event) {
		if(text_username.getText().equals("admin") && text_password.getText().equals("adminpassword")){
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Login");
			alert.setContentText("Success");
			alert.setHeaderText("Login Success");
			alert.show();
		}else{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Login");
			alert.setContentText("Username or password is invalid");
			alert.setHeaderText("Login Failed");
			alert.show();
		}
	}
}