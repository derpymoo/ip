import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving tasks to disk.
 */
public class Storage {

    private final String filePath;

    /**
     * Creates a Storage object using the given file path.
     *
     * @param filePath Path to the data file
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from disk.
     *
     * @return List of tasks loaded from file
     * @throws ShinchanException If the file cannot be read or data is corrupted
     */
    public List<Task> load() throws ShinchanException {
        List<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return tasks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(parseTask(line));
            }
        } catch (IOException e) {
            throw new ShinchanException("Error loading data from file.");
        }

        return tasks;
    }

    /**
     * Saves tasks to disk.
     *
     * @param tasks List of tasks to save
     * @throws ShinchanException If the file cannot be written
     */
    public void save(List<Task> tasks) throws ShinchanException {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : tasks) {
                writer.write(formatTask(task));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ShinchanException("Error saving data to file.");
        }
    }

    private Task parseTask(String line) throws ShinchanException {
        String[] parts = line.split(" \\| ");

        if (parts.length < 3) {
            throw new ShinchanException("Corrupted data file.");
        }

        Task task;

        switch (parts[0]) {
        case "T":
            task = new Todos(parts[2]);
            break;
        case "D":
            if (parts.length < 4) {
                throw new ShinchanException("Corrupted data file.");
            }
            task = new Deadlines(parts[2], LocalDateTime.parse(parts[3]));
            break;
        case "E":
            if (parts.length < 5) {
                throw new ShinchanException("Corrupted data file.");
            }
            task = new Events(parts[2], LocalDateTime.parse(parts[3]), LocalDateTime.parse(parts[4]));
            break;
        default:
            throw new ShinchanException("Corrupted data file.");
        }

        restoreStatus(task, parts[1]);
        return task;
    }

    private void restoreStatus(Task task, String status) {
        if ("1".equals(status)) {
            task.markAsDone();
        }
    }

    private String formatTask(Task task) throws ShinchanException {
        if (task instanceof Todos) {
            return "T | " + getStatus(task) + " | " + task.getDescription();
        }

        if (task instanceof Deadlines) {
            Deadlines deadlines = (Deadlines) task;
            return "D | " + getStatus(task) + " | "
                    + task.getDescription() + " | " + deadlines.getDueDateTime();
        }

        if (task instanceof Events) {
            Events events = (Events) task;
            return "E | " + getStatus(task) + " | "
                    + task.getDescription() + " | "
                    + events.getStartDateTime() + " | " + events.getEndDateTime();
        }

        throw new ShinchanException("Unknown task type.");
    }

    private String getStatus(Task task) {
        return task.isDone() ? "1" : "0";
    }
}
