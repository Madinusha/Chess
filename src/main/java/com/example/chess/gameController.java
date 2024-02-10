package com.example.chess;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
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

			gridPane.setPrefSize(newWidth, newWidth);

			double newSize = gridPane.getWidth() / 8 / 5;
			resizeButton.setPrefSize(newSize, newSize);

			mouseX = event.getSceneX();
			mouseY = event.getSceneY();
		});
	}
	public void createChessboard()
	{
		gameController controller = this;
		chessboard = new Chessboard(controller);
		// Получите количество строк и столбцов в GridPane
		int numRows = 8;
		int numCols = 8;

		System.out.println(chessboard);
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				Position position = new Position(col+1, 7-(row+1));

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
	public void displayPromotionMenu(Position position)
	{
		for (Node node : gridPane.getChildren()) {
			node.setDisable(true);
		}
		resizeButton.setDisable(false);
		Button button1 = new Button();
		Button button2 = new Button();
		Button button3 = new Button();
		Button button4 = new Button();
		List<Button> bList = new ArrayList<>();
		bList.add(button1);
		bList.add(button2);
		bList.add(button3);
		bList.add(button4);

		Figure figure = chessboard.getFigureAt(position);
		Image image1 = new Image(getClass().getResourceAsStream("/images/" + figure.getColor() + "/Queen.png"));
		Image image2 = new Image(getClass().getResourceAsStream("/images/" + figure.getColor() + "/Bishop.png"));
		Image image3 = new Image(getClass().getResourceAsStream("/images/" + figure.getColor() + "/Rook.png"));
		Image image4 = new Image(getClass().getResourceAsStream("/images/" + figure.getColor() + "/Knight.png"));

		// Создание объекта ImageView и установка изображения
		ImageView imageView1 = new ImageView(image1);
		ImageView imageView2 = new ImageView(image2);
		ImageView imageView3 = new ImageView(image3);
		ImageView imageView4 = new ImageView(image4);

		button1.setGraphic(imageView1);
		button2.setGraphic(imageView2);
		button3.setGraphic(imageView3);
		button4.setGraphic(imageView4);

		StackPane stackPane = (StackPane)getNodeAtPosition(position);
		for (Button button: bList) {
			button.setPrefHeight(gridPane.getHeight() / 8);
			button.setPrefWidth(gridPane.getWidth() / 8);
			button.setAlignment(Pos.CENTER);
			((ImageView)(button.getGraphic())).setFitWidth(gridPane.getWidth() / 8 / 1.2);
			((ImageView)(button.getGraphic())).setFitHeight(gridPane.getHeight() / 8 / 1.2);
			button.getStyleClass().clear();
			button.setStyle("-fx-background-color: lightyellow;");
		}

		int dir = (position.getRow() == 1)? -1 : 1;

		gridPane.add(button1, position.getColAsNumber() - 1, 8 - position.getRow());
		gridPane.add(button2, position.getColAsNumber() - 1, 8 - position.getRow() + dir);
		gridPane.add(button3, position.getColAsNumber() - 1, 8 - position.getRow() + 2 * dir);
		gridPane.add(button4, position.getColAsNumber() - 1, 8 - position.getRow() + 3 * dir);

		for (Button button : bList) {
			int index = bList.indexOf(button);
			String figNameH = "";
			if (index == 0) figNameH = "Queen";
			if (index == 1) figNameH = "Bishop";
			if (index == 2) figNameH = "Rook";
			if (index == 3) figNameH = "Knight";
			final String figName = figNameH;
			button.setOnAction(event -> {
				chessboard.exchangePawn(position, figName);

				gridPane.getChildren().removeAll(button1, button2, button3, button4);

				for (Node node : gridPane.getChildren()) {
					node.setDisable(false);
				}
				Node node = getNodeAtPosition(position);
				for (Node n : ((StackPane)node).getChildren()) {
					if (n instanceof ImageView)
					{
						((StackPane)node).getChildren().add(button.getGraphic());
						((StackPane)node).getChildren().remove(n);
						break;
					}
				}
			});

			button.setOnMouseEntered(event -> {
				button.setStyle("-fx-background-color: orange;");
			});

			button.setOnMouseExited(event -> {
				button.setStyle("-fx-background-color: lightyellow;");
			});
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
