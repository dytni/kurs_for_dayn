package com.kurs.server.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobPositionRepository {
    private final Connection connection;

    public JobPositionRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Добавление новой должности
    public boolean addJobPosition(String positionName, double hourlyRate) {
        String query = "INSERT INTO JobPosition (position_name, hourly_rate) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, positionName);
            stmt.setDouble(2, hourlyRate);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получение всех должностей
    public List<String> getAllJobPositions() {
        String query = "SELECT id, position_name, hourly_rate FROM JobPosition";
        List<String> jobPositions = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                jobPositions.add(
                        String.format("%d,%s,%.2f",
                                rs.getInt("id"),
                                rs.getString("position_name"),
                                rs.getDouble("hourly_rate"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobPositions;
    }

    // Обновление данных должности
    public boolean updateJobPosition(int id, String positionName, double hourlyRate) {
        String query = "UPDATE JobPosition SET position_name = ?, hourly_rate = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, positionName);
            stmt.setDouble(2, hourlyRate);
            stmt.setInt(3, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Проверка, связана ли должность с клиентами
    public boolean isJobPositionLinked(int jobId) {
        String query = "SELECT COUNT(*) FROM ClientJob WHERE job_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Если есть записи, должность связана с клиентами
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Удаление должности
    public boolean deleteJobPosition(int id) {
        if (isJobPositionLinked(id)) {
            System.out.println("Должность связана с клиентами и не может быть удалена.");
            return false;
        }

        String query = "DELETE FROM JobPosition WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
