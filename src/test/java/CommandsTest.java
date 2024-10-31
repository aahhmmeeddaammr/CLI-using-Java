import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommandsTest {
    private static final Path testingDirectory = Paths.get(System.getProperty("user.dir"), "testingDirectory");
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() throws IOException {
        Files.createDirectories(testingDirectory);
        Commands.currentDirectory = testingDirectory;

        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() throws IOException {
        System.setOut(originalOut);

        Files.walk(testingDirectory)
                .map(Path::toFile)
                .forEach(file -> file.delete());
    }

    @Test
    public void testPwd() {
        assertEquals(testingDirectory.toString(), Commands.currentDirectory.toString());
    }

    @Test
    public void testCd() throws IOException {
        Path newDir = testingDirectory.resolve("subDir");
        Commands.mkdir(new String[]{"mkdir", "subDir"});
        Commands.cd(new String[]{"cd", "subDir"});
        assertEquals(newDir, Commands.currentDirectory);
    }

    @Test
    public void testLs() throws IOException {
        Path file1 = testingDirectory.resolve("file1.txt");
        Path file2 = testingDirectory.resolve("file2.txt");
        Files.createFile(file1);
        Files.createFile(file2);

        Commands.ls(new String[]{"ls"});
        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));
    }

    @Test
    public void testLsDashA() throws IOException {
        Path file1 = testingDirectory.resolve("file1.txt");
        Path file2 = testingDirectory.resolve("file2.txt");
        Path file3 = testingDirectory.resolve(".hiddenFile.txt");
        Files.createFile(file1);
        Files.createFile(file2);
        Files.createFile(file3);

        Commands.ls(new String[]{"ls", "-a"});
        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));
        assertTrue(Files.exists(file3));
    }

    @Test
    public void testLsDashR() throws IOException {
        Path file1 = testingDirectory.resolve("file1.txt");
        Path file2 = testingDirectory.resolve("file2.txt");
        Files.createFile(file1);
        Files.createFile(file2);

        Commands.ls(new String[]{"ls", "-r"});

        String output = outputStream.toString();

        int file1Index = output.indexOf("file1.txt");
        int file2Index = output.indexOf("file2.txt");

        assertTrue(file1Index > file2Index);
    }

    @Test
    public void testMkdir() throws IOException {
        Commands.mkdir(new String[]{"mkdir", "newDir"});
        Path newDir = testingDirectory.resolve("newDir");
        assertTrue(Files.isDirectory(newDir));
    }

    @Test
    public void testRmdir() throws IOException {
        Path removedDir = testingDirectory.resolve("removedDir");
        Files.createDirectories(removedDir);
        Commands.rmdir(new String[]{"rmdir", "removedDir"});
        assertFalse(Files.exists(removedDir));
    }

    @Test
    public void testTouch() throws IOException {
        Path newFile = testingDirectory.resolve("newFile.txt");
        Commands.touch(new String[]{"touch", "newFile.txt"});
        assertTrue(Files.exists(newFile));
    }

    @Test
    public void testMvRename() throws IOException {
        Path oldFile = testingDirectory.resolve("oldFile.txt");
        Path newFile = testingDirectory.resolve("newFile.txt");
        Files.createFile(oldFile);

        Commands.mv(new String[]{"mv", "oldFile.txt", "newFile.txt"});
        assertTrue(Files.exists(newFile));
        assertFalse(Files.exists(oldFile));
    }

    @Test
    public void testMvDirectory() throws IOException {
        Path file = testingDirectory.resolve("file.txt");
        Path dir = testingDirectory.resolve("test");
        Path newFile = testingDirectory.resolve("test/file.txt");
        Files.createFile(file);
        Files.createDirectory(dir);
        Commands.mv(new String[]{"mv", "file.txt", "test/"});
        assertFalse(Files.exists(file));
        assertTrue(Files.exists(newFile));
    }

    @Test
    public void testRm() throws IOException {
        Path removedFile = testingDirectory.resolve("removedFile.txt");
        Files.createFile(removedFile);
        Commands.rm(new String[]{"rm", "removedFile.txt"});
        assertFalse(Files.exists(removedFile));
    }

    @Test
    public void testCat() throws IOException {
        Path file = testingDirectory.resolve("writingFile.txt");
        List<String> lines = List.of("Khalid", "Ahmed", "Youssef");
        Files.write(file, lines);

        Commands.cat(new String[]{"cat", "writingFile.txt"});

        List<String> outputList = List.of(outputStream.toString().split("\r\n"));

        for (int i = 0; i < outputList.size(); ++i) {
            assertEquals(outputList.get(i), lines.get(i));
        }
    }

}
