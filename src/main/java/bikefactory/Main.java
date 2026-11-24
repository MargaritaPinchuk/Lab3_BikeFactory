package bikefactory;

import bikefactory.service.BikeManager;
import bikefactory.ui.Menu;
import bikefactory.util.ErrorLogger;
import bikefactory.util.PasswordUtil;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (!login(scanner)) {
            System.out.println("Доступ закрыт.");
            return;
        }

        ErrorLogger logger = new ErrorLogger("errors.log");
        BikeManager manager = new BikeManager("bikes.txt", logger);
        Menu menu = new Menu(manager, scanner);

        manager.loadFromFile();
        menu.run();
    }

    private static boolean login(Scanner scanner) {
        try {
            if (!PasswordUtil.passwordFileExists()) {
                System.out.println("Пароль ещё не задан. Придумайте пароль для входа:");
                String newPassword = scanner.nextLine();
                PasswordUtil.setPassword(newPassword);
                System.out.println("Пароль сохранён (хранится только его MD5-хэш, не сам пароль).");
                return true;
            }
            for (int attempt = 1; attempt <= 3; attempt++) {
                System.out.print("Введите пароль: ");
                String entered = scanner.nextLine();
                if (PasswordUtil.checkPassword(entered)) {
                    return true;
                }
                System.out.println("Неверный пароль. Попытка " + attempt + " из 3.");
            }
            return false;
        } catch (IOException e) {
            System.out.println("Ошибка работы с паролем: " + e.getMessage());
            return false;
        }
    }
}