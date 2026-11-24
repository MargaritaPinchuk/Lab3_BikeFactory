package bikefactory.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ValidatorTest {

    @Test
    public void parseInt_validValue_returnsNumber() {
        assertEquals(21, Validator.parseInt("21", "передачи", 1, 36));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseInt_notANumber_throws() {
        Validator.parseInt("abc", "передачи", 1, 36);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseInt_outOfRange_throws() {
        Validator.parseInt("100", "передачи", 1, 36);
    }

    @Test
    public void parseDouble_withComma_isAcceptedAsDecimalSeparator() {
        assertEquals(13.5, Validator.parseDouble("13,5", "вес", 3, 40), 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseDouble_outOfRange_throws() {
        Validator.parseDouble("1000000", "цена", 1, 100000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseDouble_notANumber_throws() {
        Validator.parseDouble("abc", "цена", 1, 100000);
    }

    @Test
    public void parseText_trimsWhitespace() {
        assertEquals("Stels", Validator.parseText("  Stels  ", "модель"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseText_empty_throws() {
        Validator.parseText("   ", "модель");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseText_containsSemicolon_throws() {
        Validator.parseText("мод;ель", "модель");
    }

    @Test
    public void parseDate_validDate_isParsedAndFormattedBack() {
        Date date = Validator.parseDate("15.03.2023", "дата выпуска");
        assertEquals("15.03.2023", Validator.formatDate(date));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseDate_futureDate_throws() {
        Validator.parseDate("01.01.2099", "дата выпуска");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseDate_tooOld_throws() {
        Validator.parseDate("01.01.1980", "дата выпуска");
    }

    @Test
    public void parseIsoDate_validDate_isParsedAndFormattedBack() {
        Date date = Validator.parseIsoDate("2023-03-15", "дата выпуска");
        assertEquals("2023-03-15", Validator.formatIsoDate(date));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseIsoDate_wrongFormat_throws() {
        Validator.parseIsoDate("15.03.2023", "дата выпуска");
    }
}
