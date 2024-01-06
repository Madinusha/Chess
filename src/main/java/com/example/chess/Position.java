package com.example.chess;

import java.util.Objects;

public class Position {
	private int row;
	private char col;

	public Position(char col, int row) {
		this.row = row;
		this.col = col;
	}
	public Position(int col, int row) {
		this.row = row;
		this.col = (char)('a' + col - 1);
	}

	public int getRow() {
		return row;
	}

	public char getCol() {
		return col;
	}
	// Геттер, возвращающий числовое значение столбца
	public int getColAsNumber() {
		return col - 'a' + 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Position position = (Position) obj;
		return row == position.row && col == position.col;
	}

	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}

	@Override
	public String toString()
	{
		return col + "" + row;
	}
}
