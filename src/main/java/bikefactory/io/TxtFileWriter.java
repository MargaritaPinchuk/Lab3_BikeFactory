package bikefactory.io;

import bikefactory.model.Bicycle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class TxtFileWriter extends FileHandler {
    public TxtFileWriter(String fileName) {
        super(fileName);
    }

    @Override
    public String getFormatName() {
        return "txt";
    }

    public void write(Collection<Bicycle> bikes) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getFileName()), StandardCharsets.UTF_8)) {
            for (Bicycle bike : bikes) {
                writer.write(bike.toFileLine());
                writer.newLine();
            }
        }
    }
}
