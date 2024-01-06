package com.example.chess;

import java.util.ArrayList;
import java.util.List;

public class ChessServer {
	private List<Game> activeGames;

	public ChessServer() {
		this.activeGames = new ArrayList<>();
	}

	public void createGame(Player player1, Player player2) {
		Game newGame = new Game(player1, player2);
		activeGames.add(newGame);
	}

	public void handleMove(Game game, Player player, Position from, Position to) {
		// Логика обработки хода в игре
	}

	// Другие методы и логика сервера
}

