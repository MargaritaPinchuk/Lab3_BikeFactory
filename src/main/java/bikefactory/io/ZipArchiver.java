package bikefactory.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipArchiver {
    private ZipArchiver() {
    }

    public static void zip(String archiveFileName, List<String> fileNames) throws IOException {
        try (OutputStream fileOut = new FileOutputStream(archiveFileName);
             ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fileOut))) {
            for (String fileName : fileNames) {
                File file = new File(fileName);
                if (!file.isFile()) {
                    continue;
                }
                zipOut.putNextEntry(new ZipEntry(file.getName()));
                try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, read);
                    }
                }
                zipOut.closeEntry();
            }
        }
    }

    public static void unzip(String archiveFileName, String targetDir) throws IOException {
        Path targetPath = Paths.get(targetDir).toAbsolutePath().normalize();
        Files.createDirectories(targetPath);
        try (ZipInputStream zipIn = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(archiveFileName)))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path entryPath = targetPath.resolve(entry.getName()).normalize();
                if (!entryPath.startsWith(targetPath)) {
                    throw new IOException("небезопасная запись в архиве: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    if (entryPath.getParent() != null) {
                        Files.createDirectories(entryPath.getParent());
                    }
                    try (OutputStream out = new BufferedOutputStream(
                            new FileOutputStream(entryPath.toFile()))) {
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = zipIn.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        }
    }
}
