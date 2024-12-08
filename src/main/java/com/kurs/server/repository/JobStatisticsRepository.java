package com.kurs.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JobStatisticsRepository {
    private final Connection connection;

    public JobStatisticsRepository() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    // Получение количества клиентов по должностям
    public Map<String, Integer> getClientCountByJob() {
        String query = """
            SELECT jp.position_name, COUNT(cj.client_uuid) AS client_count
            FROM JobPosition jp
            LEFT JOIN ClientJob cj ON jp.id = cj.job_id
            GROUP BY jp.position_name;
        """;
        Map<String, Integer> statistics = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statistics.put(rs.getString("position_name"), rs.getInt("client_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statistics;
    }

    // Получение общей суммы часов по должностям
    public Map<String, Integer> getTotalHoursByJob() {
        String query = """
            SELECT jp.position_name, SUM(c.total_hours) AS total_hours
            FROM JobPosition jp
            LEFT JOIN ClientJob cj ON jp.id = cj.job_id
            LEFT JOIN Clients c ON cj.client_uuid = c.uuid
            GROUP BY jp.position_name;
        """;
        Map<String, Integer> statistics = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statistics.put(rs.getString("position_name"), rs.getInt("total_hours"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statistics;
    }
}
