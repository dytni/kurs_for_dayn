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
    private final JobPositionRepository jobPositionRepository;
    public DatabaseManager() {
        clientRepository = new ClientRepository();
        adminRepository = new AdminRepository();
        jobPositionRepository = new JobPositionRepository();
        userRepository = new UserRepository();

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
        if (isLoginTaken(login)) {
            System.out.println("Имя пользователя уже занято: " + login);
            return false;
        }
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
    public List<String> getJobsByClient(String clientUuid) {
        return clientRepository.getJobsByClient(clientUuid);
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

    public List<String> getAllJobPositions() {
        return jobPositionRepository.getAllJobPositions();
    }

    public boolean addJobPosition(String positionName, double hourlyRate) {
        return jobPositionRepository.addJobPosition(positionName,hourlyRate);
    }

    public boolean updateJobPosition(int id, String positionName, double hourlyRate) {
        return jobPositionRepository.updateJobPosition(id,positionName,hourlyRate);
    }

    public boolean deleteJobPosition(int id) {
        return jobPositionRepository.deleteJobPosition(id);
    }

    public List<String> getAllAdmins() {
        return adminRepository.getAllAdmins();
    }

    public boolean deleteAdmin(String adminUuid) {
        return adminRepository.deleteAdmin(adminUuid);
    }

    public boolean isLoginATaken(String login) {
        return adminRepository.isLoginTaken(login);
    }

    public int getJobIdByName(String jobName) {
        return clientRepository.getJobIdByName(jobName);
    }

    public boolean removeJobFromClient(String clientUuid, int jobId) {
        return userRepository.removeJobFromClient(clientUuid, jobId);
    }

    public int getHours(String clientUuid) {
        return clientRepository.getTotalHoursByUuid(clientUuid);
    }
}
