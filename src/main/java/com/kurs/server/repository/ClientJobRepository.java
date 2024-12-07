package com.kurs.server.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientJobRepository {
    private final Connection connection;

    public ClientJobRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

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


    public boolean removeJobFromClient(String clientUuid, int jobId) {
        String query = "DELETE FROM ClientJob WHERE client_uuid = ? AND job_id = ?";
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
                    jobs.add(String.format("ID: %d, Name: %s, Rate: %.2f",
                            rs.getInt("job_id"),
                            rs.getString("position_name"),
                            rs.getDouble("hourly_rate")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobs;
    }


    public boolean removeAllJobsFromClient(String clientUuid) {
        String query = "DELETE FROM ClientJob WHERE client_uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, UUID.fromString(clientUuid));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
