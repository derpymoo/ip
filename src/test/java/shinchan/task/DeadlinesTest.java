package shinchan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Deadlines}.
 */
public class DeadlinesTest {

    @Test
    public void getDueDate_returnsCorrectDate() {
        LocalDateTime dt = LocalDateTime.of(2026, 1, 15, 18, 0);
        Deadlines d = new Deadlines("submit report", dt);

        assertEquals(LocalDate.of(2026, 1, 15), d.getDueDate());
    }
}
