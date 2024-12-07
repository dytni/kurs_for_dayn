package com.kurs.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository {
    private final Connection connection;

    public UserRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
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

    public String authenticateUserWithHash(String login, String hashedPassword) {
        String query = "SELECT uuid FROM Users WHERE login = ? AND passw = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, hashedPassword);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("uuid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserRole(String login) {
        String query = "SELECT role FROM Users WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllUsers() {
        String query = "SELECT uuid, login, role FROM Users";
        List<String> users = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(
                        rs.getString("uuid") + "," +
                                rs.getString("login") + "," +
                                rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
