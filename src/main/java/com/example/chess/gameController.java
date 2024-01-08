package com.example.chess;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class gameController {

	@FXML
	public GridPane gridPane;
	private Button resizeButton;

	private double mouseX, mouseY;

	@FXML
	public void initialize() {
		ChessBoard chessBoard = new ChessBoard();
		// Получите количество строк и столбцов в GridPane
		int numRows = 8;
		int numCols = 8;

		// Заполнение ячеек для примера
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				Label label = new Label(+ (row + 1) + "" + (char)('a' + col));
				label.setMaxSize(100, 100);

				String figureFileName = chessBoard.getFigureAt(new Position(col, row)).getFileName();

				if ((row + col) % 2 == 0) {
					label.setStyle("-fx-background-color: #F0D9B5;"); // светлая клетка
				} else {
					label.setStyle("-fx-background-color: #B58863;"); // темная клетка
				}
				label.setAlignment(Pos.CENTER);
				//label.setStyle(label.getStyle() + "-fx-alignment: center;");

				this.gridPane.add(label, col, row);
			}
		}
		resizeButton = new Button("Resize");

		gridPane.add(resizeButton, 7, 7); // Добавление кнопки в последнюю ячейку
//		gridPane.setPrefSize(400, 400);

		// Установка слушателей событий мыши для кнопки
		resizeButton.setOnMousePressed(event -> {
			mouseX = event.getSceneX();
			mouseY = event.getSceneY();
		});

		resizeButton.setOnMouseDragged(event -> {
			double maxSize = 500;
			double minSize = 300;
			double deltaX = event.getSceneX() - mouseX;
			double deltaY = event.getSceneY() - mouseY;
			double detalMax = Math.max(deltaX, deltaY);
			if (deltaX > maxSize && deltaY > maxSize) {
				gridPane.setMaxSize(maxSize, maxSize);
			} else {
				gridPane.setPrefSize(gridPane.getWidth() + detalMax, gridPane.getHeight() + detalMax);
			}

			mouseX = event.getSceneX();
			mouseY = event.getSceneY();
		});
	}
}
