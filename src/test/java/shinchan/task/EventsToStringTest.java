package shinchan.task;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Events}.
 */
public class EventsToStringTest {

    @Test
    public void toString_containsStartAndEnd() {
        Events e = new Events(
                "camp",
                LocalDateTime.of(2026, 1, 10, 9, 0),
                LocalDateTime.of(2026, 1, 12, 18, 0)
        );

        String text = e.toString();
        assertTrue(text.contains("from"));
        assertTrue(text.contains("to"));
    }
}
