import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CLI {


    public static void run() {
        System.out.println("Welcome to CLI. Type 'help' to see available commands.");
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print(Commands.currentDirectory + ">");
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
            executeCommand(command.split(" "));
        }
    }

    private static void handleRedirection(String command) throws IOException {
        String[] parts;
        boolean append = false;

        if (command.contains(">>")) {
            parts = command.split(">>");
            append = true;
        } else {
            parts = command.split(">");
        }

        String cmdPart = parts[0].trim();
        String filePart = parts[1].trim();

        Path filePath = Commands.currentDirectory.resolve(filePart);
        List<String> output = executeCommandAndCaptureOutput(cmdPart.split(" "));

        if (append) {
            Files.write(filePath, output, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } else {
            Files.write(filePath, output, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private static void handlePipe(String command) throws IOException {
        String[] commands = command.split("\\|");
        for (int i = 1; i < commands.length; i++) {
            System.out.println("Pipe: " + commands[i].trim());
        }
        List<String> input = executeCommandAndCaptureOutput(commands[0].trim().split(" "));
        for (int i = 1; i < commands.length; i++) {
            input = executeCommandWithInput(commands[i].trim().split(" "), input);
        }

        input.forEach(System.out::println);  // Print final output
    }

    private static List<String> executeCommandAndCaptureOutput(String[] args) throws IOException {
        List<String> output = new ArrayList<>();

        switch (args[0]) {
            case "pwd" -> output.add(Commands.currentDirectory.toString());
            case "ls" -> {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(Commands.currentDirectory)) {
                    for (Path entry : stream) {
                        output.add(entry.getFileName().toString());
                    }
                }
            }
            case "cat" -> {
                if (args.length == 1) {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter text (type 'EOF' to end):");
                    String line;
                    while (!(line = scanner.nextLine()).equals("EOF")) {
                        output.add(line);
                    }
                } else {
                    Path filePath = Commands.currentDirectory.resolve(args[1]);
                    Files.lines(filePath).forEach(output::add);
                }
            }
            case "grep" -> {
                String searchTerm = args[1];
                output = Files.lines(Commands.currentDirectory.resolve(args[2]))
                        .filter(line -> line.contains(searchTerm))
                        .collect(Collectors.toList());
            }
            case "uniq" -> {
                Path filePath = Commands.currentDirectory.resolve(args[1]);
                output = Files.lines(filePath).distinct().collect(Collectors.toList());
            }
            case "sort" -> {
                Path sortFilePath = Commands.currentDirectory.resolve(args[1]);
                output = Files.lines(sortFilePath).sorted().collect(Collectors.toList());
            }
            default -> throw new UnsupportedOperationException("Unsupported command: " + args[0]);
        }
        return output;
    }

    private static List<String> executeCommandWithInput(String[] args, List<String> input) {
        List<String> output;

        switch (args[0]) {
            case "grep" -> {
                String searchTerm = args[1];
                output = input.stream().filter(line -> line.contains(searchTerm)).collect(Collectors.toList());
            }
            case "uniq" -> output = input.stream().distinct().collect(Collectors.toList());
            case "sort" -> output = input.stream().sorted().collect(Collectors.toList());
            default -> throw new UnsupportedOperationException("Unsupported command: " + args[0]);
        }
        return output;
    }

    private static void executeCommand(String[] args) throws IOException {
        switch (args[0]) {
            case "pwd" -> Commands.pwd();
            case "cd" -> Commands.cd(args);
            case "ls" -> Commands.ls(args);
            case "mkdir" -> Commands.mkdir(args);
            case "rmdir" -> Commands.rmdir(args);
            case "touch" -> Commands.touch(args);
            case "mv" -> Commands.mv(args);
            case "rm" -> Commands.rm(args);
            case "cat" -> Commands.cat(args);
            case "grep" -> Commands.grep(args);
            case "uniq" -> Commands.uniq(args);
            case "sort" -> Commands.sort(args);
            case "echo" -> Commands.echo(args);
            case "nano" -> Commands.nano(args);
            case "exit" -> Commands.exit();
            case "help" -> Commands.help();
            default -> System.out.println("Unknown command. Type 'help' to see available commands.");
        }
    }
}
