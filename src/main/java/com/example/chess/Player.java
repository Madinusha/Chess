package com.example.chess;

public class Player {
	private String username;
	private int score;
	private Chessboard chessBoard;
	private String color;

	public Player(String username) {
		this.username = username;
		this.score = 0;
		//this.chessBoard = new Chessboard();
	}

	public int getScore() {
		return score;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color){
		this.color = color;
	}

	public Chessboard getChessBoard() {
		return chessBoard;
	}

}
