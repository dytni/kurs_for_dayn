package com.kurs.server.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ClientRepository clientRepository;
    public DatabaseManager() {
        clientRepository = new ClientRepository();
        adminRepository = new AdminRepository();
        userRepository = new UserRepository(adminRepository, clientRepository);

    } public boolean isLoginTaken(String login) {
       return userRepository.isLoginTaken(login);
    }

    public String authenticateUserWithHash(String login, String hashedPassword) {
        return userRepository.authenticateUserWithHash(login, hashedPassword);
    }

    public String getUserRole(String login) {
       return userRepository.getUserRole(login);
    }

    public List<String> getAllUsers() {
       return userRepository.getAllUsers();
    }

    public boolean addClient(String login, String password, int totalHours, String role) {
        return clientRepository.addClient(login,password,totalHours,role);
    }

    public boolean addAdmin(String login, String password, String role) {
        return adminRepository.addAdmin(login, password, role);
    }

    // Обновление данных клиента
    public boolean updateClient(String clientUuid, int totalHours) {
        return clientRepository.updateClient(clientUuid,totalHours);
    }

    // Удаление клиента
    public boolean deleteClient(String clientUuid) {
        return clientRepository.deleteClient(clientUuid);
    }

    // Получение всех клиентов
    public List<String> getAllClients() {
        return clientRepository.getAllClients();
    }

    // Присвоение должности клиенту
    public boolean assignJobToClient(String clientUuid, int jobId) {
       return clientRepository.assignJobToClient(clientUuid, jobId);
    }

    // Добавление времени клиенту
    public boolean addHoursToClient(String clientUuid, int hours) {
       return clientRepository.addHoursToClient(clientUuid,hours);
    }

    // Получение клиента по UUID
    public String getClientByUuid(String clientUuid) {
       return clientRepository.getClientByUuid(clientUuid);
    }

    public void closeConnection() {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        databaseConnection.closeConnection();
    }
}
