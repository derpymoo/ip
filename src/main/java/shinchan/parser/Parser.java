package shinchan.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import shinchan.exception.ShinchanException;

/**
 * Parses user input into command words, arguments, and typed values.
 */
public class Parser {
    private static final int SPLIT_LIMIT_TWO = 2;
    private static final int USER_INDEX_OFFSET = 1;

    private static final DateTimeFormatter DATE_TIME_INPUT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Extracts the command word from a full input line.
     *
     * @param input The full user input line.
     * @return The command word (lowercased).
     */
    public static String getCommandWord(String input) {
        String[] parts = input.split(" ", SPLIT_LIMIT_TWO);
        return parts[0].toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the part of the input after the first word.
     *
     * @param input Full user input.
     * @return Remainder after the command word (may be empty).
     */
    public static String getRemainder(String input) {
        String trimmed = input.trim();
        int firstSpace = trimmed.indexOf(' ');
        return firstSpace == -1 ? "" : trimmed.substring(firstSpace + 1).trim();
    }

    /**
     * Parses a 1-based task number from user input into a 0-based index.
     *
     * @param input The full user input line.
     * @param invalidMessage The message to use if parsing fails.
     * @return The 0-based task index.
     * @throws ShinchanException If the index is not a valid integer.
     */
    public static int parseTaskIndex(String input, String invalidMessage) throws ShinchanException {
        String remainder = getRemainder(input);
        try {
            return Integer.parseInt(remainder) - USER_INDEX_OFFSET;
        } catch (NumberFormatException e) {
            throw new ShinchanException(invalidMessage);
        }
    }

    /**
     * Parses a date-time value in yyyy-MM-dd HHmm format.
     *
     * @param value The date-time string.
     * @param badMessage The message to use if parsing fails.
     * @return The parsed LocalDateTime.
     * @throws ShinchanException If parsing fails.
     */
    public static LocalDateTime parseDateTime(String value, String badMessage) throws ShinchanException {
        try {
            return LocalDateTime.parse(value, DATE_TIME_INPUT);
        } catch (DateTimeParseException e) {
            throw new ShinchanException(badMessage);
        }
    }

    /**
     * Parses a date value in yyyy-MM-dd format.
     *
     * @param value The date string.
     * @param badMessage The message to use if parsing fails.
     * @return The parsed LocalDate.
     * @throws ShinchanException If parsing fails.
     */
    public static LocalDate parseDate(String value, String badMessage) throws ShinchanException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new ShinchanException(badMessage);
        }
    }
}
