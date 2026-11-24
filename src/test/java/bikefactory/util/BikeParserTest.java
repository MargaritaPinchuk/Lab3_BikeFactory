package bikefactory.util;

import bikefactory.model.Bicycle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BikeParserTest {

    @Test
    public void parse_validLine_createsBicycleWithAllFields() {
        Bicycle bike = BikeParser.parse("1;горный;Stels Navigator;26;21;13.5;01.03.2022;15990");
        assertEquals(1, bike.getId());
        assertEquals("горный", bike.getType());
        assertEquals("Stels Navigator", bike.getModel());
        assertEquals(26, bike.getWheelDiameter());
        assertEquals(21, bike.getGears());
        assertEquals(13.5, bike.getWeight(), 0.0001);
        assertEquals(15990.0, bike.getPrice(), 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_wrongFieldCount_throws() {
        BikeParser.parse("1;горный;Stels Navigator;26;21;13.5;01.03.2022");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_invalidWheelDiameter_throws() {
        BikeParser.parse("1;горный;Stels Navigator;100;21;13.5;01.03.2022;15990");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_invalidDate_throws() {
        BikeParser.parse("1;горный;Stels Navigator;26;21;13.5;99.99.9999;15990");
    }
}
