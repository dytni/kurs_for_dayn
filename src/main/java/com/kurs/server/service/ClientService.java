package com.kurs.server.service;

import com.kurs.server.repository.ClientRepository;
import com.kurs.server.repository.DatabaseManager;
import com.kurs.server.util.ServerLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ClientService {
    private final ServerLogger logger;
    private final DatabaseManager clientRepository;

    public ClientService(ServerLogger logger, DatabaseManager clientRepository) {
        this.logger = logger;
        this.clientRepository = clientRepository;
    }

    // Получение списка всех клиентов
    public void getClients(PrintWriter out) {
        try {
            List<String> clients = clientRepository.getAllClients();
            out.println(clients.size());
            for (String client : clients) {
                out.println(client);
            }
            logger.log("INFO: Список клиентов успешно отправлен администратору.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при получении списка клиентов: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    // Добавление нового клиента
    public void addClient(BufferedReader in, PrintWriter out) throws IOException {
        try {
            String login = in.readLine();
            String password = in.readLine();
            int totalHours = Integer.parseInt(in.readLine());
            String role = "client";

            boolean added = clientRepository.addClient(login, password, totalHours, role);
            out.println(added ? "SUCCESS" : "FAILURE");
            logger.log(added
                    ? "INFO: Клиент с логином '" + login + "' успешно добавлен."
                    : "ERROR: Не удалось добавить клиента с логином '" + login + "'.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при добавлении клиента: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    // Обновление данных клиента
    public void updateClient(BufferedReader in, PrintWriter out) throws IOException {
        try {
            String uuid = in.readLine();
            int totalHours = Integer.parseInt(in.readLine());

            boolean updated = clientRepository.updateClient(uuid, totalHours);
            out.println(updated ? "SUCCESS" : "FAILURE");
            logger.log(updated
                    ? "INFO: Данные клиента с UUID '" + uuid + "' успешно обновлены."
                    : "ERROR: Не удалось обновить данные клиента с UUID '" + uuid + "'.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при обновлении данных клиента: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    // Удаление клиента
    public void deleteClient(BufferedReader in, PrintWriter out) throws IOException {
        try {
            String uuid = in.readLine();
            boolean deleted = clientRepository.deleteClient(uuid);
            out.println(deleted ? "SUCCESS" : "FAILURE");
            logger.log(deleted
                    ? "INFO: Клиент с UUID '" + uuid + "' успешно удалён."
                    : "ERROR: Не удалось удалить клиента с UUID '" + uuid + "'.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при удалении клиента: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    // Добавление часов клиенту
    public void addHoursToClient(BufferedReader in, PrintWriter out) throws IOException {
        try {
            String uuid = in.readLine();
            int hours = Integer.parseInt(in.readLine());

            boolean updated = clientRepository.addHoursToClient(uuid, hours);
            out.println(updated ? "SUCCESS" : "FAILURE");
            logger.log(updated
                    ? "INFO: Клиенту с UUID '" + uuid + "' установленно " + hours + " часов."
                    : "ERROR: Не удалось добавить часы клиенту с UUID '" + uuid + "'.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при добавлении часов клиенту: " + e.getMessage());
            out.println("FAILURE");
        }
    }
}
