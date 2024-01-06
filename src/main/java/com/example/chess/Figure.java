package com.example.chess;

public abstract class Figure {
	private String color; // Цвет фигуры ("white" или "black")

	public Figure(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public abstract boolean isValidMove(Position from, Position to, ChessBoard board);
}
