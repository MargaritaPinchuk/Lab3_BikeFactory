package bikefactory.io;

import java.io.File;


public abstract class FileHandler {
    private String fileName;

    public FileHandler(String fileName) {
        this.fileName = fileName;
    }

    public abstract String getFormatName();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean fileExists() {
        return new File(fileName).exists();
    }
}
