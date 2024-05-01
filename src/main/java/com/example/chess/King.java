package com.example.chess;

public class King extends Figure{
	private boolean hasMoved;
	private boolean hasChecked;
	public King(String color) {
		super(color);
		this.hasMoved = false;
		this.hasChecked = false;
	}
	public boolean getHasMoved() {
		return hasMoved;
	}
	public boolean getHasChecked() {
		return hasChecked;
	}
	public void setHasMoved() {
		this.hasMoved = true;
	}
	public void setHasChecked() {
		this.hasChecked = true;
	}

	@Override
	public boolean isValidMove(Position from, Position to, Chessboard board) {
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
