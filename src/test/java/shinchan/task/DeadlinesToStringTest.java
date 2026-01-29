package shinchan.task;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Deadlines}.
 */
public class DeadlinesToStringTest {

    @Test
    public void toString_containsFormattedDate() {
        Deadlines d = new Deadlines(
                "submit",
                LocalDateTime.of(2026, 1, 15, 18, 0)
        );

        assertTrue(d.toString().contains("Jan"));
        assertTrue(d.toString().contains("18:00"));
    }
}

