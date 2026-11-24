package bikefactory.util;

import bikefactory.model.Bicycle;

import java.util.Date;


public class BikeParser {
    public static Bicycle parse(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length != 8) {
            throw new IllegalArgumentException("ожидалось 8 полей, а найдено " + parts.length);
        }
        int id = Validator.parseInt(parts[0], "id", 1, 999999);
        String type = Validator.parseText(parts[1], "тип");
        String model = Validator.parseText(parts[2], "модель");
        int wheel = Validator.parseInt(parts[3], "диаметр колёс", 12, 29);
        int gears = Validator.parseInt(parts[4], "количество передач", 1, 36);
        double weight = Validator.parseDouble(parts[5], "вес", 3, 40);
        Date date = Validator.parseDate(parts[6], "дата выпуска");
        double price = Validator.parseDouble(parts[7], "цена", 1, 100000);
        return new Bicycle(id, type, model, wheel, gears, weight, date, price);
    }
}
