package edu.alexey.tictactoegame;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import edu.alexey.utils.ConsoleUtils;

public class Game {

	private final Scanner scanner;
	private final GameDesk gameDesk;
	private final GameDeskRenderer renderer;
	private Difficulty difficulty;

	private boolean gameOver;
	private State winner;
	private int[] winnerLine;

	private Gamer whoseTurn;
	private Gamer human;

	public Game(Scanner scanner, int dimension, Difficulty difficulty) {
		Objects.requireNonNull(scanner);
		this.scanner = scanner;
		this.difficulty = difficulty;
		gameDesk = new GameDesk(dimension);
		renderer = new GameDeskRenderer(gameDesk);

		gameOver = false;
		winner = State.NONE;
		// whoseTurn = Gamer.X;
		// human = Gamer.X;
	}

	private Gamer tossWhoHuman() {
		final int iterations = 40;
		final long pauseMs = 50;

		System.out.println("Жеребъёвка кем будет играть пользователь:");
		String[] gamers = { GameDeskRenderer.O_SYMBOL, GameDeskRenderer.X_SYMBOL };
		String gamerSymb = gamers[0];
		var rnd = ThreadLocalRandom.current();
		try {
			for (int i = 0; i < iterations; ++i) {
				gamerSymb = gamers[rnd.nextInt(0, gamers.length)];
				System.out.print(gamerSymb);
				Thread.sleep(pauseMs);
			}
		} catch (InterruptedException e) {
			// pass
		}
		System.out.println();
		String whoStr;
		Gamer who;
		if (gamerSymb.equals(GameDeskRenderer.O_SYMBOL)) {
			whoStr = "ноликами";
			who = Gamer.O;
		} else {
			whoStr = "крестиками";
			who = Gamer.X;
		}
		System.out.println("\nПользователь играет " + whoStr + "!");
		System.out.println("Первыми ходят крестики.\n");
		// ConsoleUtils.waitToProceed(scanner, "Нажмите Enter чтобы начать...");

		return who;
	}

	private void goTurn() {
		var whoseNext = Gamer.opponent(whoseTurn);

		if (whoseTurn.equals(human)) {
			// логика для человека
			var cellNumberOpt = ConsoleUtils.askInteger(
					scanner,
					"Ваш ход (или пуcтой ввод чтобы выйти): ",
					num -> gameDesk.cellState(num - 1).equals(State.NONE),
					"Некорректный ввод: ячейка занята или вне допустимого диапазона. "
							+ ConsoleUtils.PLEASE_REPEAT);

			if (cellNumberOpt.isEmpty()) {
				System.out.println("\nИгра прервана и приложение будет закрыто.");
				ConsoleUtils.waitToProceed(scanner);
				System.exit(0);
			}

			gameDesk.setCell(cellNumberOpt.getAsInt() - 1, State.byGamer(whoseTurn));

		} else {
			// логика для ИИ
			int index = gameDesk.findAnyEmptyCellIndex().getAsInt();

			if (difficulty.equals(Difficulty.HARD)) {
				var optimumIndexOpt = gameDesk.findOptimumCellIndex(whoseTurn);
				if (optimumIndexOpt.isPresent()) {
					index = optimumIndexOpt.getAsInt();
				}
			}

			gameDesk.setCell(index, State.byGamer(whoseTurn));
			// renderer.render(whoseTurn);

			try {
				Thread.sleep(500);
				System.out.printf("ИИ сделал ход в ячейку %s. ", GameDeskRenderer.cellNameByIndex(index));
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}

		whoseTurn = whoseNext;
	}

	private void checkForWinner() {
		var deskState = gameDesk.update();
		gameOver = deskState.gameOver();
		winnerLine = deskState.winnerLine();
		winner = deskState.winner();
	}

	private void congratulations() {
		if (winner.equals(State.NONE)) {
			System.out.println("\nУра! Победила ничья! \ud83d\udc6f");
		} else if (winner.equals(State.byGamer(human))) {
			System.out.println("\nПоздравляю, вы победитель! \ud83c\udf7e");
		} else {
			System.out.println("\nВаша песенка спета. Вы проиграли \ud83d\udc79");
		}
		System.out.println();
	}

	public void run() {

		human = tossWhoHuman();
		whoseTurn = Gamer.X;

		while (!gameOver) {
			ConsoleUtils.waitToProceed(scanner);
			renderer.render(whoseTurn);
			goTurn();
			checkForWinner();
		}

		renderer.setWinnerIndices(winnerLine);
		renderer.render(whoseTurn);
		congratulations();
	}

}
