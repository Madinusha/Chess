package com.example.chess;

public class Player {
	private String username;
	private int score;
	private Chessboard chessBoard;

	public Player(String username) {
		this.username = username;
		this.score = 0;
		//this.chessBoard = new Chessboard();
	}

	public int getScore() {
		return score;
	}

	public Chessboard getChessBoard() {
		return chessBoard;
	}

	// Другие методы и геттеры/сеттеры по необходимости
}
