package shinchan.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Events}.
 */
public class EventsTest {

    @Test
    public void occursOn_insideRange_returnsTrue() {
        Events e = new Events(
                "camp",
                LocalDateTime.of(2026, 1, 10, 9, 0),
                LocalDateTime.of(2026, 1, 12, 18, 0)
        );

        assertTrue(e.occursOn(LocalDate.of(2026, 1, 11)));
    }

    @Test
    public void occursOn_outsideRange_returnsFalse() {
        Events e = new Events(
                "camp",
                LocalDateTime.of(2026, 1, 10, 9, 0),
                LocalDateTime.of(2026, 1, 12, 18, 0)
        );

        assertFalse(e.occursOn(LocalDate.of(2026, 1, 13)));
    }
}
