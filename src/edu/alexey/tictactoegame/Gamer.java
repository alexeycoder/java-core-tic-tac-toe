package edu.alexey.tictactoegame;

public enum Gamer {
	O,
	X;

	public static Gamer opponent(Gamer gamer) {
		return gamer.equals(O) ? X : O;
	}
}
