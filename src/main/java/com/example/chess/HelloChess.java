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
		FXMLLoader fxmlLoader = new FXMLLoader(HelloChess.class.getResource("hello-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 320, 240);
		stage.setTitle("Hello!");
		stage.setScene(scene);
		stage.show();


		ChessBoard chessBoard = new ChessBoard();
		System.out.println("1.\n" + chessBoard + "\n");

		Position from = new Position('d', 2);
		Position to = new Position('d', 3);
		chessBoard.moveFigure(from, to);
		System.out.println("2.\n" + chessBoard);

		from = new Position('e', 2);
		to = new Position('d', 3);
		chessBoard.moveFigure(from, to);
		System.out.println("3.\n" + chessBoard);

		chessBoard.resetBoard();

		Figure whiteQueen = new Queen("white");
		Figure blackRook = new Rook("black");
		// Создаем позиции для фигур
		Position whiteQueenPosition = new Position('f', 1);
		Position blackRookPosition = new Position('f', 8);

		// Создаем список фигур и их позиций
		List<Figure> figures = new ArrayList<>();
		figures.add(whiteQueen);
		figures.add(blackRook);

		List<Position> positions = new ArrayList<>();
		positions.add(whiteQueenPosition);
		positions.add(blackRookPosition);

		// Создаем доску с определенным расположением фигур
		ChessBoard c = new ChessBoard(figures, positions);

		// Выводим доску в консоль
		System.out.println("1.\n" + c);

		from = new Position('f', 8);
		to = new Position('f', 1);
		c.moveFigure(from, to);
		System.out.println("2.\n" + c);
	}

	public static void main(String[] args) {
		launch();
	}
}