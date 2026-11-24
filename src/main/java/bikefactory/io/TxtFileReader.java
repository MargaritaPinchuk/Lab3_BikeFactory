package bikefactory.io;

import bikefactory.model.Bicycle;
import bikefactory.util.BikeParser;
import bikefactory.util.ErrorLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TxtFileReader extends FileHandler {
    public TxtFileReader(String fileName) {
        super(fileName);
    }

    @Override
    public String getFormatName() {
        return "txt";
    }

    public List<Bicycle> read(ErrorLogger logger) throws IOException {
        List<Bicycle> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(getFileName()), StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1); // убираем BOM, который иногда ставит Блокнот
                }
                if (line.trim().isEmpty()) {
                    continue; // пустые строки просто пропускаем
                }
                try {
                    result.add(BikeParser.parse(line));
                } catch (IllegalArgumentException e) {
                    logger.log("строка " + lineNumber + ": " + e.getMessage(), line);
                }
            }
        }
        return result;
    }
}
