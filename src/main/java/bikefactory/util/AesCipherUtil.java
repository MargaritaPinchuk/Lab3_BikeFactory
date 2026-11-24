package bikefactory.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public final class AesCipherUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE_BITS = 128;
    private static final Path KEY_FILE = Paths.get("secret.key");

    private AesCipherUtil() {
    }

    public static SecretKey loadOrCreateKey() throws IOException {
        if (Files.exists(KEY_FILE)) {
            byte[] encoded = Base64.getDecoder().decode(Files.readAllBytes(KEY_FILE));
            return new SecretKeySpec(encoded, ALGORITHM);
        }
        try {
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            generator.init(KEY_SIZE_BITS);
            SecretKey key = generator.generateKey();
            Files.write(KEY_FILE, Base64.getEncoder().encode(key.getEncoded()));
            return key;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Алгоритм " + ALGORITHM + " недоступен", e);
        }
    }

    public static void encryptFile(Path source, Path target) throws IOException {
        try {
            SecretKey key = loadOrCreateKey();
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

            byte[] plain = Files.readAllBytes(source);
            byte[] encrypted = cipher.doFinal(plain);

            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

            Files.write(target, result);
        } catch (Exception e) {
            throw new IOException("Не удалось зашифровать файл " + source + ": " + e.getMessage(), e);
        }
    }

    public static void decryptFile(Path source, Path target) throws IOException {
        try {
            SecretKey key = loadOrCreateKey();
            byte[] data = Files.readAllBytes(source);
            if (data.length < 16) {
                throw new IOException("файл повреждён или слишком короткий");
            }
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[data.length - 16];
            System.arraycopy(data, 0, iv, 0, 16);
            System.arraycopy(data, 16, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] plain = cipher.doFinal(encrypted);

            Files.write(target, plain);
        } catch (Exception e) {
            throw new IOException("Не удалось расшифровать файл " + source + ": " + e.getMessage(), e);
        }
    }
}
