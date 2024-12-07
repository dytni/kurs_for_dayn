package com.kurs.server.service;

import com.kurs.server.repository.DatabaseManager;
import com.kurs.server.repository.JobPositionRepository;
import com.kurs.server.util.ServerLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class JobPositionService {
    private final DatabaseManager jobPositionRepository;
    private final ServerLogger logger;

    public JobPositionService(DatabaseManager jobPositionRepository, ServerLogger logger) {
        this.jobPositionRepository = jobPositionRepository;
        this.logger = logger;
    }

    // Получение всех должностей
    public void getAllJobPositions(PrintWriter out) {
        try {
            List<String> jobPositions = jobPositionRepository.getAllJobPositions();
            out.println(jobPositions.size());
            for (String job : jobPositions) {
                out.println(job);
            }
            logger.log("INFO: Список должностей успешно отправлен.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при получении списка должностей: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    // Добавление новой должности
    public void addJobPosition(BufferedReader in, PrintWriter out) throws IOException {
        try {
            String positionName = in.readLine();
            double hourlyRate = Double.parseDouble(in.readLine());

            boolean added = jobPositionRepository.addJobPosition(positionName, hourlyRate);
            out.println(added ? "SUCCESS" : "FAILURE");
            logger.log(added
                    ? "INFO: Должность '" + positionName + "' успешно добавлена."
                    : "ERROR: Не удалось добавить должность '" + positionName + "'.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при добавлении должности: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    // Обновление должности
    public void updateJobPosition(BufferedReader in, PrintWriter out) throws IOException {
        try {
            int id = Integer.parseInt(in.readLine());
            String positionName = in.readLine();
            double hourlyRate = Double.parseDouble(in.readLine());

            boolean updated = jobPositionRepository.updateJobPosition(id, positionName, hourlyRate);
            out.println(updated ? "SUCCESS" : "FAILURE");
            logger.log(updated
                    ? "INFO: Должность ID " + id + " успешно обновлена."
                    : "ERROR: Не удалось обновить должность ID " + id + ".");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при обновлении должности: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    // Удаление должности
    public void deleteJobPosition(BufferedReader in, PrintWriter out) throws IOException {
        try {
            int id = Integer.parseInt(in.readLine());

            boolean deleted = jobPositionRepository.deleteJobPosition(id);
            out.println(deleted ? "SUCCESS" : "FAILURE");
            logger.log(deleted
                    ? "INFO: Должность ID " + id + " успешно удалена."
                    : "ERROR: Не удалось удалить должность ID " + id + ".");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при удалении должности: " + e.getMessage());
            out.println("FAILURE");
        }
    }
}
