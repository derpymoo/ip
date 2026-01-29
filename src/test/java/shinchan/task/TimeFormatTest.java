package shinchan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TimeFormat}.
 */
public class TimeFormatTest {

    @Test
    public void formatAmPm_onHour_formatsWithoutMinutes() {
        LocalDateTime dt = LocalDateTime.of(2026, 1, 10, 20, 0);
        assertEquals("8pm", TimeFormat.formatAmPm(dt));
    }

    @Test
    public void formatAmPm_withMinutes_formatsWithMinutes() {
        LocalDateTime dt = LocalDateTime.of(2026, 1, 10, 20, 30);
        assertEquals("8:30pm", TimeFormat.formatAmPm(dt));
    }
}
