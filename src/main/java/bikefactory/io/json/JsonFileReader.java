package bikefactory.io.json;

import bikefactory.io.FileHandler;
import bikefactory.model.Bicycle;
import bikefactory.util.BicycleBuilder;
import bikefactory.util.ErrorLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonFileReader extends FileHandler {
    public JsonFileReader(String fileName) {
        super(fileName);
    }

    @Override
    public String getFormatName() {
        return "json";
    }

    public List<Bicycle> read(ErrorLogger logger) throws IOException {
        Path path = Paths.get(getFileName());
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IOException("не удалось прочитать файл " + getFileName() + ": " + e.getMessage(), e);
        }

        Object root;
        try {
            root = JsonParser.parse(new String(bytes, StandardCharsets.UTF_8));
        } catch (JsonParseException e) {
            throw new IOException("ошибка разбора JSON в файле " + getFileName() + ": " + e.getMessage(), e);
        }

        List<?> items = findItems(root);
        if (items == null) {
            throw new IOException("в файле " + getFileName()
                    + " ожидается массив [ {...} ] или объект { \"bikes\": [ {...} ] }");
        }

        List<Bicycle> result = new ArrayList<>();
        int number = 0;
        for (Object item : items) {
            number++;
            if (!(item instanceof Map)) {
                logger.log("JSON, элемент #" + number + ": ожидался объект {...}, а получен "
                        + describe(item), String.valueOf(item));
                continue;
            }
            Map<?, ?> object = (Map<?, ?>) item;
            Map<String, String> fields = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : object.entrySet()) {
                fields.put(asText(entry.getKey()), asText(entry.getValue()));
            }
            try {
                result.add(BicycleBuilder.create(fields));
            } catch (IllegalArgumentException e) {
                logger.log("JSON, элемент #" + number + ": " + e.getMessage(), String.valueOf(object));
            }
        }
        return result;
    }

    private static List<?> findItems(Object root) {
        if (root instanceof List) {
            return (List<?>) root;
        }
        if (root instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) root;
            Object bikes = map.get("bikes");
            if (bikes instanceof List) {
                return (List<?>) bikes;
            }
            // на случай файла из одного объекта
            if (map.containsKey(BicycleBuilder.MODEL) || map.containsKey(BicycleBuilder.ID)) {
                return Collections.singletonList(root);
            }
        }
        return null;
    }

    private static String asText(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Double) {
            double number = (Double) value;
            if (number == Math.rint(number) && !Double.isInfinite(number)) {
                return String.valueOf((long) number);
            }
            return String.valueOf(number);
        }
        return String.valueOf(value);
    }

    private static String describe(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Map) {
            return "объект";
        }
        if (value instanceof List) {
            return "массив";
        }
        if (value instanceof String) {
            return "строку";
        }
        return String.valueOf(value);
    }
}
