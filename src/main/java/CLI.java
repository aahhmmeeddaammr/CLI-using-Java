import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public  class CLI {
    private static Path currentDirectory = Paths.get(System.getProperty("user.dir"));

    public static void run() {
        System.out.println("Welcome to CLI. Enter 'help' to see commands.");
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print(currentDirectory + ">");
            command = scanner.nextLine().trim();
            if (command.isEmpty()) continue;
            try {
                handleCommand(command);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void handleCommand(String command) throws IOException {
        if (command.contains(">")) {
            handleRedirection(command);
        } else if (command.contains("|")) {
            handlePipe(command);
        } else {
            executeCommand(command.split("\\s+"));
        }
    }

    private static void handleRedirection(String command) throws IOException {
        String[] parts;
        boolean append = false;

        if (command.contains(">>")) {
            parts = command.split(">>", 2);
            append = true;
        } else {
            parts = command.split(">", 2);
        }

        if (parts.length < 2) {
            System.out.println("Invalid redirection syntax.");
            return;
        }

        String cmdPart = parts[0].trim();
        String filePart = parts[1].trim();

        Path filePath = currentDirectory.resolve(filePart);
        List<String> output = executeCommandAndCaptureOutput(cmdPart.split("\\s+"));

        if (append) {
            Files.write(filePath, output, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } else {
            Files.write(filePath, output, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private static void handlePipe(String command) throws IOException {
        String[] commands = command.split("\\|");

        List<String> input = executeCommandAndCaptureOutput(commands[0].trim().split("\\s+"));
        for (int i = 1; i < commands.length; i++) {
            input = executeCommandWithInput(commands[i].trim().split("\\s+"), input);
        }

        input.forEach(System.out::println); // Print final output
    }

    private static List<String> executeCommandAndCaptureOutput(String[] args) throws IOException {
        List<String> output = new ArrayList<>();

        if (args[0].equals("pwd")) {
            output.add(currentDirectory.toString());
        } else if (args[0].equals("ls")) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory)) {
                for (Path entry : stream) {
                    output.add(entry.getFileName().toString());
                }
            }
        } else if (args[0].equals("cat")) {
            if (args.length < 2) {
                System.out.println("Usage: cat <file>");
            } else {
                Path filePath = currentDirectory.resolve(args[1]);
                if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                    Files.lines(filePath).forEach(output::add);
                } else {
                    System.out.println("File does not exist or is a directory: " + args[1]);
                }
            }
        } else {
            throw new UnsupportedOperationException("Unsupported command: " + args[0]);
        }
        return output;
    }

    private static List<String> executeCommandWithInput(String[] args, List<String> input) {
        List<String> output = new ArrayList<>();

        if (args[0].equals("cat")) {
            output.addAll(input);
        } else {
            throw new UnsupportedOperationException("Unsupported command: " + args[0]);
        }
        return output;
    }

    private static void executeCommand(String[] args) throws IOException {
        if (args[0].equals("pwd")) {
            Commands.pwd();
        } else if (args[0].equals("cd")) {
            Commands.cd(args);
        } else if (args[0].equals("ls")) {
            Commands.ls(args);
        } else if (args[0].equals("mkdir")) {
            Commands.mkdir(args);
        } else if (args[0].equals("rmdir")) {
            Commands.rmdir(args);
        } else if (args[0].equals("touch")) {
            Commands.touch(args);
        } else if (args[0].equals("mv")) {
            Commands.mv(args);
        } else if (args[0].equals("rm")) {
            Commands.rm(args);
        } else if (args[0].equals("cat")) {
            Commands.cat(args);
        } else if (args[0].equals("exit")) {
            Commands.exit();
        } else if (args[0].equals("help")) {
            Commands.help();
        } else {
            System.out.println("Unknown command. Enter 'help' to see commands.");
        }
    }
}
