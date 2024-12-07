package com.kurs.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRepository {
    private final Connection connection;

    public AdminRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addAdmin(String login, String password, String role) {
        String userQuery = "INSERT INTO Users (login, passw, role) VALUES (?, ?, ?) RETURNING uuid";
        String adminQuery = "INSERT INTO Admins (uuid) VALUES (?)";
        try {
            connection.setAutoCommit(false); // Начало транзакции

            String generatedUuid;

            // Добавление пользователя и получение сгенерированного UUID
            try (PreparedStatement userStmt = connection.prepareStatement(userQuery)) {
                userStmt.setString(1, login);
                userStmt.setString(2, password);
                userStmt.setString(3, role);

                try (ResultSet rs = userStmt.executeQuery()) {
                    if (rs.next()) {
                        generatedUuid = rs.getString("uuid");
                    } else {
                        throw new SQLException("Ошибка при получении UUID для администратора.");
                    }
                }
            }

            // Добавление администратора
            try (PreparedStatement adminStmt = connection.prepareStatement(adminQuery)) {
                adminStmt.setString(1, generatedUuid);
                adminStmt.executeUpdate();
            }

            connection.commit(); // Завершение транзакции
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Откат транзакции при ошибке
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // Сброс авто-коммита
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Удаление администратора
    public boolean deleteAdmin(String adminUuid) {
        String query = "DELETE FROM Users WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, adminUuid);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
