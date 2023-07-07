package edu.alexey.tictactoegame;

import java.util.Arrays;
import java.util.Objects;
import edu.alexey.utils.StringUtils;

public class GameDeskRenderer {

	final static String X_NAME = "крестики";
	final static String O_NAME = "нолики";
	final static String X_SYMBOL = "X";
	final static String O_SYMBOL = "O";
	final static int CELL_PADDING = 3;
	final static String EM_SPACE = "\u2592";

	private final GameDesk gameDesk;

	private int[] winnerIndices;
	private boolean gameOver;

	public GameDeskRenderer(GameDesk gameDesk) {
		Objects.requireNonNull(gameDesk);
		this.gameDesk = gameDesk;
	}

	public void render(Gamer whoseTurn) {

		renderGamersState(whoseTurn);

		int lastCellNumber = gameDesk.count();
		int cellHSize = Integer.toString(lastCellNumber).length() + 2 * CELL_PADDING;
		if (cellHSize % 2 == 0) {
			--cellHSize;
		}

		int dim = gameDesk.dim;
		for (int rowIndex = 0; rowIndex < dim; ++rowIndex) {
			int cellIndexFrom = rowIndex * dim;
			printRow(cellIndexFrom, cellHSize);
		}
	}

	public void setWinnerIndices(int[] indices) {
		winnerIndices = indices;
	}

	private void renderGamersState(Gamer highlighted) {
		// final int legendLen = 1 + 6 + X_NAME.length() + O_NAME.length();
		String xName = X_NAME;
		String oName = O_NAME;

		if (highlighted.equals(Gamer.X)) {
			xName = String.format(" \u25b6 %s \u25c0 ", xName);
			oName = String.format("  %s  ", oName);
		} else {
			oName = String.format(" \u25b6 %s \u25c0 ", oName);
			xName = String.format("  %s  ", xName);
		}
		System.out.println(xName + "|" + oName);
	}

	private void printRow(int indexFrom, int cellHSize) {
		int dim = gameDesk.dim;
		int midsPlusOne = dim - 1;
		String horizLine = SpecSymb.HORIZ.repeat(cellHSize);

		String valueLine = SpecSymb.VERT;
		String placeholderLine = SpecSymb.VERT;
		for (int i = indexFrom; i < indexFrom + dim; ++i) {
			valueLine += getCellString(i, cellHSize) + SpecSymb.VERT;
			placeholderLine += getCellString(i, cellHSize, true) + SpecSymb.VERT;
		}

		String upperFrame = "";
		String bottomFrame = "";
		if (indexFrom == 0) {
			upperFrame = SpecSymb.TOP_LEFT + horizLine
					+ (SpecSymb.TOP_TO_VERT + horizLine).repeat(midsPlusOne)
					+ SpecSymb.TOP_RIGHT;
		} else {
			upperFrame = SpecSymb.LEFT_TO_HORIZ + horizLine
					+ (SpecSymb.CROSS + horizLine).repeat(midsPlusOne)
					+ SpecSymb.RIGHT_TO_HORIZ;

			if (indexFrom == (dim - 1) * dim) {
				bottomFrame = SpecSymb.BTM_LEFT + horizLine
						+ (SpecSymb.BTM_TO_VERT + horizLine).repeat(midsPlusOne)
						+ SpecSymb.BTM_RIGHT;
			}
		}

		System.out.println(upperFrame);
		System.out.println(placeholderLine);
		System.out.println(valueLine);
		System.out.println(placeholderLine);
		if (!bottomFrame.isEmpty()) {
			System.out.println(bottomFrame);
		}
	}

	private String getCellString(int index, int hSize) {
		return getCellString(index, hSize, false);
	}

	private String getCellString(int index, int hSize, boolean placeholderMode) {
		boolean isWinnerCell = gameOver && Arrays.binarySearch(winnerIndices, index) >= 0;
		String space = isWinnerCell ? EM_SPACE : " ";
		State cellState = gameDesk.cellState(index);
		String cellString = space.repeat(hSize);
		if (cellState.equals(State.NONE)) {
			if (!placeholderMode) {
				cellString = StringUtils.padCenter(cellNameByIndex(index), space, hSize);
			}
		} else {
			if (!placeholderMode) {
				cellString = cellState.equals(State.X) ? X_SYMBOL : O_SYMBOL;
				cellString = StringUtils.padCenter(cellString, space, hSize);
			}
		}
		return cellString;
	}

	public static String cellNameByIndex(int index) {
		return Integer.toString(index + 1);
	}

	private static class SpecSymb {
		final static String HORIZ = "\u2550";
		final static String VERT = "\u2551";

		final static String TOP_LEFT = "\u2554";
		final static String TOP_RIGHT = "\u2557";
		final static String TOP_TO_VERT = "\u2566";

		final static String BTM_LEFT = "\u255a";
		final static String BTM_RIGHT = "\u255d";
		final static String BTM_TO_VERT = "\u2569";

		final static String LEFT_TO_HORIZ = "\u2560";
		final static String RIGHT_TO_HORIZ = "\u2563";

		final static String CROSS = "\u256c";
	}
}
