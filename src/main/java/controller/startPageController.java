package controller;

import com.example.chess.HelloChess;
import com.example.chess.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class startPageController {
	@FXML
	public Button playButton;
	public Button exitButton;

	public static void openWebpage(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void Login() throws IOException{
		Stage stage = (Stage) playButton.getScene().getWindow();

		Parent root = FXMLLoader.load(HelloChess.class.getResource("Game.fxml"));
		stage.setTitle("HelloChess!");
		InputStream iconStream = HelloChess.class.getResourceAsStream("/icon2.png");
		stage.getIcons().add(new Image(iconStream));
		stage.setScene(new Scene(root));
		stage.show();
	}


	@FXML
	public void exit() {
		// Создаем диалоговое окно подтверждения
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Уже уходите?");
		alert.setHeaderText(null);
		alert.setContentText("Вы уверены, что хотите выйти из приложения?");
		Region region = (Region) alert.getDialogPane();
		region.setStyle("-fx-background-color: pink;");

		ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/white/King.png")));
		imageView.setFitWidth(150); // устанавливаем размер иконки по вашему усмотрению
		imageView.setFitHeight(150);
		alert.setGraphic(imageView);

		// Ожидаем ответа от пользователя
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Если пользователь подтвердил выход, закрываем приложение
				Stage stage = (Stage) exitButton.getScene().getWindow();
				stage.close();
			}
		});
	}
	//	@FXML
//	public void Login() {
//		try {
//			// Создание объекта URL с адресом вашего сервера
//			URL url = new URL("http://localhost:3004/check-authentication");
//
//			// Создание HTTP-соединения
//			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//			// Установка метода запроса (GET, POST и т.д.)
//			connection.setRequestMethod("POST");
//
//			// Отправка запроса
//			int responseCode = connection.getResponseCode();
//			System.out.println("Код ответа сервера: " + responseCode);
//
//			if (responseCode == 200) {
//				// Пользователь аутентифицирован
//				System.out.println("Пользователь аутентифицирован");
//
//				// Получение потока для чтения тела ответа
//				try (InputStream inputStream = connection.getInputStream()) {
//					// Чтение данных из потока
//					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//					StringBuilder response = new StringBuilder();
//					String line;
//					while ((line = reader.readLine()) != null) {
//						response.append(line);
//					}
//
//					// Вывод тела ответа
//					System.out.println("Response Body: " + response.toString());
//					Stage stage = (Stage) playButton.getScene().getWindow();
////					stage.close();
//
//					Parent root = FXMLLoader.load(HelloChess.class.getResource("Game.fxml"));
//					stage.setTitle("HelloChess!");
//					stage.setScene(new Scene(root));
//					stage.show();
//				}
//
//			} else if (responseCode == 401){
//				// Пользователь не аутентифицирован, перенаправляем на страницу регистрации
//				System.out.println("// Пользователь НЕ аутентифицирован");
//
//				String registrationUrl = "http://localhost:3004/registration";
//				openWebpage(registrationUrl);
//			}
//
//			// Закрытие соединения
//			connection.disconnect();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}