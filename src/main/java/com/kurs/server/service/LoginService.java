package com.kurs.server.service;

import com.kurs.server.repository.DatabaseManager;
import com.kurs.server.util.HashPassword;

import java.util.UUID;

public class LoginService {
    private static final String HASH_ALGORITHM = "SHA-256";
    private final DatabaseManager manager;

    public LoginService(DatabaseManager databaseManager) {
        this.manager = databaseManager;
    }

    /**
     * Регистрация пользователя с определённой ролью.
     * @param login Логин пользователя.
     * @param password Пароль пользователя.
     * @param role Роль пользователя (например, "client" или "admin").
     * @return true, если регистрация успешна; false в случае ошибки.
     */
    public boolean registerUser(String login, String password, String role) {
        if (manager.isLoginTaken(login)) {
            System.out.println("Имя пользователя уже занято: " + login);
            return false;
        }
        String hashedPassword = HashPassword.hashPassword(password);

        return switch (role.toLowerCase()) {
            case "client" ->
                    manager.addClient(login, hashedPassword, 0, role); // 0 — начальное значение total_hours
            case "admin" ->
                    manager.addAdmin(login, hashedPassword, role);
            default -> {
                System.out.println("Неверная роль: " + role);
                yield  false;
            }
        };
    }

    /**
     * Аутентификация пользователя.
     * @param login Логин пользователя.
     * @param password Пароль пользователя.
     * @return UUID пользователя, если аутентификация успешна; null в случае ошибки.
     */
    public String authenticateUser(String login, String password) {
        String hashedPassword = HashPassword.hashPassword(password);
        return manager.authenticateUserWithHash(login, hashedPassword);
    }

}
