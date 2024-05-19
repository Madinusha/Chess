package controller;

import com.example.chess.*;



import com.example.chess.HelloChess;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;


public class playerNameController {
	@FXML
	public Button playButton;
	public TextField player1;
	public TextField player2;
	@FXML
	public void initialize() {
		restrictInputToEnglishAndDigits(player1);
		restrictInputToEnglishAndDigits(player2);
	}
	private void restrictInputToEnglishAndDigits(TextField textField) {
		textField.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, event -> {
			String input = event.getCharacter();
			if (!input.matches("[a-zA-Z]")) {
				event.consume();
			}
		});
	}

	@FXML
	public void play() throws IOException {
		Stage stage = (Stage) playButton.getScene().getWindow();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chess/Game.fxml"));
		Parent root = loader.load();

		// Проверка, что контроллер загружен
		gameController gameController = loader.getController();
		if (gameController == null) {
			System.out.println("GameController is null");
			return;
		}

		String player1Name = player1.getText();
		String player2Name = player2.getText();

		// Убедитесь, что строки не null
		if (player1Name == null || player1Name.equals("")) {
			player1Name = "Player1";
		}
		if (player2Name == null || player2Name.equals("")) {
			player2Name = "Player2";
		}

		gameController.setPlayerNames(player1Name, player2Name);

		stage.setTitle("HelloChess!");
		InputStream iconStream = HelloChess.class.getResourceAsStream("/images/white/Queen.png");
		stage.getIcons().add(new Image(iconStream));
		stage.setScene(new Scene(root));
		stage.show();
	}
	public void handleEscapeKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ESCAPE) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Return to the main menu?");
			alert.setHeaderText(null);
			alert.setContentText("Do you want to return to the main menu?");

			ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/black/Knight.png")));
			imageView.setFitWidth(150);
			imageView.setFitHeight(150);
			alert.setGraphic(imageView);
			Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
			alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/white/Queen.png")));

			alert.getDialogPane().setStyle("-fx-font-size: 30px; -fx-font-family: 'Poor Richard'; -fx-background-color: #B58863;");

			Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
			okButton.setText("Yes");
			okButton.setStyle("-fx-background-color: #B58863; -fx-background-radius:  20;");

			Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
			cancelButton.setText("Cancel");
			cancelButton.setStyle("-fx-background-color: #E3DECB; -fx-background-radius:  20;");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				Stage stage = (Stage) playButton.getScene().getWindow();
				try {
					VBox root = FXMLLoader.load(getClass().getResource("/com/example/chess/startPage.fxml"));
					Scene scene = new Scene(root);
					stage.setScene(scene);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				alert.close();
			}
		}
	}



}
