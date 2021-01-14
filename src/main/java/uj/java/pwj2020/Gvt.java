package uj.java.pwj2020;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Gvt {
    private final String curDirpath = "./.gvt";

    public static void main(String... args) {

        Gvt gvt = new Gvt();

        if (args.length == 0) {
            System.out.println("Please specify command.");
            System.exit(1);
        }

        switch (args[0]) {
            case "init":
                gvt.initCommand();
                break;
            case "add":
                gvt.addCommand(args);
                break;
            case "detach":
                gvt.detachCommand(args);
                break;
            case "checkout":
                gvt.checkoutCommand(args);
                break;
            case "commit":
                gvt.commitCommand(args);
                break;
            case "history":
                gvt.historyCommand(args);
                break;
            case "version":
                gvt.versionCommand(args);
                break;
            default:
                System.out.print("Unknown command " + args[0] + "." + "\n");
                System.exit(1);
        }
    }

    private void initCommand() {
        if (isDirectoryInitialized()) {
            System.out.print("Current directory is already initialized." + "\n");
            System.exit(10);
        } else {
            initializeDirectory();
            System.out.print("Current directory initialized successfully." + "\n");
        }
    }

    private void initializeDirectory() {
        File file = new File(curDirpath);
        file.mkdir();
        Version newVersion = new Version(0, "GVT initialized.");
        newVersion.createVersionDirectory();
        initializeHead();
        updateHead(0);
    }

    private void initializeHead() {
        File head = new File(curDirpath + "/HEAD.txt");
        try {
            head.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateHead(int s) {
        try {
            FileWriter myWriter = new FileWriter(curDirpath + "/HEAD.txt");
            myWriter.write(String.valueOf(s));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCommand(String[] args) {
        if (!isDirectoryInitialized()) {
            System.out.print("Current directory is not initialized. Please use \"init\" command to initialize." + "\n");
            System.exit(-2);
        } else if (args.length == 1 || args[1].isEmpty()) {
            System.out.println("Please specify file to add.");
            System.exit(20);
        }

        String fileName = args[1];
        try {
            if (checkFileExists(fileName)) {
                System.out.print("File " + fileName + " not found." + "\n");
                System.exit(22);
            }
            int versionNum = getCurrVersion();
            Version currVersion = Version.loadVersion(versionNum);
            if (currVersion.checkFileAdded(fileName)) {
                System.out.print("File " + fileName + " already added.");

            } else {
                int newVersionNum = versionNum + 1;
                String message = computeMessage("Added", fileName, args);
                Version newVersion = currVersion.CreateNextVersion(message);
                newVersion.addFile(fileName);
                updateHead(newVersionNum);
                System.out.print("File " + fileName + " added successfully." + "\n");
            }
        } catch (Exception e) {
            System.out.print("File " + fileName + " cannot be added, see ERR for details." + "\n");
            e.printStackTrace();
            System.exit(22);
        }
    }

    private void commitCommand(String[] args) {
        if (!isDirectoryInitialized()) {
            System.out.print("Current directory is not initialized. Please use \"init\" command to initialize." + "\n");
            System.exit(-2);
        }
        if (args.length == 1 || args[1].isEmpty()) {
            System.out.println("Please specify file to commit.");
            System.exit(50);
        }
        String fileName = args[1];

        try {
            if (checkFileExists(fileName)) {
                System.out.print("File " + fileName + " does not exist." + "\n");
                System.exit(51);
            }
            int versionNum = getCurrVersion();
            Version currVersion = Version.loadVersion(versionNum);

            if (!currVersion.checkFileAdded(fileName)) {
                System.out.print("File " + fileName + "  is not added to gvt." + "\n");
            } else {
                String message = computeMessage("Committed", fileName, args);
                Version newVersion = currVersion.CreateNextVersion(message);
                newVersion.commitFile(fileName);
                updateHead(versionNum + 1);
                System.out.print("File " + fileName + " committed successfully." + "\n");
            }
        } catch (Exception e) {
            System.out.print("File " + fileName + " cannot be committed, see ERR for details." + "\n");
            e.printStackTrace();
            System.exit(-52);
        }
    }

    private void detachCommand(String[] args) {
        if (!isDirectoryInitialized()) {
            System.out.print("Current directory is not initialized. Please use \"init\" command to initialize." + "\n");
            System.exit(-2);
        }
        if (args.length < 2) {
            System.out.print("Please specify file to detach." + "\n");
            System.exit(30);
        }
        String fileName = args[1];
        try {
            int currVersionNum = getCurrVersion();
            Version currVersion = Version.loadVersion(currVersionNum);
            if (!currVersion.checkFileAdded(fileName)) {
                System.out.print("File " + fileName + " is not added to gvt." + "\n");

            } else {
                int newVersionNum = currVersionNum + 1;
                String message = computeMessage("Detached", fileName, args);

                Version newVersion = currVersion.CreateNextVersion(message);
                newVersion.detachFile(fileName);
                updateHead(newVersionNum);
                System.out.print("File " + fileName + " detached successfully." + "\n");
            }
        } catch (Exception e) {
            System.out.print("File " + fileName + "cannot be detached, see ERR for details" + "\n");
            System.exit(31);
        }
    }

    private String computeMessage(String action, String fileName, String[] args) {
        String message = action + " file: " + fileName;
        if (args.length > 3) {
            String additionalMessage = args[3];
            message += "\n" + additionalMessage;
        }
        return message;
    }

    private void versionCommand(String[] args) {
        if (!isDirectoryInitialized()) {
            System.out.print("Current directory is not initialized. Please use \"init\" command to initialize." + "\n");
            System.exit(-2);
        }
        int currVersionNum = getCurrVersion();
        if (args.length < 2) {
            showVersion(currVersionNum);
        } else if (isNotNumeric(args[1]) || Integer.parseInt(args[1]) > currVersionNum) {
            System.out.print("Invalid version number: " + args[1] + ".\n");
            System.exit(60);
        } else {
            int version = Integer.parseInt(args[1]);
            showVersion(version);
        }
    }

    private void showVersion(int versionNum) {
        Version version = Version.loadVersion(versionNum);
        String message = version.getMessage();
        System.out.print("Version: " + versionNum + "\n" + message);
    }

    private void historyCommand(String[] args) {
        if (!isDirectoryInitialized()) {
            System.out.print("Current directory is not initialized. Please use \"init\" command to initialize." + "\n");
            System.exit(-2);
        }
        int currVersionNum = getCurrVersion();

        if (args.length > 1 && args[1].equals("-last")) {
            int number = Integer.parseInt(args[2]);
            int start = currVersionNum - number + 1;
            printVersions(start, currVersionNum);
        } else {
            printVersions(0, currVersionNum);
        }
    }

    private void printVersions(int start, int end) {
        for (int i = start; i <= end; i++) {
            Version version = Version.loadVersion(i);
            System.out.print(i + ": " + version.getMessageFirstLine() + "\n");
        }
    }

    private void checkoutCommand(String[] args) {
        if (!isDirectoryInitialized()) {
            System.out.print("Current directory is not initialized. Please use \"init\" command to initialize." + "\n");
            System.exit(-2);
        }
        int currVersionNum = getCurrVersion();
        if (isNotNumeric(args[1]) || Integer.parseInt(args[1]) > currVersionNum) {
            System.out.print("Invalid version number: " + args[1] + ".\n");
            System.exit(40);
        }
        int versionNum = Integer.parseInt(args[1]);
        Version version = Version.loadVersion(versionNum);
        version.copyFilesTo("./");
        System.out.print("Version " + versionNum + " checked out successfully." + "\n");
    }

    private boolean checkFileExists(String fileName) {
        return !Files.exists(Path.of("./" + fileName));
    }

    private int getCurrVersion() {
        File head = new File(curDirpath + "/HEAD.txt");
        Scanner myReader = null;
        try {
            myReader = new Scanner(head);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int version = myReader.nextInt();
        return version;
    }
    private boolean isNotNumeric(String str) {
        try {
            Integer.parseInt(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isDirectoryInitialized() {
        return Files.exists(Path.of(curDirpath));
    }
}

