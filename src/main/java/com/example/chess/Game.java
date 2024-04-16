package com.example.chess;

import java.util.List;

public class Game {
	private Player player1;
	private Player player2;
	private List<Chessboard> boardsList;

	public Game(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
//		this.boardsList = new Chessboard();
	}

	public void startGame() {
		// Логика начала игры
	}

	public void makeMove(Player player, Position from, Position to) {
		// Логика совершения хода
	}

	// Другие методы и логика игры
}
