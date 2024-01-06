package com.example.chess;

public class Queen extends Figure {
	public Queen(String color) {
		super(color);
	}

	@Override
	public boolean isValidMove(Position from, Position to, ChessBoard board) {
		// Реализация валидации хода для ферзя
		int rowDifference = Math.abs(to.getRow() - from.getRow());
		int colDifference = Math.abs(to.getCol() - from.getCol());

		// Ход допустим, если ферзь двигается по горизонтали, вертикали или диагонали
		if (rowDifference == 0 || colDifference == 0 || rowDifference == colDifference){
			return !board.areFiguresBetween(from, to);
		}
		return false;
	}
	@Override
	public String toString()
	{
		return (getColor().equals("white")) ? "♕" : "♛";
	}
}
