package edu.alexey.tictactoegame;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameDesk {

	public static record DeskState(boolean gameOver, State winner, int[] winnerLine) {
	}

	final int dim;
	final State[] desk;
	final int[][] scanlines;

	public GameDesk(int dimension) {
		assert dimension >= 2 : dimension;
		dim = dimension;
		desk = new State[dim * dim];
		Arrays.fill(desk, State.NONE);

		int[][] horizontals = new int[dim][];
		Arrays.setAll(horizontals, row -> IntStream.range(row * dim, (row + 1) * dim).toArray());
		int[][] verticals = new int[dim][];
		Arrays.setAll(verticals, col -> IntStream.range(0, dim).map(i -> col + dim * i).toArray());
		int[][] diagonals = {
				IntStream.range(0, dim).map(i -> i * dim + i).toArray(),
				IntStream.range(0, dim).map(i -> dim - 1 + i * (dim - 1)).toArray()
		};
		scanlines = Stream.concat(
				Stream.concat(Arrays.stream(horizontals), Arrays.stream(verticals)),
				Arrays.stream(diagonals)).toArray(int[][]::new);

		// System.out.println("horizontals");
		// for (int[] arr : horizontals) {
		// System.out.println(Arrays.toString(arr));
		// }
		// System.out.println("verticals");
		// for (int[] arr : verticals) {
		// System.out.println(Arrays.toString(arr));
		// }
		// System.out.println("diagonals");
		// for (int[] arr : diagonals) {
		// System.out.println(Arrays.toString(arr));
		// }
		// System.out.println("ALL");
		// for (int[] arr : scanlines) {
		// System.out.println(Arrays.toString(arr));
		// }
	}

	public State cellState(int index) {
		return desk[index];
	}

	public int count() {
		return desk.length;
	}

	public void setCell(int index, State cellState) {
		assert index >= 0 && index < desk.length;
		desk[index] = cellState;
	}

	public DeskState update() {

		boolean nooneCanWin = true;

		for (int[] scanline : scanlines) {

			boolean hasEmptyCells = hasEmptyCells(scanline);
			int statesCount = countStates(scanline);

			if (hasEmptyCells) {
				// если на линии пустые ячейки и метки не боле чем одного игрока,
				// то ситуация nooneCanWin отменяется:
				if (statesCount < 3) {
					nooneCanWin = false;
				}
			} else if (statesCount == 1) {
				// winner!
				return new DeskState(true, desk[scanline[0]], scanline.clone());
			}
		}

		if (nooneCanWin) {
			// drawn game!
			return new DeskState(true, State.NONE, null);
		} else {
			return new DeskState(false, null, null);
		}
	}

	public OptionalInt findOptimumCellIndex(Gamer gamer) {

		State gamerCellState = State.byGamer(gamer);
		State opponentCellState = State.byGamer(Gamer.opponent(gamer));

		int[] gamerMaxFilledLine = null;
		int gamerMaxFilledLineEmptyCount = dim;

		int[] opponentMaxFilledLine = null;
		int opponentMaxFilledLineEmptyCount = dim;

		for (int[] scanline : scanlines) {
			int emptyCount = countCellsByState(scanline, State.NONE);
			// полностью заполненные линии пропускаем
			if (emptyCount == 0) {
				continue;
			}

			int gamerCount = countCellsByState(scanline, gamerCellState);
			int opponentCount = countCellsByState(scanline, opponentCellState);
			// линии, где отметились оба игрока тоже интереса не представляют
			if (gamerCount > 0 && opponentCount > 0) {
				continue;
			}

			if (opponentCount == 0) {
				if (emptyCount == 1) {
					return indexOfAnyOccurrence(scanline, State.NONE);
				}

				if (emptyCount < gamerMaxFilledLineEmptyCount) {
					// на всякий случай запоминаем линию максимально заполненную
					// символом текущего игрока
					gamerMaxFilledLineEmptyCount = emptyCount;
					gamerMaxFilledLine = scanline;
				}
			}

			if (gamerCount == 0) {
				// оппоненту ход до выигрыша - надо помешать
				if (emptyCount == 1) {
					return indexOfAnyOccurrence(scanline, State.NONE);
				}

				if (emptyCount < opponentMaxFilledLineEmptyCount) {
					opponentMaxFilledLineEmptyCount = emptyCount;
					opponentMaxFilledLine = scanline;
				}
			}
		}

		if (gamerMaxFilledLine != null) {
			return indexOfAnyOccurrence(gamerMaxFilledLine, State.NONE);
		}
		if (opponentMaxFilledLine != null) {
			return indexOfAnyOccurrence(opponentMaxFilledLine, State.NONE);
		}
		return OptionalInt.empty();
	}

	public OptionalInt findAnyEmptyCellIndex() {
		int[] emptyCellsIndices = IntStream.range(0, desk.length)
				.filter(i -> desk[i].equals(State.NONE)).toArray();
		if (emptyCellsIndices.length == 0) {
			return OptionalInt.empty();
		}

		int iRnd = ThreadLocalRandom.current().nextInt(0, emptyCellsIndices.length);
		return OptionalInt.of(emptyCellsIndices[iRnd]);

		// for (int i = 0; i < desk.length; ++i) {
		// if (desk[i].equals(State.NONE)) {
		// return OptionalInt.of(i);
		// }
		// }
		// return OptionalInt.empty();
	}

	private boolean hasEmptyCells(int[] line) {
		return Arrays.stream(line).anyMatch(i -> desk[i].equals(State.NONE));
	}

	private int countStates(int[] line) {
		assert line != null && line.length == dim : line;
		return (int) Arrays.stream(line).<State>mapToObj(i -> desk[i])
				.distinct().count();
	}

	private int countCellsByState(int[] line, State cellState) {
		return (int) Arrays.stream(line).filter(i -> desk[i].equals(cellState))
				.count();
	}

	private OptionalInt indexOfAnyOccurrence(int[] line, State cellState) {
		return Arrays.stream(line).filter(i -> desk[i].equals(cellState)).findAny();
	}
}
