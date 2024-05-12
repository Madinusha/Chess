package controller;

import com.example.chess.*;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class gameController {

	@FXML
	public GridPane gridPane1;
	public GridPane gridPane2;
	private Button resizeButton;
	public Label eatenFigures1;
	public Label eatenFigures2;
	private Position selectedPosition;
	private double mouseX, mouseY;
	public Chessboard chessboard;
	private Player player1;
	private Player player2;

	@FXML
	public void initialize() {
		player1 = new Player("1");
		player1.setColor("white");
		player2 = new Player("2");
		player2.setColor("black");
		eatenFigures1.setText("");
		eatenFigures2.setText("");
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
			double deltaMax = Math.max(deltaX, deltaY);

			double newWidth = gridPane1.getWidth() + deltaMax;

			// Ограничение размеров
			newWidth = Math.max(minSize, Math.min(newWidth, maxSize));

			gridPane1.setPrefSize(newWidth, newWidth);
			gridPane2.setPrefSize(newWidth, newWidth);

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

		gridPane2.setRotate(180);

		System.out.println(chessboard);
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				Position position = new Position(col+1, 7-(row+1));

				Label label1 = new Label();
				Label label2 = new Label();
				label1.setMaxSize(100, 100);
				label2.setMaxSize(100, 100);

				if ((row + col) % 2 == 0) {
					label1.setStyle("-fx-background-color: #F0D9B5;"); // светлая клетка
					label2.setStyle("-fx-background-color: #F0D9B5;"); // светлая клетка
				} else {
					label1.setStyle("-fx-background-color: #B58863;"); // темная клетка
					label2.setStyle("-fx-background-color: #B58863;"); // темная клетка
				}
				label1.setAlignment(Pos.CENTER);
				label1.setStyle(label1.getStyle() + "-fx-alignment: center;");
				label2.setAlignment(Pos.CENTER);
				label2.setStyle(label1.getStyle() + "-fx-alignment: center;");

				StackPane stackPane1 = new StackPane();
				stackPane1.getChildren().add(label1);

				StackPane stackPane2 = new StackPane(); // Создаем новый StackPane для gridPane2
				stackPane2.getChildren().add(label2); // Добавляем label в новый StackPane
				stackPane2.setRotate(180);

				gridPane1.add(stackPane1, col, row);
				gridPane2.add(stackPane2, col, row);

				// Установите обработчик события для нажатия на ячейку для каждого гридпейна отдельно
				Node cellNode1 = gridPane1.getChildren().get(row * numCols + col);
				Node cellNode2 = gridPane2.getChildren().get(row * numCols + col);
				int finalRow = row;
				int finalCol = col;
				cellNode1.setOnMouseClicked(event -> handleCellClick(finalRow, finalCol, gridPane1));
				cellNode2.setOnMouseClicked(event -> handleCellClick(finalRow, finalCol, gridPane2));
			}
		}
		StackPane stackPane;
		stackPane = (StackPane) getNodeAtPosition(new Position(1, 8), gridPane2);
		resizeButton = new Button();
		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(
				20.0, 20.0,
				10.0, 20.0,
				20.0, 10.0
		);
		resizeButton.setGraphic(triangle);
		triangle.setStyle("-fx-fill: brown;");
		resizeButton.setBackground(null); // Убираем фон кнопки
		resizeButton.setBorder(null); // Убираем границы кнопки

		resizeButton.setAlignment(Pos.BOTTOM_RIGHT);

		stackPane.getChildren().add(resizeButton);
		StackPane.setAlignment(resizeButton, Pos.BOTTOM_RIGHT);
		resizeButton.setPrefSize(gridPane1.getHeight() / 8 / 8, gridPane1.getHeight() / 8 / 8);
		resizeButton.setStyle("-fx-cursor: se-resize;");


		placeFiguresSP(gridPane1);
		placeFiguresSP(gridPane2);
	}

	// Метод для изменения размеров треугольника
	private void resizeTriangle(Polygon triangle, double scale) {
		for (int i = 0; i < triangle.getPoints().size(); i++) {
			triangle.getPoints().set(i, triangle.getPoints().get(i) * scale);
		}
	}

	// Метод для обработки события нажатия на ячейку
	private void handleCellClick(int row, int col, GridPane gridPane) {
		System.out.println("Clicked on cell: " + row + ", " + col);
		if (selectedPosition == null) {
			Position p =  new Position(col+1, 8 - row);

			if (chessboard.getFigureAt(p) != null)
			{
				selectedPosition = p;
				if (chessboard.getFigureAt(selectedPosition).getColor().equals("white")) {
					if (gridPane != gridPane2)
						showValidMove(selectedPosition, gridPane1);
					else return;
				} else {
					if (gridPane != gridPane1)
						showValidMove(selectedPosition, gridPane2);
					else return;
				}

			}
		} else {
			Position targetPosition = new Position(col+1, 8 - row);

			Figure figure1 = chessboard.getFigureAt(selectedPosition);
			Figure figure2 = chessboard.getFigureAt(targetPosition);
			if (figure1 == figure2)
			{
				hideValidMove(selectedPosition, gridPane1);
				hideValidMove(selectedPosition, gridPane2);
				selectedPosition = null;
				return;
			}
			if (figure1 != null && figure2 != null)
				if (figure1.getColor().equals(figure2.getColor()))
				{
					hideValidMove(selectedPosition, gridPane1);
					hideValidMove(selectedPosition, gridPane2);

					selectedPosition = targetPosition;
					if (chessboard.getFigureAt(selectedPosition).getColor().equals("white")) {
						if (gridPane != gridPane2)
							showValidMove(selectedPosition, gridPane1);
						else return;
					} else {
						if (gridPane != gridPane1)
							showValidMove(selectedPosition, gridPane2);
						else return;
					}
					return;
				}
			hideValidMove(selectedPosition, gridPane1);
			hideValidMove(selectedPosition, gridPane2);
			if (chessboard.isValidMove(selectedPosition, targetPosition, chessboard)) {
				Pair<Position, Position> rookPositions = chessboard.isCastleMove(selectedPosition, targetPosition, chessboard);
				if(rookPositions != null){
					System.out.println("рокировка " + rookPositions.getKey() + " " + rookPositions.getValue());

					if (chessboard.getFigureAt(selectedPosition).getColor().equals("white")) {
						if (gridPane != gridPane2)
							moveFigure(rookPositions.getKey(), rookPositions.getValue());

					} else if (chessboard.getFigureAt(selectedPosition).getColor().equals("black")) {
						if (gridPane != gridPane1){
							moveFigure(rookPositions.getKey(), rookPositions.getValue());
						}
					}
				} else System.out.println("rookPositions == NULL");

				if (chessboard.getFigureAt(selectedPosition).getColor().equals("white")) {
					if (gridPane != gridPane2){
						moveFigure(selectedPosition, targetPosition);
					}
					else{
						if (gridPane != gridPane1){
							selectedPosition = null;
						}
					}
				} else {
					if (gridPane != gridPane2) {
						selectedPosition = null;
					} else if (gridPane != gridPane1) {
						moveFigure(selectedPosition, targetPosition);
					}
				}

				System.out.println(selectedPosition + " " + targetPosition);
			}
			selectedPosition = null;
		}
	}

	private boolean handleMouseClick = true;
	public void displayPromotionMenu(Position position, GridPane gridPane)
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
		List<ImageView> ivList = new ArrayList<>();
		ivList.add(imageView1);
		ivList.add(imageView2);
		ivList.add(imageView3);
		ivList.add(imageView4);

		button1.setGraphic(imageView1);
		button2.setGraphic(imageView2);
		button3.setGraphic(imageView3);
		button4.setGraphic(imageView4);

		StackPane stackPane = (StackPane)getNodeAtPosition(position, gridPane);
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
				Node node = getNodeAtPosition(position, gridPane);
				for (Node n : ((StackPane)node).getChildren()) {
					if (n instanceof ImageView)
					{
						((StackPane)node).getChildren().add(button.getGraphic());
						((StackPane)node).getChildren().remove(n);
						break;
					}
				}

				GridPane gp2;
				if (gridPane == gridPane1) gp2 = gridPane2;
				else gp2 = gridPane1;
				Node node2 = getNodeAtPosition(position, gp2);
				for (Node n : ((StackPane)node2).getChildren()) {
					if (n instanceof ImageView)
					{
						ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/" + figure.getColor() + "/" + figName + ".png")));
						imageView.setFitHeight(gridPane.getHeight() / 8 / 1.2);
						imageView.setFitWidth(gridPane.getWidth() / 8 / 1.2);
						((StackPane)node2).getChildren().add(imageView);
						((StackPane)node2).getChildren().remove(n);
						break;
					}
				}
				handleMouseClick = false;
			});

			button.setOnMouseEntered(event -> {
				button.setStyle("-fx-background-color: orange;");
			});

			button.setOnMouseExited(event -> {
				button.setStyle("-fx-background-color: lightyellow;");
			});


			handleMouseClick = true;
			gridPane.setOnMouseClicked(event -> {
				if (handleMouseClick) {
					// Обработка щелчка мыши на GridPane
					Node clickedNode = event.getPickResult().getIntersectedNode();
					if (clickedNode != null && !(clickedNode instanceof Button)) {
						gridPane.getChildren().removeAll(button1, button2, button3, button4);

						for (Node node : gridPane.getChildren()) {
							node.setDisable(false);
						}

						Position returningPos = chessboard.getMotionList().get(chessboard.getMotionList().size() - 1).getKey();
						Figure returningFigure = chessboard.getEatenFigures().get(chessboard.getEatenFigures().size() - 1).getValue();

						if (chessboard.getEatenFigures().get(chessboard.getEatenFigures().size() - 1).getKey() != chessboard.getMotionList().size()-1){
							returningFigure = null;
						}

						Node spp = getNodeAtPosition(position, gridPane);
						ImageView iv = findImageViewInStackPane((StackPane) spp);
						((StackPane) spp).getChildren().remove(iv);


						chessboard.placeFigure(chessboard.getFigureAt(position), returningPos);
						placeFigure(figure, returningPos, gridPane);

						// Возвращаем фигуру, которая была съедена, если такая есть
						if (returningFigure != null) {
							chessboard.placeFigure(returningFigure, position);
							placeFigure(returningFigure, position, gridPane);
						}

					}
					selectedPosition = null;
					handleMouseClick = false;

					String color = figure.getColor().equals("white")? "black" : "white";
					// Проверяем на мат
					if (chessboard.isCheckmate(color, chessboard.getChessboard())) {
						chessboard.showWinMessage(figure.getColor());
						System.out.println("Игра окончена. Шах и мат!");
					}
				}
			});
		}
	}
	public void eatFigure(Position from, Position to)
	{
		Node spp1 = getNodeAtPosition(to, gridPane1);
		Node spp2 = getNodeAtPosition(to, gridPane2);

		ImageView iv1 = findImageViewInStackPane((StackPane) spp1);
		ImageView iv2 = findImageViewInStackPane((StackPane) spp2);

		System.out.println("spp1: ");
		for (Node node: ((StackPane) spp1).getChildren()) {
			System.out.print(" " + node);
		}
		System.out.println("spp2: ");
		for (Node node: ((StackPane) spp2).getChildren()) {
			System.out.print(" " + node);
		}

		((StackPane) spp1).getChildren().remove(iv1);
		((StackPane) spp2).getChildren().remove(iv2);
		System.out.println("spp1: ");
		for (Node node: ((StackPane) spp1).getChildren()) {
			System.out.print(" " + node);
		}
		System.out.println("spp2: ");
		for (Node node: ((StackPane) spp2).getChildren()) {
			System.out.print(" " + node);
		}

	}


	public void placeFigure(Figure figure, Position position, GridPane gridPane)
	{
		Node node = getNodeAtPosition(position, gridPane);
		Image image = new Image(getClass().getResourceAsStream("/images/" + figure.getColor() + "/" + figure.getFileName() + ".png"));
		ImageView imageView = new ImageView();
		imageView.setFitHeight(gridPane.getHeight() / 8 / 1.2);
		imageView.setFitWidth(gridPane.getWidth() / 8 / 1.2);
		imageView.setImage(image);

		((StackPane) node).getChildren().add(imageView);
	}
	public void hideValidMove(Position from, GridPane gridPane)
	{
		StackPane sp = (StackPane)getNodeAtPosition(from, gridPane);
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
				Node nod = getNodeAtPosition(to, gridPane);
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
	public void showValidMove(Position from, GridPane gridPane) {
		StackPane sp = (StackPane)getNodeAtPosition(from, gridPane);
		Rectangle rectangle = new Rectangle();
		rectangle.setWidth(gridPane.getWidth() / 8);
		rectangle.setHeight(gridPane.getHeight() / 8);
		rectangle.setFill(Color.rgb(0, 255, 0, 0.4));

		sp.getChildren().add(1, rectangle);

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Position to = new Position(col + 1, 8 - row);
				StackPane stackPane = (StackPane)getNodeAtPosition(to, gridPane);
				Figure figureTo = chessboard.getFigureAt(to);
				if (chessboard.isValidMove(from, to, chessboard)) {
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
		Node sourceNode1 = getNodeAtPosition(from, gridPane1);
		Node targetNode1 = getNodeAtPosition(to, gridPane1);
		Node sourceNode2 = getNodeAtPosition(from, gridPane2);
		Node targetNode2 = getNodeAtPosition(to, gridPane2);

		if (sourceNode1 instanceof StackPane && targetNode1 instanceof StackPane) {
			StackPane sourceStackPane1 = (StackPane) sourceNode1;
			StackPane targetStackPane1 = (StackPane) targetNode1;
			StackPane sourceStackPane2 = (StackPane) sourceNode2;
			StackPane targetStackPane2 = (StackPane) targetNode2;

			// Проверяем, есть ли ImageView в sourceStackPane
			ImageView imageView1 = findImageViewInStackPane(sourceStackPane1);
			ImageView imageView2 = findImageViewInStackPane(sourceStackPane2);

			if (imageView1 != null) {
				StackPane animationPane1 = new StackPane();
				animationPane1.getChildren().add(imageView1);
				StackPane animationPane2 = new StackPane();
				animationPane2.getChildren().add(imageView2);
				animationPane2.setRotate(180);

				// Создаем анимацию перемещения ImageView
				TranslateTransition transition1 = new TranslateTransition(Duration.millis(150), animationPane1);
				TranslateTransition transition2 = new TranslateTransition(Duration.millis(150), animationPane2);

				// Устанавливаем конечные координаты для анимации (новая позиция)
				transition1.setToX(getXCoordinate(from.getColAsNumber(), to.getColAsNumber(), gridPane1));
				transition1.setToY(getYCoordinate(from.getRow(), to.getRow(), gridPane1));
				transition2.setToX(getXCoordinate(from.getColAsNumber(), to.getColAsNumber(), gridPane2));
				transition2.setToY(getYCoordinate(from.getRow(), to.getRow(), gridPane2));

				// Обработчик завершения анимации (если нужно)
				transition1.setOnFinished(event -> {
					if (chessboard.isEdible(from, to))
					{
						targetStackPane1.getChildren().remove(findImageViewInStackPane(targetStackPane1));
						targetStackPane2.getChildren().remove(findImageViewInStackPane(targetStackPane2));
						playMp3(true);

						if (chessboard.getFigureAt(from).getColor().equals("white")){
							eatenFigures1.setText(eatenFigures1.getText() + chessboard.getFigureAt(to));
						} else{
							eatenFigures2.setText(eatenFigures2.getText() + chessboard.getFigureAt(to));
						}
					} else playMp3(false);

					chessboard.moveFigure(from, to);
					sourceStackPane1.getChildren().remove(imageView1);
					sourceStackPane2.getChildren().remove(imageView2);
					targetStackPane1.getChildren().add(imageView1);
					targetStackPane2.getChildren().add(imageView2);
					imageView1.setTranslateX(0);
					imageView1.setTranslateY(0);
					imageView2.setTranslateX(0);
					imageView2.setTranslateY(0);
					gridPane1.getChildren().remove(animationPane1);
					gridPane2.getChildren().remove(animationPane2);

				});
				gridPane1.add(animationPane1, from.getColAsNumber() - 1, 8 - from.getRow());
				gridPane2.add(animationPane2, from.getColAsNumber() - 1, 8 - from.getRow());

				transition1.play();
				transition2.play();

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

	private void playMp3(Boolean eatFigure) {
		new Thread(() -> {
			try {
				File soundFile;
				if (!eatFigure) {
					soundFile = new File(getClass().getResource("/sounds/movePiece.wav").getFile());
				} else {
					soundFile = new File(getClass().getResource("/sounds/eatPiece.wav").getFile());
				}

				AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
				Clip clip = AudioSystem.getClip();
				clip.open(ais);
				clip.setFramePosition(0); //устанавливаем указатель на старт
				clip.start();
			} catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
				exc.printStackTrace();
			}
		}).start();
	}

	private Node getNodeAtPosition(Position position, GridPane gridPane) {
		int index = position.getColAsNumber() - 1 + (8 - position.getRow()) * 8;
		return gridPane.getChildren().get(index);
	}

	private double getXCoordinate(int colFrom, int colTo, GridPane gridPane) {
		return (colTo - colFrom) * (gridPane.getHeight() / 8);
	}

	private double getYCoordinate(int rowFrom, int rowTo, GridPane gridPane) {
		return (rowFrom - rowTo) * (gridPane.getHeight() / 8);
	}

	public void placeFiguresSP(GridPane gridPane) {
		for (Map.Entry<Position, Figure> entry : chessboard.getChessboard().entrySet()) {
			Position position = entry.getKey();
			Figure figure = entry.getValue();
			Node node = gridPane.getChildren().get(position.getColAsNumber() - 1 + (8 - position.getRow()) * 8);

			// Проверка, является ли дочерний узел StackPane
			if (node instanceof StackPane) {
				StackPane stackPane = (StackPane) node;

				// Создание нового ImageView
				ImageView imageView = new ImageView();
				imageView.setFitWidth(gridPane.getWidth() / 8 / 1.2);
				imageView.setFitHeight(gridPane.getWidth() / 8 / 1.2);

				gridPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
					for (Node node1 : gridPane.getChildren()) {
						if (node1 instanceof StackPane) {
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
					resizeButton.setPrefWidth(newWidth.doubleValue() / 8 / 10);
					resizeButton.setPrefHeight(newWidth.doubleValue() / 8 / 10);
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
	public void handleEscapeKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ESCAPE) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Return to the main menu?");
			alert.setHeaderText(null);
			alert.setContentText("Do you want to return to the main menu?");

			ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/black/Knight.png")));
			imageView.setFitWidth(150);
			imageView.setFitHeight(150);
			alert.setGraphic(imageView);
			Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
			InputStream iconStream = HelloChess.class.getResourceAsStream("/icon2.png");
			alertStage.getIcons().add(new Image(iconStream));

			alert.getDialogPane().setStyle("-fx-font-size: 30px; -fx-font-family: 'Poor Richard'; -fx-background-color: #B58863;");

			Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
			okButton.setText("Yes");
			okButton.setStyle("-fx-background-color: #B58863; -fx-background-radius:  20;");

			Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
			cancelButton.setText("Cancel");
			cancelButton.setStyle("-fx-background-color: #E3DECB; -fx-background-radius:  20;");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				Stage stage = (Stage) eatenFigures1.getScene().getWindow();
				try {
					VBox root = FXMLLoader.load(getClass().getResource("/com/example/chess/startPage.fxml"));
					Scene scene = new Scene(root);
					stage.setScene(scene);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// Закрываем алерт
				alert.close();
			}
		}
	}

}
