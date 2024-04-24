package com.example.chess;

import controller.gameController;
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
	public List<Pair<Position, Position>> getMotionList() {
		return motionList;
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

	public boolean isValidMove(Position from, Position to) {
		Figure movingFigure = board.get(from);
		Figure targetFigure = board.get(to);

		if (movingFigure != null && movingFigure.isValidMove(from, to, this)) {
			// Проверяем, чтобы на клетке, на которую совершается ход, не стояла фигура того же цвета
			if (targetFigure == null || !targetFigure.getColor().equals(movingFigure.getColor())) {
				if (!isMoveLeadsToCheck(from, to, movingFigure)) {
					System.out.println(from.toString() + " - " + to.toString() + " " + " ведет к шаху? " + isMoveLeadsToCheck(from, to, movingFigure));
					return true;
				}
			} //else System.out.println(from.toString() + " - " + to.toString() + " " + " ведет к шаху? " + isMoveLeadsToCheck(from, to, movingFigure));
		}
		return false;
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
				return true;
			}
		}
		// Если ни одна фигура не атакует короля, значит, он не под шахом
		return false;
	}

	public Position findKingPosition(String kingColor, Map<Position, Figure> board) {
		System.out.println("Ищем короля ");
		Chessboard cb = new Chessboard(controller);
		cb.board = board;
		for (Map.Entry<Position, Figure> entry : board.entrySet()) {
			Position position = entry.getKey();
			Figure figure = cb.getFigureAt(position);
			if (figure instanceof King){
				if (figure.getColor().equals(kingColor)) {
					System.out.println("КОРОЛЬ "  +  position.toString());
					return position;
				} else System.out.println("Король не того цвета " +  position.toString());
			} else System.out.println("Это не король "  +  position.toString());
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

	public boolean moveFigure(Position from, Position to) {
		Figure movingFigure = board.get(from);
		Figure targetFigure = board.get(to);

		if (movingFigure != null) {
			// Проверка, что ход допустим для фигуры
			if (movingFigure.isValidMove(from, to, this)) {
				// Проверка, что в целевой клетке нет фигуры того же цвета
				if (targetFigure == null || !targetFigure.getColor().equals(movingFigure.getColor())) {
					// Съедаем фигуру в целевой клетке, если она есть
					if (targetFigure != null) {
						figureCapture(targetFigure);
					}
					if (movingFigure instanceof Pawn && ((Pawn) movingFigure).canCaptureEnPassant(from, to, this)) {
						Pair<Position, Position> lastMove = this.getMotionList().get(this.getMotionList().size() - 1);
						Position toLastMove = lastMove.getValue();
						this.eatFigure(from, toLastMove);
					}

					// Перемещаем фигуру на новую позицию
					board.remove(from);
					board.put(to, movingFigure);
					motionList.add(new Pair<>(from, to));

					// Проверяем, если это пешка, меняем флаг
					if (movingFigure instanceof Pawn) {
						checkPromotion(to);
						((Pawn) movingFigure).setHasMoved(true);
					}

					// Вывести информацию о ходе
					System.out.println("Успех! " + movingFigure.getColor() + " " + movingFigure.getClass().getSimpleName() + " сделал ход с " + from + " на " + to + "\n");
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
		System.out.println("Фигура съедена!");
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





//	public boolean isCheckmate() {
//		for (Position kingPosition : getKingPositions()) {
//			King king = (King) getFigureAt(kingPosition);
//			if (king != null && king.isCheckmate(this)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean isStalemate() {
//		for (Position kingPosition : getKingPositions()) {
//			King king = (King) getFigureAt(kingPosition);
//			if (king != null && king.isStalemate(this)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean isCheck() {
//		// Проверка на "шах"
//		// (реализация зависит от структуры классов Figure и King)
//		return false;
//	}
//
//	public boolean isDraw() {
//		// Проверка на "ничью"
//		// (реализация зависит от ваших правил для определения ничьи)
//		return false;
//	}
//
//	// Дополнительные методы, если необходимо

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
