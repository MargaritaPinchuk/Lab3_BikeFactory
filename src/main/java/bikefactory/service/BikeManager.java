package bikefactory.service;

import bikefactory.io.TxtFileReader;
import bikefactory.io.TxtFileWriter;
import bikefactory.io.ZipArchiver;
import bikefactory.model.Bicycle;
import bikefactory.storage.BikeList;
import bikefactory.storage.BikeMap;
import bikefactory.util.AesCipherUtil;
import bikefactory.util.ErrorLogger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class BikeManager {
    private BikeList list = new BikeList();
    private BikeMap map = new BikeMap();
    private TxtFileReader reader;
    private TxtFileWriter writer;
    private ErrorLogger logger;

    public BikeManager(String fileName, ErrorLogger logger) {
        this.reader = new TxtFileReader(fileName);
        this.writer = new TxtFileWriter(fileName);
        this.logger = logger;
    }

    public void loadFromFile() {
        if (!reader.fileExists()) {
            System.out.println("Файл " + reader.getFileName() + " не найден, начинаем с пустого списка.");
            return;
        }
        try {
            List<Bicycle> loaded = reader.read(logger);
            list.clear();
            map.clear();
            for (Bicycle bike : loaded) {
                if (list.add(bike)) {
                    map.add(bike);
                } else {
                    logger.log("повторяющийся id " + bike.getId() + ", объект пропущен", bike.toFileLine());
                }
            }
            System.out.println("Загружено велосипедов: " + list.size());
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try {
            writer.write(list.getAll());
            System.out.println("Данные записаны в файл " + writer.getFileName());
        } catch (IOException e) {
            System.out.println("Не удалось записать файл: " + e.getMessage());
        }
    }

    public boolean addBike(Bicycle bike) {
        if (!list.add(bike)) {
            return false;
        }
        map.add(bike);
        saveToFile();
        return true;
    }

    public boolean updateBike(Bicycle bike) {
        if (!list.update(bike)) {
            return false;
        }
        map.update(bike);
        saveToFile();
        return true;
    }

    public boolean removeBike(int id) {
        if (!list.remove(id)) {
            return false;
        }
        map.remove(id);
        saveToFile();
        return true;
    }

    public Bicycle findBike(int id) {
        return list.getById(id);
    }

    public int nextId() {
        int max = 0;
        Iterator<Bicycle> it = list.iterator();
        while (it.hasNext()) {
            int id = it.next().getId();
            if (id > max) {
                max = id;
            }
        }
        return max + 1;
    }

    public void printList() {
        System.out.println("Список (List), всего: " + list.size());
        Iterator<Bicycle> it = list.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    public void printMap() {
        System.out.println("Словарь (Map), всего: " + map.size());
        Iterator<Bicycle> it = map.iterator();
        while (it.hasNext()) {
            Bicycle bike = it.next();
            System.out.println(bike.getId() + ": " + bike);
        }
    }

    public boolean sortList(int field, boolean ascending) {
        Comparator<Bicycle> comparator;
        switch (field) {
            case 1:
                comparator = (a, b) -> a.getType().compareToIgnoreCase(b.getType());
                break;
            case 2:
                comparator = (a, b) -> a.getModel().compareToIgnoreCase(b.getModel());
                break;
            case 3:
                comparator = (a, b) -> Double.compare(a.getWeight(), b.getWeight());
                break;
            case 4:
                comparator = (a, b) -> a.getReleaseDate().compareTo(b.getReleaseDate());
                break;
            case 5:
                comparator = (a, b) -> Double.compare(a.getPrice(), b.getPrice());
                break;
            default:
                return false;
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        list.sort(comparator);
        return true;
    }

    public void encryptDataFile() {
        try {
            String encrypted = reader.getFileName() + ".enc";
            AesCipherUtil.encryptFile(Paths.get(reader.getFileName()), Paths.get(encrypted));
            System.out.println("Файл зашифрован: " + encrypted + " (ключ хранится в secret.key)");
        } catch (IOException e) {
            System.out.println("Не удалось зашифровать файл: " + e.getMessage());
        }
    }

    public void decryptDataFile() {
        try {
            String encrypted = reader.getFileName() + ".enc";
            String decrypted = reader.getFileName() + ".decrypted";
            AesCipherUtil.decryptFile(Paths.get(encrypted), Paths.get(decrypted));
            System.out.println("Файл расшифрован: " + decrypted);
        } catch (IOException e) {
            System.out.println("Не удалось расшифровать файл: " + e.getMessage());
        }
    }

    public void backupToZip(String archiveFileName) {
        try {
            ZipArchiver.zip(archiveFileName, Arrays.asList(reader.getFileName(), "errors.log"));
            System.out.println("Создан архив " + archiveFileName);
        } catch (IOException e) {
            System.out.println("Не удалось создать архив: " + e.getMessage());
        }
    }

    public void restoreFromZip(String archiveFileName, String targetDir) {
        try {
            ZipArchiver.unzip(archiveFileName, targetDir);
            System.out.println("Данные распакованы в папку " + targetDir);
        } catch (IOException e) {
            System.out.println("Не удалось распаковать архив: " + e.getMessage());
        }
    }
}