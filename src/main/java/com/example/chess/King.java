package com.example.chess;

public class King extends Figure{
	public King(String color) {
		super(color);
	}

	@Override
	public boolean isValidMove(Position from, Position to, ChessBoard board) {
		int rowDifference = Math.abs(to.getRow() - from.getRow());
		int colDifference = Math.abs(to.getCol() - from.getCol());

		// Король может двигаться на одну клетку в любом направлении
		return (rowDifference <= 1 && colDifference <= 1);
	}
	@Override
	public String toString()
	{
		return (getColor().equals("white")) ? "♔" : "♚";
	}
}
