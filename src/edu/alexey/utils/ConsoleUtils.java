package edu.alexey.utils;

import java.util.Scanner;

public class ConsoleUtils {
	public static final String PLEASE_REPEAT = "Пожалуйста попробуйте снова.";
	private static final String PROMPT_RETURN = "Нажмите Ввод чтобы продолжить...";

	public static void waitToProceed(Scanner scanner, String prompt) {
		System.out.println(prompt);
		scanner.nextLine();
	}

	public static void waitToProceed(Scanner scanner) {
		waitToProceed(scanner, PROMPT_RETURN);
	}

	
}
