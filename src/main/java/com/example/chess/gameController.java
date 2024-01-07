package com.example.chess;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class gameController {
	@FXML
	public GridPane gridPane;

	@FXML
	public void initialize() {
		// Получите количество строк и столбцов в GridPane
		int numRows = 8;
		int numCols = 8;

		// Заполнение ячеек для примера
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				Label label = new Label(+ (row + 1) + "" + (char)('a' + col));
				label.setMaxSize(50, 50); // Установите размер ячейки по вашему выбору

				// Установка стиля для ячейки с индексом 0,0
				if ((row + col) % 2 == 0) {
					label.setStyle("-fx-background-color: #c4ac93;");
				} else {
					label.setStyle("-fx-background-color: #8c6549;");
				}

				this.gridPane.add(label, col, row);
			}
		}
	}
}
