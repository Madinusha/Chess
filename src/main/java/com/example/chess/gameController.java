package com.example.chess;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Map;

public class gameController {

	@FXML
	public GridPane gridPane;
	private Button resizeButton;
	private Position selectedPosition;
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

				Node cellNode = gridPane.getChildren().get(row * numCols + col);
				int finalRow = row;
				int finalCol = col;
				// Установите обработчик события для нажатия на ячейку
				cellNode.setOnMouseClicked(event -> handleCellClick(finalRow, finalCol));
			}
		}

		placeFiguresSP();
	}
	// Метод для обработки события нажатия на ячейку
	private void handleCellClick(int row, int col) {
		System.out.println("Clicked on cell: " + row + ", " + col);
		if (selectedPosition == null) {
			Position p =  new Position(col+1, 8 - row);
			if (chessboard.getFigureAt(p) != null)
			{
				selectedPosition = p;
			}
		} else {
			Position targetPosition = new Position(col+1, 8 - row);
			if (chessboard.isValidMove(selectedPosition, targetPosition)) {
				moveFigure(selectedPosition, targetPosition);
				System.out.println(selectedPosition + " " + targetPosition);
			}
			selectedPosition = null;
		}
	}
	private void moveFigure(Position from, Position to) {
		Node sourceNode = getNodeAtPosition(from);
		Node targetNode = getNodeAtPosition(to);

		if (sourceNode instanceof StackPane && targetNode instanceof StackPane) {
			StackPane sourceStackPane = (StackPane) sourceNode;
			StackPane targetStackPane = (StackPane) targetNode;

			// Проверяем, есть ли ImageView в sourceStackPane
			ImageView imageView = findImageViewInStackPane(sourceStackPane);

			if (imageView != null) {
				// Создаем анимацию перемещения ImageView
				TranslateTransition transition = new TranslateTransition(Duration.millis(200), imageView);

				// Устанавливаем конечные координаты для анимации (новая позиция)
				transition.setToX(getXCoordinate(from.getColAsNumber(), to.getColAsNumber()));
				transition.setToY(getYCoordinate(from.getRow(), to.getRow()));

				// Обработчик завершения анимации (если нужно)
				transition.setOnFinished(event -> {
					if (chessboard.isEdible(from, to))
					{
						targetStackPane.getChildren().remove(findImageViewInStackPane(targetStackPane));
					}
					chessboard.moveFigure(from, to);
					sourceStackPane.getChildren().remove(imageView);
					targetStackPane.getChildren().add(imageView);
					imageView.setTranslateX(0);
					imageView.setTranslateY(0);

				});

				transition.play();
			} else System.out.println("Не имг вью");
		} else System.out.println("Не стакпейн");
	}
	private ImageView findImageViewInStackPane(StackPane stackPane) {
		for (Node node : stackPane.getChildren()) {
			if (node instanceof ImageView) {
				return (ImageView) node;
			}
		}
		return null;
	}

	private Node getNodeAtPosition(Position position) {
		int index = position.getColAsNumber() - 1 + (8 - position.getRow()) * 8;
		return gridPane.getChildren().get(index);
	}

	private double getXCoordinate(int colFrom, int colTo) {
		return (colTo - colFrom) * (gridPane.getHeight() / 8);
	}

	private double getYCoordinate(int rowFrom, int rowTo) {
		return (rowFrom - rowTo) * (gridPane.getHeight() / 8);
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
