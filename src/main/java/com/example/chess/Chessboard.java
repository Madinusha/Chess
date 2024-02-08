package com.example.chess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chessboard {


	private Map<Position, Figure> board; // Шахматная доска

	public Chessboard() {
		this.board = new HashMap<>();
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
	private void placeFigure(Figure figure, Position position) {
		board.put(position, figure);
	}

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
	public boolean isValidMove(Position from, Position to)
	{
		Figure movingFigure = board.get(from);
		Figure targetFigure = board.get(to);
		if (movingFigure != null) {
			// Проверка, что ход допустим для фигуры
			if (movingFigure.isValidMove(from, to, this)) {
				// Проверка, что в целевой клетке нет фигуры того же цвета
				if (targetFigure == null || !targetFigure.getColor().equals(movingFigure.getColor())) {
					return true;
				}
			}
		}
		return false;
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
						// Обработка события съедания фигуры
						figureCapture(targetFigure);
					}

					// Перемещаем фигуру на новую позицию
					board.remove(from);
					board.put(to, movingFigure);

					// Проверяем, если это пешка, меняем флаг
					if (movingFigure instanceof Pawn) {
						((Pawn) movingFigure).setHasMoved(true);
					}

					// Вывести информацию о ходе
					System.out.println("Успех! " + movingFigure.getColor() + " "  + movingFigure.getClass().getSimpleName() + " сделал ход с " + from + " на " + to);
					return true;
				} else System.out.println(movingFigure.getColor() + " "  + movingFigure.getClass().getSimpleName() + " не может есть фигуру своего цвета.");
			} else System.out.println(movingFigure.getColor() + " "  + movingFigure.getClass().getSimpleName() + " не может сделать ход с " + from + " на " + to);
		}
		return false;
	}
	private void figureCapture(Figure capturedFigure) {
		// код для обработки события съедания фигуры
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
