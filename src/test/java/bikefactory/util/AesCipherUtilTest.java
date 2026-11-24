package bikefactory.util;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AesCipherUtilTest {

    private static final Path KEY_FILE = Paths.get("secret.key");

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(KEY_FILE);
    }

    @Test
    public void encryptThenDecrypt_returnsOriginalContent() throws IOException {
        Path source = Files.createTempFile("aes-source", ".txt");
        Path encrypted = Files.createTempFile("aes-encrypted", ".enc");
        Path decrypted = Files.createTempFile("aes-decrypted", ".txt");
        try {
            String original = "1;горный;Stels Navigator;26;21;13.5;01.03.2022;15990";
            Files.write(source, original.getBytes(StandardCharsets.UTF_8));

            AesCipherUtil.encryptFile(source, encrypted);
            AesCipherUtil.decryptFile(encrypted, decrypted);

            String result = new String(Files.readAllBytes(decrypted), StandardCharsets.UTF_8);
            assertEquals(original, result);
        } finally {
            Files.deleteIfExists(source);
            Files.deleteIfExists(encrypted);
            Files.deleteIfExists(decrypted);
        }
    }

    @Test
    public void encryptFile_producesDifferentBytesThanOriginal() throws IOException {
        Path source = Files.createTempFile("aes-source2", ".txt");
        Path encrypted = Files.createTempFile("aes-encrypted2", ".enc");
        try {
            Files.write(source, "секретные данные".getBytes(StandardCharsets.UTF_8));
            AesCipherUtil.encryptFile(source, encrypted);

            byte[] plainBytes = Files.readAllBytes(source);
            byte[] cipherBytes = Files.readAllBytes(encrypted);
            assertFalse(Arrays.equals(plainBytes, cipherBytes));
        } finally {
            Files.deleteIfExists(source);
            Files.deleteIfExists(encrypted);
        }
    }

    @Test
    public void loadOrCreateKey_calledTwice_returnsTheSameKey() throws IOException {
        byte[] first = AesCipherUtil.loadOrCreateKey().getEncoded();
        byte[] second = AesCipherUtil.loadOrCreateKey().getEncoded();
        assertArrayEquals(first, second);
    }
}
