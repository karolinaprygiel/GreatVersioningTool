package uj.java.pwj2020;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Version {
    private static final String dirPath = "./.gvt/version";
    private final int version;
    private final ArrayList<String> files;
    private final String message;
    private final String versionDirPath;

    public Version(int version, String message) {
        this(version, message, new ArrayList<String>());
    }

    public Version(int version, String message, ArrayList<String> listFile) {
        this.version = version;
        this.files = listFile;
        this.message = message;
        this.versionDirPath = dirPath + version;
    }

    public String getMessageFirstLine() {
        if (message.contains("\n")) {
            return message.substring(0, message.indexOf("\n"));
        } else {
            return message;
        }
    }

    public String getMessage() {
        return message;
    }

    public static Version loadVersion(int number) {
        File info = new File(dirPath + number + "/info.txt");
        Scanner myReader = null;
        try {
            myReader = new Scanner(info);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int version = Integer.parseInt(myReader.nextLine());
        String files = myReader.nextLine();
        ArrayList<String> fileArray = stringFilesToArray(files);
        String message = myReader.nextLine();
        if (myReader.hasNext()) {
            message += "\n" + myReader.nextLine();
        }
        return new Version(version, message, fileArray);
    }

    private static ArrayList<String> stringFilesToArray(String files) {
        ArrayList<String> fileArray = new ArrayList<>();
        if (!files.equals("[]")) {
            String[] stringFiles = files.substring(1, files.length() - 1).split(", ");
            Collections.addAll(fileArray, stringFiles);
        }
        return fileArray;
    }

    public boolean checkFileAdded(String fileName) {
        return files.contains(fileName);
    }

    public void createVersionDirectory() {
        File file = new File(versionDirPath);
        file.mkdir();
        createVersionInfoFile();
    }


    public Version CreateNextVersion(String message) {
        ArrayList<String> oldFiles = new ArrayList<>(files);
        Version newVersion = new Version(version + 1, message, oldFiles);
        newVersion.createVersionDirectory();
        copyFilesTo(dirPath + (version + 1) + "/");
        return newVersion;
    }

    public void addFile(String fileName) {
        addToFileList(fileName);
        copyFromRepo(fileName, version);
        createVersionInfoFile();
    }

    public void commitFile(String fileName) {
        copyFromRepo(fileName, version);
        createVersionInfoFile();
    }

    public void detachFile(String fileName) {
        deleteFromFileList(fileName);
        deleteFile(fileName, version);
        createVersionInfoFile();
    }


    public void copyFilesTo(String destPath) {
        for (String file : files) {
            Path src = Paths.get(versionDirPath + "/" + file);
            Path dest = Paths.get(destPath + file);

            try {
                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteFile(String fileName, int version) {
        String path = "./.gvt/version" + version + "/" + fileName;
        File file = new File(path);
        file.delete();
    }

    private void copyFromRepo(String fileName, int version) {
        Path src = Paths.get("./" + fileName);
        Path dest = Paths.get("./.gvt/version" + version + "/" + fileName);
        try {
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addToFileList(String fileName) {
        files.add(fileName);
    }

    private void deleteFromFileList(String fileName) {
        files.remove(fileName);
    }

    private void createVersionInfoFile() {
        File infoFile = new File(versionDirPath + "/info.txt");
        try {
            infoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter(versionDirPath + "/info.txt");
            myWriter.write(version + "\n");
            myWriter.write(files.toString() + "\n");
            myWriter.write(message);

            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
