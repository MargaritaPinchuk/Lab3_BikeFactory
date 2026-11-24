package bikefactory.ui;

import bikefactory.model.Bicycle;
import bikefactory.service.BikeManager;
import bikefactory.util.Validator;

import java.util.Date;
import java.util.Scanner;

public class Menu {
    private BikeManager manager;
    private Scanner scanner;

    public Menu(BikeManager manager) {
        this(manager, new Scanner(System.in));
    }

    public Menu(BikeManager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }

    public void run() {
        boolean working = true;
        while (working) {
            printMenu();
            if (!scanner.hasNextLine()) {
                break;
            }
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    manager.printList();
                    break;
                case "2":
                    manager.printMap();
                    break;
                case "3":
                    addBike();
                    break;
                case "4":
                    editBike();
                    break;
                case "5":
                    deleteBike();
                    break;
                case "6":
                    sortBikes();
                    break;
                case "7":
                    manager.loadFromFile();
                    break;
                case "8":
                    manager.saveToFile();
                    break;
                case "9":
                    manager.encryptDataFile();
                    break;
                case "10":
                    manager.decryptDataFile();
                    break;
                case "11":
                    manager.backupToZip("backup.zip");
                    break;
                case "12":
                    manager.restoreFromZip("backup.zip", "restored");
                    break;
                case "0":
                    working = false;
                    System.out.println("До свидания!");
                    break;
                default:
                    System.out.println("Нет такого пункта меню.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Фабрика велосипедов");
        System.out.println("1 - Показать список (List)");
        System.out.println("2 - Показать словарь (Map)");
        System.out.println("3 - Добавить велосипед");
        System.out.println("4 - Редактировать велосипед");
        System.out.println("5 - Удалить велосипед");
        System.out.println("6 - Сортировать список");
        System.out.println("7 - Загрузить данные из файла");
        System.out.println("8 - Сохранить данные в файл");
        System.out.println("9 - Зашифровать файл данных (AES)");
        System.out.println("10 - Расшифровать файл данных (AES)");
        System.out.println("11 - Создать резервный архив (zip)");
        System.out.println("12 - Восстановить из архива (zip)");
        System.out.println("0 - Выход");
        System.out.print("Ваш выбор: ");
    }

    private void addBike() {
        int id = manager.nextId();
        System.out.println("Новому велосипеду присвоен id " + id);
        Bicycle bike = readBike(id, null);
        manager.addBike(bike);
    }

    private void editBike() {
        int id = askInt("id редактируемого велосипеда", 1, 999999, null);
        Bicycle old = manager.findBike(id);
        if (old == null) {
            System.out.println("Велосипеда с id " + id + " нет.");
            return;
        }
        System.out.println("Сейчас: " + old);
        System.out.println("Чтобы оставить значение без изменений, просто нажмите Enter.");
        Bicycle updated = readBike(id, old);
        manager.updateBike(updated);
    }

    private void deleteBike() {
        int id = askInt("id удаляемого велосипеда", 1, 999999, null);
        Bicycle bike = manager.findBike(id);
        if (bike == null) {
            System.out.println("Велосипеда с id " + id + " нет.");
            return;
        }
        System.out.println(bike);
        System.out.print("Удалить? (да/нет): ");
        String answer = scanner.nextLine().trim();
        if (answer.equalsIgnoreCase("да")) {
            manager.removeBike(id);
            System.out.println("Удалено.");
        } else {
            System.out.println("Отменено.");
        }
    }

    private void sortBikes() {
        System.out.println("Сортировать по: 1 - типу, 2 - модели, 3 - весу, 4 - дате выпуска, 5 - цене");
        int field = askInt("номер поля", 1, 5, null);
        int direction = askInt("порядок (1 - по возрастанию, 2 - по убыванию)", 1, 2, null);
        manager.sortList(field, direction == 1);
        manager.printList();
    }

    private Bicycle readBike(int id, Bicycle old) {
        String type = askText("Тип (горный, шоссейный...)", old == null ? null : old.getType());
        String model = askText("Модель", old == null ? null : old.getModel());
        int wheel = askInt("Диаметр колёс, дюймы", 12, 29,
                old == null ? null : String.valueOf(old.getWheelDiameter()));
        int gears = askInt("Количество передач", 1, 36,
                old == null ? null : String.valueOf(old.getGears()));
        double weight = askDouble("Вес, кг", 3, 40,
                old == null ? null : String.valueOf(old.getWeight()));
        Date date = askDate("Дата выпуска",
                old == null ? null : Validator.formatDate(old.getReleaseDate()));
        double price = askDouble("Цена", 1, 100000,
                old == null ? null : String.valueOf(old.getPrice()));
        return new Bicycle(id, type, model, wheel, gears, weight, date, price);
    }

    private String ask(String prompt, String oldValue) {
        if (oldValue == null) {
            System.out.print(prompt + ": ");
        } else {
            System.out.print(prompt + " [" + oldValue + "]: ");
        }
        String line = scanner.nextLine();
        if (line.trim().isEmpty() && oldValue != null) {
            return oldValue;
        }
        return line;
    }

    private String askText(String field, String oldValue) {
        while (true) {
            try {
                return Validator.parseText(ask(field, oldValue), field);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private int askInt(String field, int min, int max, String oldValue) {
        while (true) {
            try {
                return Validator.parseInt(ask(field + " (" + min + "-" + max + ")", oldValue), field, min, max);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private double askDouble(String field, double min, double max, String oldValue) {
        while (true) {
            try {
                return Validator.parseDouble(ask(field + " (" + min + "-" + max + ")", oldValue), field, min, max);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private Date askDate(String field, String oldValue) {
        while (true) {
            try {
                return Validator.parseDate(ask(field + " (дд.мм.гггг)", oldValue), field);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}