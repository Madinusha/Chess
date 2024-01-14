package com.example.chess;

public class Pawn extends Figure {
	private boolean hasMoved;  // Переменная, отслеживающая, делала ли пешка первый ход

	public Pawn(String color) {
		super(color);
		this.hasMoved = false;
	}
	public boolean getHasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	@Override
	public boolean isValidMove(Position from, Position to, Chessboard board) {
		int direction = getColor().equals("white") ? 1 : -1;

		int rowDifference = to.getRow() - from.getRow();
		int colDifference = Math.abs(to.getCol() - from.getCol());

		// Пешка может двигаться вперед на одну клетку
		if (colDifference == 0 && rowDifference == direction) {
			if (board.getFigureAt(to) == null) {
				return true;
			}
		} else if (!hasMoved && colDifference == 0 && rowDifference == 2 * direction) {
			// Пешка может двигаться на две клетки при первом ходе
			if (board.getFigureAt(to) == null && board.getFigureAt(new Position(from.getRow() + direction, from.getCol())) == null
					&& !board.areFiguresBetween(from, to)) {
				// Проверка, что две клетки вперед свободны и на пути нет фигур
				return true;
			}
		}

		// Пешка может бить фигуры по диагонали
		if (colDifference == 1 && rowDifference == direction) {
			Figure targetFigure = board.getFigureAt(to);
			if (targetFigure != null && !targetFigure.getColor().equals(getColor())) {
				return true;
			}
		}

		return false;
	}
	@Override
	public String toString()
	{
		return (getColor().equals("white")) ? "♙" : "♟";
	}

}