package com.example.chess;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Map;

public class gameController {

	@FXML
	public GridPane gridPane;
	private Button resizeButton;

	private double mouseX, mouseY;
	public Chessboard chessboard;

	@FXML
	public void initialize() {
		createChessboard();
		// Установка слушателей событий мыши для кнопки
		resizeButton.setOnMousePressed(event -> {
			mouseX = event.getSceneX();
			mouseY = event.getSceneY();
		});
		resizeButton.setOnMouseDragged(event -> {
			double maxSize = 500;
			double minSize = 250;
			double deltaX = event.getSceneX() - mouseX;
			double deltaY = event.getSceneY() - mouseY;
			double detalMax = Math.max(deltaX, deltaY);

			double newWidth = gridPane.getWidth() + detalMax;
			double newHeight = gridPane.getHeight() + detalMax;

			// Ограничение размеров
			newWidth = Math.max(minSize, Math.min(newWidth, maxSize));
			//newHeight = Math.max(minSize, Math.min(newHeight, maxSize));

			// Установка нового размера для GridPane
			gridPane.setPrefSize(newWidth, newWidth);

			// Установка нового размера для кнопки
			double stackPaneSize = ((StackPane) resizeButton.getParent()).getHeight();
			double newSize = stackPaneSize / 5;
			resizeButton.setPrefSize(newSize, newSize);

			mouseX = event.getSceneX();
			mouseY = event.getSceneY();
		});
	}
	public void createChessboard()
	{
		chessboard = new Chessboard();
		// Получите количество строк и столбцов в GridPane
		int numRows = 8;
		int numCols = 8;

		System.out.println(chessboard);
		// Заполнение ячеек для примера
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				Position position = new Position(col+1, 8-row);


				Label label = new Label();
				label.setMaxSize(100, 100);

				if ((row + col) % 2 == 0) {
					label.setStyle("-fx-background-color: #F0D9B5;"); // светлая клетка
				} else {
					label.setStyle("-fx-background-color: #B58863;"); // темная клетка
				}
				label.setAlignment(Pos.CENTER);
				label.setStyle(label.getStyle() + "-fx-alignment: center;");

				StackPane stackPane = new StackPane();
				stackPane.getChildren().add(label);

				if (col == 7 && row == 7)
				{
					resizeButton = new Button(".");
					stackPane.getChildren().add(resizeButton);
					StackPane.setAlignment(resizeButton, Pos.BOTTOM_RIGHT);
					//resizeButton.setPrefSize(stackPane.getHeight()/5, stackPane.getHeight()/5);
				}
				gridPane.add(stackPane, col, row);
			}
		}

		placeFiguresSP();
	}

	public void placeFiguresSP() {
		for (Map.Entry<Position, Figure> entry : chessboard.getChessboard().entrySet()) {
			Position position = entry.getKey();
			Figure figure = entry.getValue();
			Node node = gridPane.getChildren().get(position.getColAsNumber() - 1 + (8 - position.getRow()) * 8);

			// Проверка, является ли дочерний узел StackPane
			if (node instanceof StackPane) {
				StackPane stackPane = (StackPane) node;

				// Создание нового ImageView
				ImageView imageView = new ImageView();
				imageView.setFitWidth(50);
				imageView.setFitHeight(50);

				// Установка слушателей изменения размера для ImageView
				stackPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
					imageView.setFitWidth(newWidth.doubleValue() / 1.2);
				});

				stackPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
					imageView.setFitHeight(newHeight.doubleValue() / 1.2);
				});

				// Проверка, есть ли уже дочерний узел типа ImageView и удаление его, если есть
				stackPane.getChildren().removeIf(child -> child instanceof ImageView);

				if (figure != null) {
					Image image = new Image(getClass().getResourceAsStream("/images/" + figure.getColor() + "/" + figure.getFileName() + ".png"));
					imageView.setImage(image);

					// Добавление ImageView в StackPane
					stackPane.getChildren().add(imageView);
				}
			}
		}
	}

	public void placeFiguresL()
	{
		for (Map.Entry<Position, Figure> entry : chessboard.getChessboard().entrySet()) {
			Position position = entry.getKey();
			Figure figure = entry.getValue();
			Node node = gridPane.getChildren().get(position.getColAsNumber()-1 + (8 - position.getRow()) * 8);

			// Проверка, является ли дочерний узел Label
			if (node instanceof Label) {
				Label label = (Label) node;
				if (figure != null)
				{
					Image image = new Image(getClass().getResourceAsStream("/images/" + figure.getColor() + "/" + figure.getFileName() + ".png"));

					ImageView imageView = new ImageView(image);

					// Установка слушателя изменения размера для лейбла
					label.widthProperty().addListener((obs, oldWidth, newWidth) -> {
						imageView.setFitWidth(newWidth.doubleValue() / 1.2);
					});

					label.heightProperty().addListener((obs, oldHeight, newHeight) -> {
						imageView.setFitHeight(newHeight.doubleValue() / 1.2);
					});

					imageView.setFitWidth(50);
					imageView.setFitHeight(50);

					label.setGraphic(imageView);
				}
			}
		}

	}


}
