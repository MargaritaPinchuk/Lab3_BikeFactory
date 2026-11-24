package bikefactory.util;

import bikefactory.model.Bicycle;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


public final class BicycleBuilder {
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String MODEL = "model";
    public static final String WHEEL_DIAMETER = "wheelDiameter";
    public static final String GEARS = "gears";
    public static final String WEIGHT = "weight";
    public static final String RELEASE_DATE = "releaseDate";
    public static final String PRICE = "price";

    public static final String[] FIELDS = {
            ID, TYPE, MODEL, WHEEL_DIAMETER, GEARS, WEIGHT, RELEASE_DATE, PRICE
    };

    private BicycleBuilder() {
    }

    public static Bicycle create(Map<String, String> fields) {
        int id = Validator.parseInt(require(fields, ID), "id", 1, 999999);
        String type = Validator.parseText(require(fields, TYPE), "тип");
        String model = Validator.parseText(require(fields, MODEL), "модель");
        int wheel = Validator.parseInt(require(fields, WHEEL_DIAMETER), "диаметр колёс", 12, 29);
        int gears = Validator.parseInt(require(fields, GEARS), "количество передач", 1, 36);
        double weight = Validator.parseDouble(require(fields, WEIGHT), "вес", 3, 40);
        Date date = Validator.parseIsoDate(require(fields, RELEASE_DATE), "дата выпуска");
        double price = Validator.parseDouble(require(fields, PRICE), "цена", 1, 100000);
        return new Bicycle(id, type, model, wheel, gears, weight, date, price);
    }

    public static Map<String, String> toMap(Bicycle bike) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put(ID, String.valueOf(bike.getId()));
        fields.put(TYPE, bike.getType());
        fields.put(MODEL, bike.getModel());
        fields.put(WHEEL_DIAMETER, String.valueOf(bike.getWheelDiameter()));
        fields.put(GEARS, String.valueOf(bike.getGears()));
        fields.put(WEIGHT, String.valueOf(bike.getWeight()));
        fields.put(RELEASE_DATE, Validator.formatIsoDate(bike.getReleaseDate()));
        fields.put(PRICE, String.valueOf(bike.getPrice()));
        return fields;
    }

    private static String require(Map<String, String> fields, String name) {
        String value = fields.get(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("отсутствует поле '" + name + "'");
        }
        return value;
    }
}
