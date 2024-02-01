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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
			//double newSize = stackPaneSize / 5;
			double newSize = gridPane.getWidth() / 8 / 5;
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
					resizeButton = new Button();
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
				showValidMove(selectedPosition);
			}
		} else {
			Position targetPosition = new Position(col+1, 8 - row);

			Figure figure1 = chessboard.getFigureAt(selectedPosition);
			Figure figure2 = chessboard.getFigureAt(targetPosition);
			if (figure1 == figure2)
			{
				hideValidMove(selectedPosition);
				selectedPosition = null;
				return;
			}
			if (figure1 != null && figure2 != null)
				if (figure1.getColor() == figure2.getColor())
				{
					hideValidMove(selectedPosition);
					selectedPosition = targetPosition;
					showValidMove(selectedPosition);
					return;
				}
			hideValidMove(selectedPosition);
			if (chessboard.isValidMove(selectedPosition, targetPosition)) {
				moveFigure(selectedPosition, targetPosition);
				System.out.println(selectedPosition + " " + targetPosition);
			}
			selectedPosition = null;
		}
	}
	public void hideValidMove(Position from)
	{
		StackPane sp = (StackPane)getNodeAtPosition(from);
		for (Node node : sp.getChildren()) {
			if (node instanceof Rectangle) {
				sp.getChildren().remove(node);
				break;
			}
		}
		for (int row = 0; row < 8; row++)
		{
			for (int col = 0; col < 8; col++)
			{
				Position to = new Position(col + 1, 8 - row);
				Node nod = getNodeAtPosition(to);
				if (nod instanceof StackPane)
				{
					StackPane stackPane = (StackPane) nod;
					for (Node node : stackPane.getChildren()) {
						if (node instanceof Circle) {
							stackPane.getChildren().remove(node);
							break;
						}
					}
				}
			}
		}
	}
	public void showValidMove(Position from) {
		StackPane sp = (StackPane)getNodeAtPosition(from);
		Rectangle rectangle = new Rectangle();
		rectangle.setWidth(gridPane.getWidth() / 8);
		rectangle.setHeight(gridPane.getHeight() / 8);
		rectangle.setFill(Color.rgb(0, 255, 0, 0.4));

		sp.getChildren().add(1, rectangle);

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Position to = new Position(col + 1, 8 - row);
				StackPane stackPane = (StackPane)getNodeAtPosition(to);
				Figure figureTo = chessboard.getFigureAt(to);
				if (chessboard.isValidMove(from, to)) {
					if (figureTo == null) {
						double radius = stackPane.getHeight() / 6;
						Circle greenCircle = new Circle(radius, Color.GREEN);
						stackPane.getChildren().add(greenCircle);
					}
					else {
						double radius = gridPane.getHeight() / 8 / 2.5;
						Circle circle = new Circle(radius);
						circle.setStroke(Color.GREEN);
						circle.setStrokeWidth(3);
						circle.setFill(null);
						stackPane.getChildren().add(circle);
					}
				}
			}
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

				gridPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
					for (Node node1 : gridPane.getChildren()) {
						if (node instanceof StackPane) {
							StackPane stackPane1 = (StackPane) node1;

							for (Node n : stackPane1.getChildren()) {
								if (n instanceof ImageView){
									((ImageView) n).setFitHeight(newWidth.doubleValue() / 8 / 1.2);
									((ImageView) n).setFitWidth(newWidth.doubleValue() / 8 / 1.2);
								} else if (n instanceof Label) {
									((Label) n).setPrefWidth(stackPane1.getWidth() / 2);
									((Label) n).setPrefHeight(stackPane1.getWidth() / 2);
								} else if (n instanceof Rectangle) {
									((Rectangle) n).setHeight(newWidth.doubleValue() / 8);
									((Rectangle) n).setWidth(newWidth.doubleValue() / 8);
								} else if (n instanceof Circle) {
									Circle circle = (Circle) n;
									if (circle.getFill() != null) {
										// Заполненный круг
										circle.setRadius(newWidth.doubleValue() / 8 / 6);
									} else {
										// Пустой круг
										circle.setRadius(newWidth.doubleValue() / 8 / 2.5);
									}
								}
							}
						}
					}
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

}
