package com.example.chess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloChess extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(HelloChess.class.getResource("registration.fxml"));
		stage.setTitle("HelloChess!");
		stage.setScene(new Scene(root));
		stage.show();
	}


	public static void main(String[] args) {
		launch();
	}
}