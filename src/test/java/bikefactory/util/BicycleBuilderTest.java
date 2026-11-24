package bikefactory.util;

import bikefactory.model.Bicycle;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BicycleBuilderTest {

    private Map<String, String> validFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put(BicycleBuilder.ID, "1");
        fields.put(BicycleBuilder.TYPE, "горный");
        fields.put(BicycleBuilder.MODEL, "Stels Navigator");
        fields.put(BicycleBuilder.WHEEL_DIAMETER, "26");
        fields.put(BicycleBuilder.GEARS, "21");
        fields.put(BicycleBuilder.WEIGHT, "13.5");
        fields.put(BicycleBuilder.RELEASE_DATE, "2022-03-01");
        fields.put(BicycleBuilder.PRICE, "15990");
        return fields;
    }

    @Test
    public void create_validFields_buildsBicycle() {
        Bicycle bike = BicycleBuilder.create(validFields());
        assertEquals(1, bike.getId());
        assertEquals("Stels Navigator", bike.getModel());
        assertEquals("2022-03-01", Validator.formatIsoDate(bike.getReleaseDate()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_missingField_throws() {
        Map<String, String> fields = validFields();
        fields.remove(BicycleBuilder.MODEL);
        BicycleBuilder.create(fields);
    }

    @Test
    public void toMap_thenCreate_roundTripsTheSameData() {
        Bicycle original = BicycleBuilder.create(validFields());
        Map<String, String> asMap = BicycleBuilder.toMap(original);
        Bicycle rebuilt = BicycleBuilder.create(asMap);
        assertEquals(original.getId(), rebuilt.getId());
        assertEquals(original.getModel(), rebuilt.getModel());
        assertEquals(original.getPrice(), rebuilt.getPrice(), 0.0001);
    }
}
