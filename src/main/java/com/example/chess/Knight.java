package com.example.chess;

public class Knight extends Figure {
	public Knight(String color) {
		super(color);
	}

	@Override
	public boolean isValidMove(Position from, Position to, Chessboard board) {
		int rowDifference = Math.abs(to.getRow() - from.getRow());
		int colDifference = Math.abs(to.getCol() - from.getCol());

		// Ход допустим, если разница в строках равна 2 и разница в столбцах равна 1, или наоборот
		return (rowDifference == 2 && colDifference == 1) || (rowDifference == 1 && colDifference == 2);
	}
	@Override
	public String toString()
	{
		return (getColor().equals("white")) ? "♘" : "♞";
	}
}
