package bikefactory.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PasswordUtilTest {

    private static final Path HASH_FILE = Paths.get("password.hash");

    @Before
    public void cleanUp() throws IOException {
        Files.deleteIfExists(HASH_FILE);
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(HASH_FILE);
    }

    @Test
    public void passwordFileExists_beforeSetPassword_isFalse() {
        assertFalse(PasswordUtil.passwordFileExists());
    }

    @Test
    public void setPassword_thenCheckCorrectPassword_returnsTrue() throws IOException {
        PasswordUtil.setPassword("qwerty123");
        assertTrue(PasswordUtil.checkPassword("qwerty123"));
    }

    @Test
    public void setPassword_thenCheckWrongPassword_returnsFalse() throws IOException {
        PasswordUtil.setPassword("qwerty123");
        assertFalse(PasswordUtil.checkPassword("wrongpass"));
    }

    @Test
    public void checkPassword_withoutSetPassword_returnsFalse() throws IOException {
        assertFalse(PasswordUtil.checkPassword("anything"));
    }

    @Test
    public void setPassword_twice_producesDifferentSalt() throws IOException {
        PasswordUtil.setPassword("same-password");
        String first = new String(Files.readAllBytes(HASH_FILE));
        Files.delete(HASH_FILE);
        PasswordUtil.setPassword("same-password");
        String second = new String(Files.readAllBytes(HASH_FILE));
        assertNotEquals(first, second);
    }
}
