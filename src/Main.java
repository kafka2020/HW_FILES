import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;
import java.io.File;

public class Main {
    private static final String MAIN_PATH = "/Users/citizen/IdeaProjects/HW_FILES/Games";
    private static StringBuilder log = new StringBuilder();

    public static void main(String[] args) {
        Queue<String> resourcePathQueue = new LinkedList<>();
        resourcePathQueue.add("src");
        resourcePathQueue.add("res");
        resourcePathQueue.add("savegames");
        resourcePathQueue.add("temp");
        resourcePathQueue.add("src/main");
        resourcePathQueue.add("src/test");
        resourcePathQueue.add("src/main/Main.java");
        resourcePathQueue.add("src/main/Utils.java");
        resourcePathQueue.add("res/drawables");
        resourcePathQueue.add("res/vectors");
        resourcePathQueue.add("res/icons");
        resourcePathQueue.add("temp/temp.txt");

        while (!resourcePathQueue.isEmpty()) {
            String path = String.format("%s/%s", MAIN_PATH, resourcePathQueue.poll());
            File currentResource = new File(path);
            if (path.contains(".")) {
                createFile(currentResource);
            } else {
                createDirectory(currentResource);
            }
        }

        // Запись лога в файл temp.txt
        writeLogToFile(MAIN_PATH + "/temp/temp.txt");
    }

    private static void createDirectory(File directory) {
        if (directory.mkdir()) {
            addLogEntry("INFO", "Directory created: " + directory.getPath());
        } else {
            addLogEntry("ERROR", "Directory not created: " + directory.getPath());
        }
    }

    private static void createFile(File file) {
        try {
            if (file.createNewFile()) {
                addLogEntry("INFO", "File created: " + file.getPath());
            } else {
                addLogEntry("WARNING", "File already exists: " + file.getPath());
            }
        } catch (IOException e) {
            addLogEntry("ERROR", "File not created: " + file.getPath() +
                    " - Reason: " + e.getMessage());
        }
    }

    private static void addLogEntry(String level, String message) {
        String logEntry = String.format("%s %s %s%n",
                level,
                LocalDateTime.now(),
                message);
        log.append(logEntry);
    }

    private static void writeLogToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(log.toString());
            System.out.println("Log written to: " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to write log file: " + e.getMessage());
        }
    }

}
