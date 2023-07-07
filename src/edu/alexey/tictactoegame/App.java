package edu.alexey.tictactoegame;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Scanner;

import edu.alexey.utils.UncloseableInputStream;

public class App {

    static final Locale LOCALE = Locale.of("ru", "RU");
    static final Charset CHARSET = Charset.defaultCharset();
    static final Scanner scanner = new Scanner(UncloseableInputStream.wrap(System.in), CHARSET);

    public static void main(String[] args) throws Exception {
        Locale.setDefault(LOCALE);

        var game = new Game(scanner, 3, Difficulty.EASY);
        game.run();
    }
}
