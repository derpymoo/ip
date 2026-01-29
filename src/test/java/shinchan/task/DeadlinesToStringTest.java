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

        String text = d.toString();
        assertTrue(text.contains("Jan 15 2026"));
        assertTrue(text.contains("6pm"));
    }
}
