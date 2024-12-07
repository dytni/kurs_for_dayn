package com.kurs.server.service;

import com.kurs.server.repository.AdminRepository;
import com.kurs.server.repository.DatabaseManager;
import com.kurs.server.util.ServerLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class AdminService {
    private final DatabaseManager adminRepository;
    private final ServerLogger logger;

    public AdminService(DatabaseManager adminRepository, ServerLogger logger) {
        this.adminRepository = adminRepository;
        this.logger = logger;
    }

    // Получение списка администраторов
    public void getAllAdmins(PrintWriter out) {
        try {
            List<String> admins = adminRepository.getAllAdmins();
            out.println(admins.size());
            for (String admin : admins) {
                out.println(admin);
            }
            logger.log("INFO: Список администраторов успешно отправлен.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при получении списка администраторов: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    // Добавление администратора
    public void addAdmin(BufferedReader in, PrintWriter out) throws IOException {
        try {
            String login = in.readLine();
            String password = in.readLine();

            if (adminRepository.isLoginATaken(login)) {
                out.println("LOGIN_IN_USE");
                logger.log("ERROR: Логин уже занят: " + login);
                return;
            }

            boolean added = adminRepository.addAdmin(login, password, "admin");
            out.println(added ? "SUCCESS" : "FAILURE");
            logger.log(added
                    ? "INFO: Администратор '" + login + "' успешно добавлен."
                    : "ERROR: Не удалось добавить администратора '" + login + "'.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при добавлении администратора: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    // Удаление администратора
    public void deleteAdmin(BufferedReader in, PrintWriter out) throws IOException {
        try {
            String adminUuid = in.readLine();

            boolean deleted = adminRepository.deleteAdmin(adminUuid);
            out.println(deleted ? "SUCCESS" : "FAILURE");
            logger.log(deleted
                    ? "INFO: Администратор с UUID " + adminUuid + " успешно удалён."
                    : "ERROR: Не удалось удалить администратора с UUID " + adminUuid + ".");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при удалении администратора: " + e.getMessage());
            out.println("FAILURE");
        }
    }
}
