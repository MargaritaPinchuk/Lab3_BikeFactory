package bikefactory.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorLogger {
    private String logFileName;

    public ErrorLogger(String logFileName) {
        this.logFileName = logFileName;
    }

    public void log(String message, String badLine) {
        String time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
        String text = "[" + time + "] ОШИБКА: " + message + " | Строка: " + badLine;
        System.out.println(text);
        try {
            Files.write(Paths.get(logFileName), (text + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Не удалось записать в лог-файл: " + e.getMessage());
        }
    }
}
