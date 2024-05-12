package com.example.chess;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
//import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.net.URISyntaxException;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

public class HelloChess extends Application {


	@Override
	public void start(Stage stage) throws IOException {
		VBox root = FXMLLoader.load(getClass().getResource("startPage.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		InputStream iconStream = HelloChess.class.getResourceAsStream("/images/white/Queen.png");
		stage.getIcons().add(new Image(iconStream));
		stage.setTitle("HelloChess!");

		stage.setMinHeight(614);
		stage.setMinWidth(886);
		stage.show();


	}
	//	@Override
	//	public void start(Stage stage) throws IOException {
	//		Parent root = FXMLLoader.load(HelloChess.class.getResource("Game.fxml"));
	//		stage.setTitle("HelloChess!");
	//		InputStream iconStream = HelloChess.class.getResourceAsStream("/icon2.png");
	//		stage.getIcons().add(new Image(iconStream));
	//
	//		stage.setScene(new Scene(root));
	//		stage.show();
	//	}

	public static void main(String[] args) {
		launch();
	}
}