package bikefactory.io.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonParser {
    private static final int MAX_DEPTH = 64;

    private final String text;
    private int position;

    private JsonParser(String text) {
        this.text = text;
    }

    public static Object parse(String text) throws JsonParseException {
        if (text == null || text.isEmpty()) {
            throw new JsonParseException("файл пуст, JSON не найден", 1, 1);
        }
        JsonParser parser = new JsonParser(text);
        if (text.charAt(0) == '\uFEFF') {
            parser.position = 1; // пропускаем BOM
        }
        Object value = parser.readValue(0);
        parser.skipWhitespace();
        if (parser.position < text.length()) {
            throw parser.error("после JSON-значения найден лишний текст");
        }
        return value;
    }

    private Object readValue(int depth) throws JsonParseException {
        if (depth > MAX_DEPTH) {
            throw error("слишком большая вложенность JSON");
        }
        skipWhitespace();
        if (position >= text.length()) {
            throw error("неожиданный конец файла: ожидалось значение");
        }
        char current = text.charAt(position);
        switch (current) {
            case '{':
                return readObject(depth);
            case '[':
                return readArray(depth);
            case '"':
                return readString();
            case 't':
                expectWord("true");
                return Boolean.TRUE;
            case 'f':
                expectWord("false");
                return Boolean.FALSE;
            case 'n':
                expectWord("null");
                return null;
            default:
                if (current == '-' || isDigit(current)) {
                    return readNumber();
                }
                throw error("неожиданный символ '" + current + "'");
        }
    }

    private Map<String, Object> readObject(int depth) throws JsonParseException {
        Map<String, Object> result = new LinkedHashMap<>();
        position++;
        skipWhitespace();
        if (peek() == '}') {
            position++;
            return result;
        }
        while (true) {
            skipWhitespace();
            if (peek() != '"') {
                throw error("ожидалось имя поля в кавычках");
            }
            String key = readString();
            skipWhitespace();
            if (peek() != ':') {
                throw error("после имени поля ожидалось ':'");
            }
            position++;
            result.put(key, readValue(depth + 1));
            skipWhitespace();
            char next = peek();
            if (next == ',') {
                position++;
                continue;
            }
            if (next == '}') {
                position++;
                return result;
            }
            throw error("ожидалось ',' или '}'");
        }
    }

    private List<Object> readArray(int depth) throws JsonParseException {
        List<Object> result = new ArrayList<>();
        position++;
        skipWhitespace();
        if (peek() == ']') {
            position++;
            return result;
        }
        while (true) {
            result.add(readValue(depth + 1));
            skipWhitespace();
            char next = peek();
            if (next == ',') {
                position++;
                continue;
            }
            if (next == ']') {
                position++;
                return result;
            }
            throw error("ожидалось ',' или ']'");
        }
    }

    private String readString() throws JsonParseException {
        position++;
        StringBuilder value = new StringBuilder();
        while (position < text.length()) {
            char current = text.charAt(position++);
            if (current == '"') {
                return value.toString();
            }
            if (current == '\\') {
                if (position >= text.length()) {
                    throw error("незаконченная escape-последовательность");
                }
                char escaped = text.charAt(position++);
                switch (escaped) {
                    case '"':
                        value.append('"');
                        break;
                    case '\\':
                        value.append('\\');
                        break;
                    case '/':
                        value.append('/');
                        break;
                    case 'b':
                        value.append('\b');
                        break;
                    case 'f':
                        value.append('\f');
                        break;
                    case 'n':
                        value.append('\n');
                        break;
                    case 'r':
                        value.append('\r');
                        break;
                    case 't':
                        value.append('\t');
                        break;
                    case 'u':
                        value.append(readUnicode());
                        break;
                    default:
                        throw error("неизвестная escape-последовательность \\" + escaped);
                }
                continue;
            }
            if (current < 0x20) {
                throw error("в строке недопустимый управляющий символ");
            }
            value.append(current);
        }
        throw error("строка не закрыта кавычкой");
    }

    private char readUnicode() throws JsonParseException {
        if (position + 4 > text.length()) {
            throw error("незаконченная \\u-последовательность");
        }
        String hex = text.substring(position, position + 4);
        for (int i = 0; i < hex.length(); i++) {
            char digit = hex.charAt(i);
            boolean valid = (digit >= '0' && digit <= '9')
                    || (digit >= 'a' && digit <= 'f')
                    || (digit >= 'A' && digit <= 'F');
            if (!valid) {
                throw error("неверная \\u-последовательность '" + hex + "'");
            }
        }
        position += 4;
        return (char) Integer.parseInt(hex, 16);
    }

    /** Число по правилам JSON: -?цифры[.цифры][e[+-]цифры]. */
    private Double readNumber() throws JsonParseException {
        int start = position;
        if (text.charAt(position) == '-') {
            position++;
            if (position >= text.length()) {
                throw error("незаконченное число");
            }
        }
        char first = text.charAt(position);
        if (first == '0') {
            position++;
        } else if (first >= '1' && first <= '9') {
            while (position < text.length() && isDigit(text.charAt(position))) {
                position++;
            }
        } else {
            throw error("неверный формат числа");
        }
        if (position < text.length() && text.charAt(position) == '.') {
            position++;
            if (position >= text.length() || !isDigit(text.charAt(position))) {
                throw error("после точки в числе ожидалась цифра");
            }
            while (position < text.length() && isDigit(text.charAt(position))) {
                position++;
            }
        }
        if (position < text.length() && (text.charAt(position) == 'e' || text.charAt(position) == 'E')) {
            position++;
            if (position < text.length() && (text.charAt(position) == '+' || text.charAt(position) == '-')) {
                position++;
            }
            if (position >= text.length() || !isDigit(text.charAt(position))) {
                throw error("в экспоненте ожидалась цифра");
            }
            while (position < text.length() && isDigit(text.charAt(position))) {
                position++;
            }
        }
        String number = text.substring(start, position);
        double value;
        try {
            value = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            throw error("неверное число '" + number + "'");
        }
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw error("слишком большое число '" + number + "'");
        }
        return value;
    }

    private void expectWord(String word) throws JsonParseException {
        if (!text.startsWith(word, position)) {
            throw error("ожидалось '" + word + "'");
        }
        position += word.length();
    }

    private void skipWhitespace() {
        while (position < text.length()) {
            char current = text.charAt(position);
            if (current == ' ' || current == '\t' || current == '\n' || current == '\r') {
                position++;
            } else {
                break;
            }
        }
    }

    private char peek() throws JsonParseException {
        if (position >= text.length()) {
            throw error("неожиданный конец файла");
        }
        return text.charAt(position);
    }

    private static boolean isDigit(char value) {
        return value >= '0' && value <= '9';
    }

    /** Считает номер строки и позицию для текущего места в файле. */
    private JsonParseException error(String message) {
        int line = 1;
        int column = 1;
        int limit = Math.min(position, text.length());
        int index = 0;
        while (index < limit) {
            char current = text.charAt(index);
            if (current == '\r') {
                if (index + 1 < limit && text.charAt(index + 1) == '\n') {
                    index++;
                }
                index++;
                line++;
                column = 1;
            } else if (current == '\n') {
                index++;
                line++;
                column = 1;
            } else {
                index++;
                column++;
            }
        }
        return new JsonParseException(message, line, column);
    }
}
