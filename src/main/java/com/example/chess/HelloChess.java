package com.example.chess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloChess extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(HelloChess.class.getResource("Game.fxml"));
		stage.setTitle("HelloChess!");
		stage.setScene(new Scene(root));
		stage.show();
	}

//	@Override
//	public void start(Stage stage) throws IOException {
//		BorderPane root = FXMLLoader.load(getClass().getResource("registration.fxml"));
//		Scene scene = new Scene(root);
//		stage.setScene(scene);
//		stage.setTitle("Login");
//		stage.setResizable(false);
//		stage.show();
//	}
//


	public static void main(String[] args) {
		launch();
	}
}