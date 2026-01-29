package shinchan.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import shinchan.exception.ShinchanException;

/**
 * Tests for {@link Parser}.
 */
public class ParserTest {

    @Test
    public void getCommandWord_validInput_returnsLowercaseCommand() {
        assertEquals("todo", Parser.getCommandWord("todo read book"));
    }

    @Test
    public void getRemainder_withArguments_returnsRemainder() {
        assertEquals("read book", Parser.getRemainder("todo read book"));
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws ShinchanException {
        assertEquals(1, Parser.parseTaskIndex("delete 2", "error"));
    }

    @Test
    public void parseTaskIndex_invalidNumber_throwsException() {
        assertThrows(
                ShinchanException.class,
                () -> Parser.parseTaskIndex("delete two", "error")
        );
    }

    @Test
    public void parseDate_validDate_returnsLocalDate() throws ShinchanException {
        LocalDate date = Parser.parseDate("2026-01-10", "error");
        assertEquals(LocalDate.of(2026, 1, 10), date);
    }

    @Test
    public void parseDate_invalidDate_throwsException() {
        assertThrows(
                ShinchanException.class,
                () -> Parser.parseDate("10-01-2026", "error")
        );
    }

    @Test
    public void parseDateTime_validDateTime_returnsLocalDateTime() throws ShinchanException {
        LocalDateTime dateTime =
                Parser.parseDateTime("2026-01-10 1800", "error");

        assertEquals(LocalDateTime.of(2026, 1, 10, 18, 0), dateTime);
    }
}
