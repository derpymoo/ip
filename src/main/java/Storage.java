import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
     * @throws ShinchanException If the file cannot be read
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
                Task task = parseTask(line);
                tasks.add(task);
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

        switch (parts[0]) {
        case "T":
            return buildTodo(parts);
        case "D":
            return buildDeadline(parts);
        case "E":
            return buildEvent(parts);
        default:
            throw new ShinchanException("Corrupted data file.");
        }
    }

    private Task buildTodo(String[] parts) {
        Task task = new Todos(parts[2]);
        restoreStatus(task, parts[1]);
        return task;
    }

    private Task buildDeadline(String[] parts) {
        Task task = new Deadlines(parts[2], parts[3]);
        restoreStatus(task, parts[1]);
        return task;
    }

    private Task buildEvent(String[] parts) {
        Task task = new Events(parts[2], parts[3], parts[4]);
        restoreStatus(task, parts[1]);
        return task;
    }

    private void restoreStatus(Task task, String status) {
        if ("1".equals(status)) {
            task.markAsDone();
        }
    }

    private String formatTask(Task task) {
        if (task instanceof Todos) {
            return formatTodo(task);
        }
        if (task instanceof Deadlines) {
            return formatDeadline((Deadlines) task);
        }
        return formatEvent((Events) task);
    }

    private String formatTodo(Task task) {
        return "T | " + getStatus(task) + " | " + extractDescription(task);
    }

    private String formatDeadline(Deadlines task) {
        return "D | " + getStatus(task) + " | "
                + extractDescription(task) + " | " + extractDeadline(task);
    }

    private String formatEvent(Events task) {
        return "E | " + getStatus(task) + " | "
                + extractDescription(task) + " | "
                + extractStart(task) + " | " + extractEnd(task);
    }

    private String getStatus(Task task) {
        return task.getStatusIcon().equals("X") ? "1" : "0";
    }

    private String extractDescription(Task task) {
        return task.toString()
                .replaceFirst("^\\[[TDES]\\]\\[[ X]\\] ", "")
                .replaceAll(" \\(.*\\)$", "");
    }

    private String extractDeadline(Deadlines task) {
        return task.toString().replaceAll("^.*by: ", "").replace(")", "");
    }

    private String extractStart(Events task) {
        return task.toString().replaceAll("^.*from: ", "").replaceAll(" to:.*", "");
    }

    private String extractEnd(Events task) {
        return task.toString().replaceAll("^.*to: ", "").replace(")", "");
    }
}
