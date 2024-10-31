import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Commands {
    public static Path currentDirectory = Paths.get(System.getProperty("user.dir"));

    public static void pwd() {
        System.out.println(currentDirectory);
    }

    public static void cd(String[] args) {
        if (args.length < 2) {
            System.out.println("command: cd <directory>");
            return;
        }
        Path newPath = currentDirectory.resolve(args[1]).normalize();
        if (Files.isDirectory(newPath)) {
            currentDirectory = newPath;
        } else {
            System.out.println("Directory does not exist: " + args[1]);
        }
    }

    public static void ls(String[] args) throws IOException {
        List<String> files = new ArrayList<>();
        boolean recursive = false;
        boolean showAll = false;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory)) {
            for (Path entry : stream) {
                files.add(entry.getFileName().toString());
            }
        }

        for (String arg : args) {
            if (arg.equals("-r")) {
                recursive = true;
            } else if (arg.equals("-a")) {
                showAll = true;
            }
        }

        if (recursive) {
            Collections.reverse(files);
        }

        for (String file : files) {
            if (showAll || !file.startsWith(".")) {
                System.out.println(file);
            }
        }

    }

    public static void mkdir(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("command: mkdir <directory>");
            return;
        }
        for (int i = 1; i < args.length; i++) {
            Path newPath = currentDirectory.resolve(args[i]);
            Files.createDirectories(newPath);
            System.out.println("Directory created: " + newPath);
        }
    }

    public static void rmdir(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("command: rmdir <directory>");
            return;
        }
        for (int i = 1; i < args.length; i++) {
            Path dirPath = currentDirectory.resolve(args[i]);
            if (Files.isDirectory(dirPath)) {
                Files.delete(dirPath);
                System.out.println("Directory removed: " + dirPath);
            } else {
                System.out.println("Not a directory: " + args[i]);
            }
        }
    }

    public static void touch(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("command: touch <file>");
            return;
        }
        for (int i = 1; i < args.length; i++) {
            Path filePath = currentDirectory.resolve(args[i]);
            try {
                if (Files.notExists(filePath)) {
                    Files.createFile(filePath);
                    System.out.println("File created: " + filePath);
                } else {
                    System.out.println("File already exists: " + filePath);
                }
            } catch (IOException e) {
                System.out.println("Error creating file: " + filePath + " - " + e.getMessage());
            }
        }
    }

    public static void mv(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("command: mv <source> <destination>");
            return;
        }

        Path sourcePath = currentDirectory.resolve(args[1]);
        Path destPath = currentDirectory.resolve(args[2]);

        if (!Files.exists(sourcePath)) {
            System.out.println("Error: Source file does not exist: " + sourcePath);
            return;
        }

        if (Files.isDirectory(destPath)) {
            Path newDestPath = destPath.resolve(sourcePath.getFileName());

            try {
                Files.move(sourcePath, newDestPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved " + sourcePath + " to " + newDestPath);
            } catch (IOException e) {
                System.out.println("Error: Unable to move file. " + e.getMessage());
            }
        } else {
            try {
                Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved " + sourcePath + " to " + destPath);
            } catch (IOException e) {
                System.out.println("Error: Unable to move file. " + e.getMessage());
            }
        }
    }

    public static void rm(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("command: rm <file>");
            return;
        }
        Path filePath = currentDirectory.resolve(args[1]);
        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            Files.delete(filePath);
            System.out.println("File removed: " + filePath);
        } else {
            System.out.println("File does not exist or is a directory: " + args[1]);
        }
    }

    public static void cat(String[] args) throws IOException {
        if (args.length < 2) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter text (type 'EOF' to end):");
            String line;
            while (!(line = scanner.nextLine()).equals("EOF")) {
                System.out.println(line);
            }
        } else {
            Path filePath = currentDirectory.resolve(args[1]);
            Files.lines(filePath).forEach(System.out::println);
        }
    }

    public static void grep(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("command: grep <term> <file>");
            return;
        }
        String term = args[1];
        Path filePath = currentDirectory.resolve(args[2]);
        Files.lines(filePath)
                .filter(line -> line.contains(term))
                .forEach(System.out::println);
    }

    public static void uniq(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("command: uniq <file>");
            return;
        }
        Path filePath = currentDirectory.resolve(args[1]);
        Files.lines(filePath).distinct().forEach(System.out::println);
    }

    public static void sort(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("command: sort <file>");
            return;
        }
        Path filePath = currentDirectory.resolve(args[1]);
        Files.lines(filePath).sorted().forEach(System.out::println);
    }

    public static void echo(String[] args) {
        if (args.length > 1) {
            System.out.println(String.join(" ", args).substring(5));
        }
    }

    public static void nano(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("command: nano <file>");
            return;
        }
        Path filePath = currentDirectory.resolve(args[1]);
        System.out.println("Opening " + filePath + " in nano mode. Type 'EOF' to finish editing.");
        List<String> lines = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String line;
        while (!(line = scanner.nextLine()).equals("EOF")) {
            lines.add(line);
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
        System.out.println("Saved file " + filePath);
    }

    public static void exit() {
        System.out.println("Exiting CLI...");
        System.exit(0);
    }

    public static void help() {
        System.out.println("""
                Available commands:
                pwd        - Show current directory
                cd <dir>   - Change directory
                ls         - List files in directory
                mkdir <dir> - Create directory
                rmdir <dir> - Remove directory
                touch <file> - Create empty file
                mv <src> <dest> - Move file or directory
                rm <file>   - Remove file
                cat <file>  - Display file contents
                grep <term> <file> - Search term in file
                uniq <file> - Display unique lines
                sort <file> - Sort file lines
                echo <text> - Display text
                nano <file> - Simple file editor
                """);
    }
}
