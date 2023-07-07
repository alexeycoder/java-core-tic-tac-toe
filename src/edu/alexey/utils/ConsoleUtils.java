package edu.alexey.utils;

import java.util.OptionalInt;
import java.util.Scanner;
import java.util.function.Function;

public class ConsoleUtils {
	public static final String PLEASE_REPEAT = "Пожалуйста попробуйте снова.";
	private static final String PROMPT_RETURN = "Нажмите Ввод чтобы продолжить...";

	private static final String ERR_NOT_INT = "Некорректный ввод: Требуется целое число. " + PLEASE_REPEAT;
	private static final String ERR_INT_MUST_BE_IN_RANGE = "Число должно быть в интервале от %d до %d! "
			+ PLEASE_REPEAT;
	private static final String ERR_INT_TOO_LOW = "Число не должно быть меньше %d! " + PLEASE_REPEAT;
	private static final String ERR_INT_TOO_HIGH = "Число не должно быть больше %d! " + PLEASE_REPEAT;

	public static void waitToProceed(Scanner inputScanner, String prompt) {
		System.out.println(prompt);
		inputScanner.nextLine();
	}

	public static void waitToProceed(Scanner inputScanner) {
		waitToProceed(inputScanner, PROMPT_RETURN);
	}

	public static boolean askYesNo(Scanner inputScanner, String prompt, boolean isYesDefault) {
		System.out.print(prompt);
		var answer = inputScanner.nextLine();

		if (answer.isBlank()) {
			return isYesDefault;
		}

		if (answer.startsWith("y") || answer.startsWith("д") || answer.startsWith("l")) {
			return true;
		} else if (answer.startsWith("n") || answer.startsWith("н") || answer.startsWith("т")) {
			return false;
		} else {
			return isYesDefault;
		}
	}

	public static OptionalInt askInteger(Scanner inputScanner, String prompt, Integer min, Integer max) {
		Integer answer = getUserInputIntRange(inputScanner, prompt, min, max);
		if (answer != null) {
			return OptionalInt.of(answer);
		} else {
			return OptionalInt.empty();
		}
	}

	public static Integer getUserInputIntRange(
			Scanner inputScanner, String prompt,
			Integer min, Integer max) {

		boolean isMinSet = min != null && !min.equals(Integer.MIN_VALUE);
		boolean isMaxSet = max != null && !max.equals(Integer.MAX_VALUE);

		boolean wrongType = false;
		boolean outOfRange = false;

		while (true) {
			if (wrongType) {
				wrongType = false;
				printError(ERR_NOT_INT);
			}
			if (outOfRange) {
				outOfRange = false;
				String errOutOfRange;
				if (isMinSet && isMaxSet) {
					errOutOfRange = String.format(ERR_INT_MUST_BE_IN_RANGE, min, max);
				} else if (isMinSet) {
					errOutOfRange = String.format(ERR_INT_TOO_LOW, min);
				} else {
					errOutOfRange = String.format(ERR_INT_TOO_HIGH, max);
				}
				printError(errOutOfRange);
			}

			System.out.print(prompt);
			var rawInp = inputScanner.nextLine();
			if (rawInp.isBlank()) {
				return null;
			}
			var value = tryParseInt(rawInp);
			if (value == null) {
				wrongType = true;
			} else {
				if (!(outOfRange = isOutOfRange(value, min, max))) {
					return value;
				}
			}
		}
	}

	private static boolean isOutOfRange(Integer value, Integer min, Integer max) {
		return (min != null && value < min) || (max != null && value > max);
	}

	public static OptionalInt askInteger(Scanner inputScanner, String prompt,
			Function<Integer, Boolean> checkValidity,
			String wrongWarn) {
		Integer answer = getUserInputInt(inputScanner, prompt, checkValidity, wrongWarn);
		if (answer != null) {
			return OptionalInt.of(answer);
		} else {
			return OptionalInt.empty();
		}
	}

	public static Integer getUserInputInt(
			Scanner inputScanner, String prompt,
			Function<Integer, Boolean> checkIfValid,
			String warnOutOfRange) {

		boolean wrongType = false;
		boolean outOfRange = false;

		while (true) {
			if (wrongType) {
				wrongType = false;
				printError(ERR_NOT_INT);
			}
			if (outOfRange) {
				outOfRange = false;
				if (warnOutOfRange != null)
					printError(warnOutOfRange);
			}

			System.out.print(prompt);
			var rawInp = inputScanner.nextLine();
			if (rawInp.isBlank()) {
				return null;
			}
			var value = tryParseInt(rawInp);
			if (value != null) {
				if (checkIfValid == null || checkIfValid.apply(value)) {
					return value;
				}
				outOfRange = true;
			} else {
				wrongType = true;
			}
		}
	}

	// aux

	private static void printError(String errorMessage) {
		System.err.println(errorMessage);
	}

	private static Integer tryParseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
