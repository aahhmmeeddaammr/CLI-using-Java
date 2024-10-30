import java.io.IOException;
import java.nio.file.*;

public class Commands {
    public static Path currentDirectory = Paths.get(System.getProperty("user.dir"));
    public static void pwd() {
        System.out.println(currentDirectory);
    }

    public static void cd(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: cd <directory>");
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
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory)) {
            for (Path entry : stream) {
                System.out.println(entry.getFileName());
            }
        }
    }

    public static void mkdir(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: mkdir <directory>");
            return;
        }
        Path newPath = currentDirectory.resolve(args[1]);
        Files.createDirectories(newPath);
        System.out.println("Directory created: " + newPath);
    }

    public static void rmdir(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: rmdir <directory>");
            return;
        }
        Path dirPath = currentDirectory.resolve(args[1]);
        if (Files.isDirectory(dirPath)) {
            Files.delete(dirPath);
            System.out.println("Directory removed: " + dirPath);
        } else {
            System.out.println("Not a directory: " + args[1]);
        }
    }

    public static void touch(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: touch <file>");
            return;
        }
        Path filePath = currentDirectory.resolve(args[1]);
        Files.createFile(filePath);
        System.out.println("File created: " + filePath);
    }

    public static void mv(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: mv <source> <destination>");
            return;
        }
        Path sourcePath = currentDirectory.resolve(args[1]);
        if (!sourcePath.toFile().exists()) {
            System.out.println("File does not exist: " + args[1]);
            return;
        }
        Path destPath = currentDirectory.resolve(args[2]);
        if (!destPath.toFile().exists()) {
            System.out.println("File does not exist: " + args[2]);
            return;
        }
        try{
            Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            System.out.println("File already exists: " + args[2]);
            return;
        }
        System.out.println("Moved " + sourcePath + " to " + destPath);
    }

    public static void rm(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: rm <file>");
            return;
        }
        Path filePath = currentDirectory.resolve(args[1]);
        if(Files.isDirectory(filePath)) {
            Files.delete(filePath);
        }else {
            System.out.println("Not a directory: " + args[1]);
            return;
        }
        System.out.println("File removed: " + Files.deleteIfExists(filePath));
    }

    public static void cat(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: cat <file>");
            return;
        }
        Path filePath = currentDirectory.resolve(args[1]);
        Files.lines(filePath).forEach(System.out::println);
    }

    public static void exit() {
        System.out.println("Exiting CLI.");
        System.exit(0);
    }

    public static void help() {
        System.out.println("Available commands:\n\tpwd\n\tcd\n\tls\n\tmkdir\n\trmdir\n\ttouch\n\tmv\n\trm\n\tcat\n\texit\n\thelp");
    }
}
