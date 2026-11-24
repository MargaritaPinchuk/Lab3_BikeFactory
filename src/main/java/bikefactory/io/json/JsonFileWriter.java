package bikefactory.io.json;

import bikefactory.io.FileHandler;
import bikefactory.model.Bicycle;
import bikefactory.util.Validator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;


public class JsonFileWriter extends FileHandler {
    private static final String INDENT = "  ";

    public JsonFileWriter(String fileName) {
        super(fileName);
    }

    @Override
    public String getFormatName() {
        return "json";
    }

    public void write(Collection<Bicycle> bikes) throws IOException {
        File target = new File(getFileName());
        File parent = target.getAbsoluteFile().getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append(INDENT).append("\"bikes\": [");
        boolean first = true;
        for (Bicycle bike : bikes) {
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append('\n').append(INDENT).append(INDENT).append('{');
            appendField(json, 3, "id", String.valueOf(bike.getId()), true);
            appendField(json, 3, "type", quote(bike.getType()), false);
            appendField(json, 3, "model", quote(bike.getModel()), false);
            appendField(json, 3, "wheelDiameter", String.valueOf(bike.getWheelDiameter()), false);
            appendField(json, 3, "gears", String.valueOf(bike.getGears()), false);
            appendField(json, 3, "weight", number(bike.getWeight()), false);
            appendField(json, 3, "releaseDate", quote(Validator.formatIsoDate(bike.getReleaseDate())), false);
            appendField(json, 3, "price", number(bike.getPrice()), false);
            json.append('\n').append(INDENT).append(INDENT).append('}');
        }
        if (!first) {
            json.append('\n');
        }
        json.append(INDENT).append("]\n");
        json.append("}\n");

        Files.write(target.toPath(), json.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void appendField(StringBuilder json, int level, String name, String value, boolean first) {
        if (first) {
            json.append('\n');
        } else {
            json.append(",\n");
        }
        for (int i = 0; i < level; i++) {
            json.append(INDENT);
        }
        json.append('"').append(name).append("\": ").append(value);
    }

    private static String number(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private static String quote(String value) {
        return '"' + escape(value) + '"';
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder result = new StringBuilder(value.length() + 8);
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            switch (current) {
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    if (current < 0x20) {
                        result.append(String.format("\\u%04x", (int) current));
                    } else {
                        result.append(current);
                    }
            }
        }
        return result.toString();
    }
}
