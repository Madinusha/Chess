package com.example.chess;

public class Rook extends Figure { // Ладья
	public Rook(String color) {
		super(color);
	}

	@Override
	public boolean isValidMove(Position from, Position to, Chessboard board) {
		// Реализация валидации хода для ладьи
		int rowDifference = Math.abs(to.getRow() - from.getRow());

		int colDifference = Math.abs(to.getCol() - from.getCol());

		// Ход допустим, если ладья двигается по горизонтали или вертикали
		if (rowDifference == 0 || colDifference == 0) {
			return !board.areFiguresBetween(from, to);
		}
		return false;
	}
	@Override
	public String toString()
	{
		return (getColor().equals("white")) ? "♖" : "♜";
	}
}
