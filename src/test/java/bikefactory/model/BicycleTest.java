package bikefactory.model;

import bikefactory.util.Validator;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BicycleTest {

    @Test
    public void toFileLine_producesSemicolonSeparatedFields() {
        Date date = Validator.parseDate("01.03.2022", "дата выпуска");
        Bicycle bike = new Bicycle(1, "горный", "Stels Navigator", 26, 21, 13.5, date, 15990);
        assertEquals("1;горный;Stels Navigator;26;21;13.5;01.03.2022;15990.0", bike.toFileLine());
    }

    @Test
    public void toString_containsKeyFields() {
        Date date = Validator.parseDate("01.03.2022", "дата выпуска");
        Bicycle bike = new Bicycle(1, "горный", "Stels Navigator", 26, 21, 13.5, date, 15990);
        String text = bike.toString();
        assertTrue(text.contains("Stels Navigator"));
        assertTrue(text.contains("горный"));
        assertTrue(text.contains("15990.0"));
    }

    @Test
    public void settersChangeTheCorrespondingFields() {
        Date date = Validator.parseDate("01.03.2022", "дата выпуска");
        Bicycle bike = new Bicycle(1, "горный", "Stels Navigator", 26, 21, 13.5, date, 15990);
        bike.setWeight(10.0);
        bike.setPrice(9999);
        assertEquals(10.0, bike.getWeight(), 0.0001);
        assertEquals(9999.0, bike.getPrice(), 0.0001);
    }
}
