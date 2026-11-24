package bikefactory.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Validator {
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";

    public static int parseInt(String text, String field, int min, int max) {
        if (text == null) {
            throw new IllegalArgumentException(field + ": поле не заполнено");
        }
        int value;
        try {
            value = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + ": '" + text + "' не является целым числом");
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException(field + ": значение должно быть от " + min + " до " + max);
        }
        return value;
    }

    public static double parseDouble(String text, String field, double min, double max) {
        if (text == null) {
            throw new IllegalArgumentException(field + ": поле не заполнено");
        }
        double value;
        try {
            value = Double.parseDouble(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + ": '" + text + "' не является числом");
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(field + ": недопустимое число");
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException(field + ": значение должно быть от " + min + " до " + max);
        }
        return value;
    }

    public static String parseText(String text, String field) {
        if (text == null) {
            throw new IllegalArgumentException(field + ": поле не может быть пустым");
        }
        String value = text.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(field + ": поле не может быть пустым");
        }
        if (value.contains(";")) {
            throw new IllegalArgumentException(field + ": нельзя использовать символ ';'");
        }
        return value;
    }

    public static Date parseDate(String text, String field) {
        String value = text == null ? "" : text.trim();
        if (value.length() != DATE_FORMAT.length()) {
            throw new IllegalArgumentException(field + ": дата должна быть в формате дд.мм.гггг");
        }
        return parseDateWithFormat(value, field, DATE_FORMAT);
    }

    /**
     * Дата в формате ISO (гггг-мм-дд) — используется в XML и JSON.
     */
    public static Date parseIsoDate(String text, String field) {
        String value = text == null ? "" : text.trim();
        if (value.length() != ISO_DATE_FORMAT.length()) {
            throw new IllegalArgumentException(field + ": дата должна быть в формате гггг-мм-дд");
        }
        return parseDateWithFormat(value, field, ISO_DATE_FORMAT);
    }

    private static Date parseDateWithFormat(String value, String field, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        Date date;
        try {
            date = format.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(field + ": '" + value + "' не похоже на дату " + pattern);
        }
        if (!format.format(date).equals(value)) {
            throw new IllegalArgumentException(field + ": '" + value + "' не похоже на дату " + pattern);
        }
        if (date.after(new Date())) {
            throw new IllegalArgumentException(field + ": дата выпуска не может быть в будущем");
        }
        if (date.before(parseMinDate())) {
            throw new IllegalArgumentException(field + ": слишком старая дата (раньше 01.01.1990)");
        }
        return date;
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    public static String formatIsoDate(Date date) {
        return new SimpleDateFormat(ISO_DATE_FORMAT).format(date);
    }

    private static Date parseMinDate() {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse("01.01.1990");
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }
}
