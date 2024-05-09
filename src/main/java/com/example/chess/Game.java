package com.example.chess;

import java.util.List;
import java.util.Random;

public class Game {
	private Player white;
	private Player black;
	private Chessboard chessboard;

	public Game(Player player1, Player player2) {
		Random rand = new Random();
		int rand_color = rand.nextInt(2);
		if (rand_color == 0) {
			this.white = player1;
			player1.setColor("white");
			this.black = player2;
			player2.setColor("black");
		} else {
			this.white = player2;
			player2.setColor("white");
			this.black = player1;
			player1.setColor("black");
		}

	}

	public void startGame() {
		// Логика начала игры
	}

	public void makeMove(Player player, Position from, Position to) {
		// Логика совершения хода
	}

	// Другие методы и логика игры
}

// serverSocket
// получить два соккета и соединить их в этом классе
