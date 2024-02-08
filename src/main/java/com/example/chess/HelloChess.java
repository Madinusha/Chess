package com.example.chess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloChess extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(HelloChess.class.getResource("Game.fxml"));
		gameController gameController = new gameController();
		fxmlLoader.setController(gameController);
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("HelloChess!");

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}