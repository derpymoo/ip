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
    public void occursOn_dateWithinRange_returnsTrue() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 12, 18, 0);
        Events event = new Events("camp", start, end);

        assertTrue(event.occursOn(LocalDate.of(2026, 1, 11)));
    }

    @Test
    public void occursOn_dateOutsideRange_returnsFalse() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 12, 18, 0);
        Events event = new Events("camp", start, end);

        assertFalse(event.occursOn(LocalDate.of(2026, 1, 13)));
    }

    @Test
    public void occursOn_onBoundaryDates_returnsTrue() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 12, 18, 0);
        Events event = new Events("camp", start, end);

        assertTrue(event.occursOn(LocalDate.of(2026, 1, 10)));
        assertTrue(event.occursOn(LocalDate.of(2026, 1, 12)));
    }
}
