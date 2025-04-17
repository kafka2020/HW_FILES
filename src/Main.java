import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

        // Задние №2
        GameProgress gp1 = new GameProgress(100, 80, 5, 145.0);
        GameProgress gp2 = new GameProgress(40, 60, 12, 905.54);
        GameProgress gp3 = new GameProgress(90, 15, 1, 89.01);
        saveGame(MAIN_PATH + "/savegames/save1.dat", gp1);
        saveGame(MAIN_PATH + "/savegames/save2.dat", gp2);
        saveGame(MAIN_PATH + "/savegames/save3.dat", gp3);

        List<String> savePathList = Arrays.asList(
                MAIN_PATH + "/savegames/save1.dat",
                MAIN_PATH + "/savegames/save2.dat",
                MAIN_PATH + "/savegames/save3.dat"
        );
        zipFiles(MAIN_PATH + "/savegames/zip.zip", savePathList);

        for (String savePath : savePathList) {
            File file = new File(savePath);
            file.delete();
        }

        // Задание №3
        openZip(MAIN_PATH + "/savegames/zip.zip", MAIN_PATH + "/savegames/");
        System.out.println(openProgress(MAIN_PATH + "/savegames/save2.dat"));
    }

    private static GameProgress openProgress(String savePath) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(savePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }

    private static void openZip(String zipFilePath, String unzipDirPath) {
        try (FileInputStream fin = new FileInputStream(zipFilePath);
             ZipInputStream zin = new ZipInputStream(fin)) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                try (FileOutputStream fout = new FileOutputStream(unzipDirPath + name)) {
                    for (int c = zin.read(); c != -1 ; c = zin.read()) {
                        fout.write(c);
                    }
                    fout.flush();
                }
                zin.closeEntry();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());;
        }
    }

    private static void zipFiles(String zipFilePath, List<String> filePathList) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zout = new ZipOutputStream(fos)) {
            for (String filePath : filePathList) {
                File file = new File(filePath);
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry entry = new ZipEntry(file.getName());
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void saveGame(String filePath, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
