package com.example.chess;

public class Bishop extends Figure{ // Офицер
	public Bishop(String color) {
		super(color);
	}
	@Override
	public boolean isValidMove(Position from, Position to, ChessBoard board) {
		int rowDifference = Math.abs(to.getRow() - from.getRow());
		int colDifference = Math.abs(to.getCol() - from.getCol());

		if (rowDifference == colDifference){
			return !board.areFiguresBetween(from, to);
		}
		return false;
	}
	@Override
	public String toString()
	{
		return (getColor().equals("white")) ? "♗" : "♝";
	}
}
