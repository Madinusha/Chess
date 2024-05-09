package com.example.chess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.net.URISyntaxException;

public class HelloChess extends Application {
//	@Override
//	public void start(Stage stage) throws IOException {
//		Parent root = FXMLLoader.load(HelloChess.class.getResource("Game.fxml"));
//		stage.setTitle("HelloChess!");
//		stage.setScene(new Scene(root));
//		stage.show();
//	}

	@Override
	public void start(Stage stage) throws IOException {
		VBox root = FXMLLoader.load(getClass().getResource("startPage.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Start");
		stage.show();
	}




	public static void main(String[] args) {
		launch();
	}
}