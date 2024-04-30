package com.example.chess;

import controller.gameController;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chessboard {
	private Map<Position, Figure> board; // Шахматная доска
	public gameController controller;

	private List<Pair<Integer, Figure>> eatenFigures;
	private List<Pair<Position, Position>> motionList;

	public Chessboard(gameController controller) {
		this.controller = controller;
		this.board = new HashMap<>();
		this.motionList = new ArrayList<>();
		this.eatenFigures = new ArrayList<>();
		initialize();
	}
	public Chessboard(Map<Position, Figure> board) {
		this.board = new HashMap<>(board);
		this.motionList = new ArrayList<>();
		this.eatenFigures = new ArrayList<>();
	}


	public Map<Position, Figure> getChessboard() {
		return board;
	}
	public void initialize() {
		// Очистить доску перед началом новой игры
		board.clear();

		// Расставить белые фигуры
		placeInitialPieces("white");

		// Расставить черные фигуры
		placeInitialPieces("black");
	}
	public Chessboard(List<Figure> figures, List<Position> positions) {
		if (figures.size() != positions.size()) {
			throw new IllegalArgumentException("Количество фигур должно быть равно количеству позиций.");
		}

		board = new HashMap<>();

		for (int i = 0; i < figures.size(); i++) {
			Figure figure = figures.get(i);
			Position position = positions.get(i);
			placeFigure(figure, position);
		}
	}
	public Chessboard(Chessboard newBoard)
	{
		this.controller = newBoard.controller;
		this.board = newBoard.getChessboard();
		this.motionList = newBoard.getMotionList();
		this.eatenFigures = newBoard.getEatenFigures();
	}
	public List<Pair<Position, Position>> getMotionList() {
		return motionList;
	}
	public void appendInMotionList(Position from, Position to) {
		motionList.add(new Pair<>(from, to));
	}

	public List<Pair<Integer, Figure>> getEatenFigures() {
		return eatenFigures;
	}

	public void addEatenFigures(Figure eatenFigure) {
		this.eatenFigures.add(new Pair<>(getMotionList().size(), eatenFigure));
	}

	public void placeFigure(Figure figure, Position position) {
		board.put(position, figure);
	}
	public void deleteFigureAt(Position position) { board.remove(position); }

	private void placeInitialPieces(String color) {
		int pawnRow = (color.equals("white")) ? 2 : 7;
		int backRow = (color.equals("white")) ? 1 : 8;

		// Расставить пешки
		for (char col = 'a'; col <= 'h'; col++) {
			Position pawnPosition = new Position(col, pawnRow);
			Pawn pawn = new Pawn(color);
			board.put(pawnPosition, pawn);
		}

		// Расставить остальные фигуры
		placeNonPawnPieces(color, backRow);
	}

	private void placeNonPawnPieces(String color, int row) {
		Position rook1Position = new Position('a', row);
		Position knight1Position = new Position('b', row);
		Position bishop1Position = new Position('c', row);
		Position queenPosition = new Position('d', row);
		Position kingPosition = new Position('e', row);
		Position bishop2Position = new Position('f', row);
		Position knight2Position = new Position('g', row);
		Position rook2Position = new Position('h', row);

		// Расставить ладьи
		board.put(rook1Position, new Rook(color));
		board.put(rook2Position, new Rook(color));

		// Расставить кони
		board.put(knight1Position, new Knight(color));
		board.put(knight2Position, new Knight(color));

		// Расставить слоны
		board.put(bishop1Position, new Bishop(color));
		board.put(bishop2Position, new Bishop(color));

		// Расставить ферзя
		board.put(queenPosition, new Queen(color));

		// Расставить короля
		board.put(kingPosition, new King(color));
	}

	public void resetBoard() {
		board.clear();
		initialize();
	}

	public boolean isValidMove(Position from, Position to, Chessboard board) {
		Figure movingFigure = board.getFigureAt(from);
		Figure targetFigure = board.getFigureAt(to);

		if (movingFigure != null && movingFigure.isValidMove(from, to, this)) {
			// Проверяем, чтобы на клетке, на которую совершается ход, не стояла фигура того же цвета
			if (targetFigure == null || !targetFigure.getColor().equals(movingFigure.getColor())) {
				if (!isMoveLeadsToCheck(from, to, movingFigure)) {
					return true;
				}
			}
		}
		return false;
	}
	private void showWinMessage(String winner) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Поздравляем!");
		alert.setHeaderText(null);
		alert.setContentText(winner + " выиграли игру!");

		alert.setOnCloseRequest(event -> {
			controller.gridPane.getChildren().clear();
			controller.initialize();
		});
		alert.show();

	}


	private boolean isMoveLeadsToCheck(Position from, Position to, Figure movingFigure) {
		Map<Position, Figure> tempBoard = new HashMap<>(board);
		tempBoard.put(to, tempBoard.remove(from));

		return isKingInCheck(movingFigure.getColor(), tempBoard);
	}
	public boolean isKingInCheck(String kingColor, Map<Position, Figure> board) {
		// Находим позицию короля нужного цвета
		Position kingPosition = findKingPosition(kingColor, board);
		if (kingPosition == null) {
			System.out.println("kingPosition = null");
			System.out.println("ПРОИГРЫШ");
			return false;
		}
		Chessboard cb = new Chessboard(controller);
		cb.board = board;
		// Проходимся по всей доске и проверяем каждую фигуру
		for (Map.Entry<Position, Figure> entry : board.entrySet()) {
			Position currentPosition = entry.getKey();
			Figure currentFigure = cb.getFigureAt(currentPosition);

			// Если фигура принадлежит другому игроку и может атаковать короля, то король находится под шахом
			if (!currentFigure.getColor().equals(kingColor) && currentFigure.isValidMove(currentPosition, kingPosition, cb)) {
				System.out.println("фигура " + currentFigure.getFileName() + " принадлежит другому игроку и может атаковать короля, ШАХ королю цвета " + cb.getFigureAt(kingPosition).getColor() + cb.getFigureAt(kingPosition).getFileName());
				return true;
			}
		}
		// Если ни одна фигура не атакует короля, значит, он не под шахом
		return false;
	}

	public boolean isCheckmate(String kingColor, Map<Position, Figure> board) {
		System.out.println("\t\t\t вызов checkmate");
		// Проверяем, находится ли король нужного цвета под шахом
		if (isKingInCheck(kingColor, board)) {
			System.out.println("\t\t\t король " + kingColor + " под шахом ");

			// Проходимся по всей доске и проверяем каждую фигуру
			for (Map.Entry<Position, Figure> entry : board.entrySet()) {
				Position currentPosition = entry.getKey();
				Figure currentFigure = entry.getValue();

				if (currentFigure.getColor().equals(kingColor)) {
					Chessboard cb = new Chessboard(controller);
					cb.board = board;

					// Получаем список всех возможных ходов для текущей фигуры
					List<Position> possibleMoves = getPossibleMoves(currentPosition, cb);
					if (possibleMoves.isEmpty()) {
						System.out.println("\t\t\t\tpossibleMoves == null для фигуры " + getFigureAt(currentPosition).getFileName() + " " + currentPosition);
						continue;
						//return true;
					}
					else {
						System.out.println("possibleMoves: ");
						for (Position position : possibleMoves) {
							System.out.print("\t\t\t" + position + " ");
						}
						System.out.println();
					}

					// Проходимся по каждому возможному ходу
					for (Position move : possibleMoves) {
						if (!cb.isMoveLeadsToCheck(currentPosition, move, currentFigure)){
							System.out.println(" ход ведет к шаху у фигуры " + cb.getFigureAt(currentPosition) + " " + move);
							return false;
						}
					}
					//return false;
				}
			}
			// Если не найдено ни одного хода, который бы предотвратил шах, то это мат
			return true;
		}
		// Если король не под шахом, то это не мат
		return false;
	}
	public List<Position> getPossibleMoves(Position currentPosition, Chessboard board) {
		List<Position> possibleMoves = new ArrayList<>();
		Figure myfigure = board.getFigureAt(currentPosition);
		System.out.println("\t\t\t вызываем getPossibleMoves");
		// Получаем цвет текущей фигуры
		String color = myfigure.getColor();

		// Проходимся по всем клеткам на доске
		for (int row = 1; row <= 8; row++) {
			for (int col = 1; col <= 8; col++) {
				Figure figure = myfigure;
				Position targetPosition = new Position(row, col);

				// Проверяем, может ли текущая фигура совершить ход на целевую позицию
				if (isValidMove(currentPosition, targetPosition, board) && currentPosition != targetPosition) {
					// Создаем временную доску и совершаем ход, чтобы проверить, не будет ли король под шахом
					Map<Position, Figure> tempBoard = new HashMap<>(board.getChessboard());
					tempBoard.put(targetPosition, tempBoard.remove(currentPosition));

					// Проверяем, не окажется ли король под шахом после этого хода
					if (!board.isKingInCheck(color, tempBoard)) {
						// Если не окажется, добавляем позицию в список возможных ходов
						possibleMoves.add(targetPosition);
					}
				}
			}
		}

		return possibleMoves;
	}

	public Position findKingPosition(String kingColor, Map<Position, Figure> board) {
		//System.out.println("Ищем короля ");
		Chessboard cb = new Chessboard(controller);
		cb.board = board;
		for (Map.Entry<Position, Figure> entry : board.entrySet()) {
			Position position = entry.getKey();
			Figure figure = cb.getFigureAt(position);
			if (figure instanceof King){
				if (figure.getColor().equals(kingColor)) {
					//System.out.println("КОРОЛЬ "  +  position.toString() + "\n");
					return position;
				} //else System.out.println("Король не того цвета " +  position.toString());
			}
		}
		return null;
	}

	public void eatFigure(Position from, Position to)
	{
		figureCapture(getFigureAt(to));
		deleteFigureAt(to);
		controller.eatFigure(from, to);
	}

	public boolean isEdible(Position from, Position to)
	{
		Figure movingFigure = board.get(from);
		Figure targetFigure = board.get(to);
		if (movingFigure != null) {
			// Проверка, что ход допустим для фигуры
			if (movingFigure.isValidMove(from, to, this)) {
				// Проверка, что в целевой клетке нет фигуры того же цвета
				if (targetFigure == null || !targetFigure.getColor().equals(movingFigure.getColor())) {
					if (targetFigure != null) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean moveFigure(Position from, Position to){
		return moveFigure(from, to, this);
	}

	public boolean moveFigure(Position from, Position to, Chessboard board) {
		Figure movingFigure = board.getFigureAt(from);
		Figure targetFigure = board.getFigureAt(to);

		if (movingFigure != null) {
			// Проверка, что ход допустим для фигуры
			if (movingFigure.isValidMove(from, to, board)) {
				// Проверка, что в целевой клетке нет фигуры того же цвета
				if (targetFigure == null || !targetFigure.getColor().equals(movingFigure.getColor())) {
					// Съедаем фигуру в целевой клетке, если она есть
					if (targetFigure != null) {
						figureCapture(targetFigure);
					}
					if (movingFigure instanceof Pawn && ((Pawn) movingFigure).canCaptureEnPassant(from, to, board)) {
						Pair<Position, Position> lastMove = board.getMotionList().get(board.getMotionList().size() - 1);
						Position toLastMove = lastMove.getValue();
						this.eatFigure(from, toLastMove);
					}

					// Перемещаем фигуру на новую позицию
					board.getChessboard().remove(from);
					board.getChessboard().put(to, movingFigure);
					board.motionList.add(new Pair<>(from, to));

					// Проверяем, если это пешка, меняем флаг
					if (movingFigure instanceof Pawn) {
						checkPromotion(to);
						((Pawn) movingFigure).setHasMoved(true);
					}

					// Вывести информацию о ходе
					System.out.println("Успех! " + movingFigure.getColor() + " " + movingFigure.getClass().getSimpleName() + " сделал ход с " + from + " на " + to + "\n");

					String color = movingFigure.getColor().equals("white")? "black" : "white";
					// Проверяем на мат
					if (isCheckmate(color, board.getChessboard())) {
						showWinMessage(movingFigure.getColor());
						System.out.println("Игра окончена. Шах и мат!");
					}

					return true;
				} else System.out.println(movingFigure.getColor() + " " + movingFigure.getClass().getSimpleName() + " не может есть фигуру своего цвета.");
			}
		}
		return false;
	}

	public void checkPromotion(Position position) { // Только для пешек. Проверяет, оказались ли они на противоположном конце доски
		Figure pawn = getFigureAt(position);
		if (position.getRow() == 1 && pawn.getColor() == "black"|| position.getRow() == 8 && pawn.getColor() == "white") {
			controller.displayPromotionMenu(position);
		}
	}
	public void exchangePawn(Position position, String figName)
	{
		Figure figure = getFigureAt(position);
		switch (figName)
		{
			case "Queen":
				figure = new Queen(figure.getColor());
				break;
			case "Knight":
				figure = new Knight(figure.getColor());
				break;
			case "Rook":
				figure = new Rook(figure.getColor());
				break;
			case "Bishop":
				figure = new Bishop(figure.getColor());
				break;
		}
		placeFigure(figure, position);
	}

	private void figureCapture(Figure capturedFigure) {
		// код для обработки события съедания фигуры
		eatenFigures.add(new Pair<>(motionList.size(), capturedFigure));
		System.out.println("Фигура " + capturedFigure + " съедена!");
	}

	public Figure getFigureAt(Position position) {
		return board.get(position);
	}
	public boolean areFiguresBetween(Position from, Position to) {
		// Проверка наличия фигур на горизонтальном пути
		if (from.getRow() == to.getRow()) {
			int startCol = Math.min(from.getColAsNumber(), to.getColAsNumber());
			int endCol = Math.max(from.getColAsNumber(), to.getColAsNumber());

			for (int col = startCol + 1; col < endCol; col++) {
				Position position = new Position(col, from.getRow());
				if (board.containsKey(position)) {
					return true; // Есть фигура на пути
				}
			}
		}

		// Проверка наличия фигур на вертикальном пути
		if (from.getColAsNumber() == to.getColAsNumber()) {
			int startRow = Math.min(from.getRow(), to.getRow());
			int endRow = Math.max(from.getRow(), to.getRow());

			for (int row = startRow + 1; row < endRow; row++) {
				Position position = new Position(from.getColAsNumber(), row);
				if (board.containsKey(position)) {
					return true; // Есть фигура на пути
				}
			}
		}
		// Проверка наличия фигур на диагональном пути (слева вверх)
		if (from.getRow() > to.getRow() && from.getColAsNumber() > to.getColAsNumber()) {
			for (int i = 1; i < from.getRow() - to.getRow(); i++) {
				Position position = new Position(from.getColAsNumber() - i, from.getRow() - i);
				if (board.containsKey(position)) {
					return true; // Есть фигура на пути
				}
			}
		}

		// Проверка наличия фигур на диагональном пути (справа вниз)
		if (from.getRow() < to.getRow() && from.getColAsNumber() < to.getColAsNumber()) {
			for (int i = 1; i < to.getRow() - from.getRow(); i++) {
				Position position = new Position(from.getColAsNumber() + i, from.getRow() + i);
				if (board.containsKey(position)) {
					return true; // Есть фигура на пути
				}
			}
		}

		// Проверка наличия фигур на диагональном пути (слева вниз)
		if (from.getRow() < to.getRow() && from.getColAsNumber() > to.getColAsNumber()) {
			for (int i = 1; i < to.getRow() - from.getRow(); i++) {
				Position position = new Position(from.getColAsNumber() - i, from.getRow() + i);
				if (board.containsKey(position)) {
					return true; // Есть фигура на пути
				}
			}
		}

		// Проверка наличия фигур на диагональном пути (справа вверх)
		if (from.getRow() > to.getRow() && from.getColAsNumber() < to.getColAsNumber()) {
			for (int i = 1; i < from.getRow() - to.getRow(); i++) {
				Position position = new Position(from.getColAsNumber() + i, from.getRow() - i);
				if (board.containsKey(position)) {
					return true; // Есть фигура на пути
				}
			}
		}

		return false; // Фигур нет на пути
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		for (int row = 8; row >= 1; row--) {
			for (char col = 'a'; col <= 'h'; col++) {
				Position position = new Position(col, row);
				Figure figure = board.get(position);

				// Если на клетке есть фигура, добавляем ее в строку
				if (figure != null) {
					result.append(figure).append(" ");
				} else {
					result.append("   ");
				}
			}
			result.append("\n");
		}

		return result.toString();
	}
}
