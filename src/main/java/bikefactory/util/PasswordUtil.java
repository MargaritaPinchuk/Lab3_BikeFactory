package bikefactory.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final String ALGORITHM = "MD5";
    private static final Path HASH_FILE = Paths.get("password.hash");

    private PasswordUtil() {
    }

    public static boolean passwordFileExists() {
        return Files.exists(HASH_FILE);
    }

    public static void setPassword(String rawPassword) throws IOException {
        byte[] salt = generateSalt();
        String hash = hash(rawPassword, salt);
        String content = Base64.getEncoder().encodeToString(salt) + ":" + hash;
        Files.write(HASH_FILE, content.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean checkPassword(String rawPassword) throws IOException {
        if (!passwordFileExists()) {
            return false;
        }
        String content = new String(Files.readAllBytes(HASH_FILE), StandardCharsets.UTF_8).trim();
        String[] parts = content.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String expectedHash = parts[1];
        String actualHash = hash(rawPassword, salt);
        return expectedHash.equals(actualHash);
    }

    private static String hash(String rawPassword, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashed) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Алгоритм " + ALGORITHM + " недоступен", e);
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
