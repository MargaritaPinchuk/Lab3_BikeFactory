package bikefactory.io;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class ZipArchiverTest {

    @Test
    public void zipThenUnzip_restoresAllFilesWithSameContent() throws IOException {
        Path file1 = Files.createTempFile("zip-a", ".txt");
        Path file2 = Files.createTempFile("zip-b", ".txt");
        Path archive = Files.createTempFile("zip-archive", ".zip");
        Path targetDir = Files.createTempDirectory("zip-restore");
        try {
            Files.write(file1, "содержимое первого файла".getBytes(StandardCharsets.UTF_8));
            Files.write(file2, "содержимое второго файла".getBytes(StandardCharsets.UTF_8));

            ZipArchiver.zip(archive.toString(),
                    Arrays.asList(file1.toString(), file2.toString()));
            ZipArchiver.unzip(archive.toString(), targetDir.toString());

            Path restored1 = targetDir.resolve(file1.getFileName());
            Path restored2 = targetDir.resolve(file2.getFileName());

            assertArrayEquals(Files.readAllBytes(file1), Files.readAllBytes(restored1));
            assertArrayEquals(Files.readAllBytes(file2), Files.readAllBytes(restored2));
        } finally {
            Files.deleteIfExists(file1);
            Files.deleteIfExists(file2);
            Files.deleteIfExists(archive);
            deleteRecursively(targetDir);
        }
    }

    @Test
    public void zip_missingFile_isSilentlySkipped() throws IOException {
        Path archive = Files.createTempFile("zip-archive2", ".zip");
        try {
            ZipArchiver.zip(archive.toString(), Arrays.asList("no-such-file-xyz.txt"));
            assertTrue(Files.size(archive) > 0);
        } finally {
            Files.deleteIfExists(archive);
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        Files.walk(path)
                .sorted(Comparator.comparingInt(Path::getNameCount).reversed())
                .forEach(p -> p.toFile().delete());
    }
}
