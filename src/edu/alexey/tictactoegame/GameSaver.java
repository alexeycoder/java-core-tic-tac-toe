package edu.alexey.tictactoegame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class GameSaver {

	private static final Path GAMESAVEFILE = Paths.get("game.save");

	private static final int EMPTY = 0;
	private static final int X = 1;
	private static final int O = 2;
	private static final int END = 3;

	public static boolean isThereSave() {
		return Files.isRegularFile(GAMESAVEFILE);
	}

	public static void save(GameDesk gameDesk, Gamer human) {

		int encoded = encode(gameDesk, human);

		try (FileOutputStream fos = new FileOutputStream(GAMESAVEFILE.toFile(), false);
				DataOutputStream dos = new DataOutputStream(fos)) {

			dos.writeInt(encoded);
			dos.flush();

		} catch (IOException e) {
			System.err.println("Не удалось сохранить игру.");
			e.printStackTrace();
		}
	}

	public static Gamer restore(GameDesk gameDesk) {

		try (FileInputStream fis = new FileInputStream(GAMESAVEFILE.toFile());
				DataInputStream dis = new DataInputStream(fis)) {

			int encoded = dis.readInt();
			if (encoded == 0) {
				System.err.println("Не удалось загрузить сохранённую игру.");
				return null;
			}
			return decode(encoded, gameDesk);

		} catch (IOException e) {
			System.err.println("Не удалось загрузить сохранённую игру.");
			e.printStackTrace();
			return null;
		}
	}

	private static int encode(GameDesk gameDesk, Gamer human) {
		int len = gameDesk.count();
		int[] states = new int[len];
		for (int i = 0; i < gameDesk.count(); ++i) {
			states[i] = switch (gameDesk.cellState(i)) {
				case NONE -> EMPTY;
				case X -> X;
				case O -> O;
				default -> throw new NoSuchElementException();
			};
		}
		int whosHuman = human.equals(Gamer.X) ? X : O;

		int encoded = END;
		for (int state : states) {
			encoded <<= 2;
			encoded += state;
		}
		encoded <<= 2;
		encoded += whosHuman;

		return encoded;
	}

	private static Gamer decode(int encoded, GameDesk gameDesk) {
		int whosHuman = 3 & encoded;

		// для потенциальной возможности кодировать большие чем 3x3 поля
		// в будущих версиях
		final int maxBitPairs = 32 / 2 - 1;
		ArrayList<Integer> restored = new ArrayList<>();
		for (int i = 0; i < maxBitPairs; ++i) {
			encoded >>= 2;
			int state = 3 & encoded;
			if (state == END) {
				break;
			}
			restored.add(state);
		}
		Collections.reverse(restored);

		for (int i = 0; i < restored.size(); ++i) {
			State state = switch (restored.get(i)) {
				case EMPTY -> State.NONE;
				case X -> State.X;
				case O -> State.O;
				default -> throw new NoSuchElementException();
			};
			gameDesk.setCell(i, state);
		}

		return whosHuman == X ? Gamer.X : Gamer.O;
	}

}
