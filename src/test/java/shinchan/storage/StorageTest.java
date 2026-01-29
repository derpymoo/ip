package shinchan.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shinchan.exception.ShinchanException;
import shinchan.task.Task;
import shinchan.task.Todos;

/**
 * Tests for {@link Storage}.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void saveAndLoad_roundTripPreservesTasks() throws ShinchanException {
        Path file = tempDir.resolve("test.txt");
        Storage storage = new Storage(file.toString());

        Task task = new Todos("read book");
        storage.save(List.of(task));

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(task.toString(), loaded.get(0).toString());
    }
}
