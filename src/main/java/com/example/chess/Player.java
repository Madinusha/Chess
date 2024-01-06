package com.example.chess;

public class Player {
	private String username;
	private int score;
	private ChessBoard chessBoard;

	public Player(String username) {
		this.username = username;
		this.score = 0;
		this.chessBoard = new ChessBoard();
	}

	public int getScore() {
		return score;
	}

	public ChessBoard getChessBoard() {
		return chessBoard;
	}

	// Другие методы и геттеры/сеттеры по необходимости
}
