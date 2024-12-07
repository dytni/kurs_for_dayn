package com.kurs.server.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminRepository {
    private final Connection connection;

    public AdminRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean isLoginTaken(String login) {
        String query = "SELECT COUNT(*) FROM Users WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
                adminStmt.setObject(1, UUID.fromString(generatedUuid));
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

    public boolean deleteAdmin(String adminUuid) {
        String query = "DELETE FROM Users WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, UUID.fromString(adminUuid));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получение всех администраторов.
     *
     * @return Список строк с данными администраторов в формате "UUID,Login,Role".
     */
    public List<String> getAllAdmins() {
        List<String> admins = new ArrayList<>();
        String query = "SELECT u.uuid, u.login, u.role FROM Admins a JOIN Users u ON a.uuid = u.uuid";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                admins.add(
                        rs.getString("uuid") + "," +
                                rs.getString("login") + "," +
                                rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }
}
