package com.kurs.server.repository;

import com.kurs.server.util.SalaryData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientRepository {
    private final Connection connection;

    public ClientRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    public SalaryData getSalaryDataByUuid(String clientUuid) {
        String query = """
            SELECT jp.position_name, jp.hourly_rate, c.total_hours
            FROM Clients c
            LEFT JOIN ClientJob cj ON c.uuid = cj.client_uuid
            LEFT JOIN JobPosition jp ON cj.job_id = jp.id
            WHERE c.uuid = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, UUID.fromString(clientUuid));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String positionName = rs.getString("position_name");
                    double hourlyRate = rs.getDouble("hourly_rate");
                    int totalHours = rs.getInt("total_hours");

                    return new SalaryData(positionName, hourlyRate, totalHours);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Если данных не найдено
    }

    public int getJobIdByName(String jobName) {
        String query = "SELECT id FROM JobPosition WHERE position_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, jobName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Возвращаем -1, если должность не найдена
    }


    // Добавление клиента
    public boolean addClient(String login, String password, int totalHours, String role) {
        String userQuery = "INSERT INTO Users (login, passw, role) VALUES (?, ?, ?) RETURNING uuid";
        String clientQuery = "INSERT INTO Clients (uuid, total_hours) VALUES (?, ?)";
        try {
            connection.setAutoCommit(false); // Начало транзакции

            UUID generatedUuid;
            // Добавление пользователя и получение сгенерированного UUID
            try (PreparedStatement userStmt = connection.prepareStatement(userQuery)) {
                userStmt.setString(1, login);
                userStmt.setString(2, password);
                userStmt.setString(3, role);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (rs.next()) {
                        generatedUuid = (UUID) rs.getObject("uuid");
                    } else {
                        throw new SQLException("Ошибка при получении UUID пользователя.");
                    }
                }
            }

            // Добавление клиента
            try (PreparedStatement clientStmt = connection.prepareStatement(clientQuery)) {
                clientStmt.setObject(1, generatedUuid);
                clientStmt.setInt(2, totalHours);
                clientStmt.executeUpdate();
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

    // Обновление данных клиента
    public boolean updateClient(String clientUuid, int totalHours) {
        String query = "UPDATE Clients SET total_hours = ? WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, totalHours);
            stmt.setObject(2, UUID.fromString(clientUuid));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Удаление клиента
    public boolean deleteClient(String clientUuid) {
        String deleteClientQuery = "DELETE FROM Clients WHERE uuid = ?";
        String deleteUserQuery = "DELETE FROM Users WHERE uuid = ?";
        try {
            connection.setAutoCommit(false); // Начало транзакции

            // Удаление записи из таблицы Clients
            try (PreparedStatement clientStmt = connection.prepareStatement(deleteClientQuery)) {
                clientStmt.setObject(1, UUID.fromString(clientUuid));
                clientStmt.executeUpdate();
            }

            // Удаление записи из таблицы Users
            try (PreparedStatement userStmt = connection.prepareStatement(deleteUserQuery)) {
                userStmt.setObject(1, UUID.fromString(clientUuid));
                userStmt.executeUpdate();
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
    public int getTotalHoursByUuid(String clientUuid) {
        String query = "SELECT total_hours FROM Clients WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, UUID.fromString(clientUuid));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_hours");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Возвращаем -1, если клиент не найден или произошла ошибка
    }



    // Получение всех клиентов
    public List<String> getAllClients() {
        String query = "SELECT c.uuid, u.login, c.total_hours FROM Clients c " +
                "JOIN Users u ON c.uuid = u.uuid";
        List<String> clients = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String client = String.format("%s,%s,%d",
                        rs.getString("uuid"),
                        rs.getString("login"),
                        rs.getInt("total_hours"));
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // Присвоение должности клиенту
    public boolean assignJobToClient(String clientUuid, int jobId) {
        String query = "INSERT INTO ClientJob (client_uuid, job_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, UUID.fromString(clientUuid));
            stmt.setInt(2, jobId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getJobsByClient(String clientUuid) {
        String query = "SELECT cj.job_id, jp.position_name, jp.hourly_rate " +
                "FROM ClientJob cj " +
                "JOIN JobPosition jp ON cj.job_id = jp.id " +
                "WHERE cj.client_uuid = ?";
        List<String> jobs = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, UUID.fromString(clientUuid));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(
                            rs.getString("position_name") + " "
                            );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }



    // Добавление времени клиенту
    public boolean addHoursToClient(String clientUuid, int hours) {
        String query = "UPDATE Clients SET total_hours = total_hours + ? WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, hours);
            stmt.setObject(2, UUID.fromString(clientUuid));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получение клиента по UUID
    public String getClientByUuid(String clientUuid) {
        String query = "SELECT c.uuid, u.login, c.total_hours FROM Clients c " +
                "JOIN Users u ON c.uuid = u.uuid WHERE c.uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, UUID.fromString(clientUuid));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return String.format("%s,%s,%d",
                            rs.getString("uuid"),
                            rs.getString("login"),
                            rs.getInt("total_hours"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
