package com.kurs.server.service;

import com.kurs.server.repository.ClientRepository;
import com.kurs.server.repository.DatabaseManager;
import com.kurs.server.util.SalaryData;
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

    public void getSalary(BufferedReader in, PrintWriter out) {
        try {
            // Читаем UUID клиента
            String clientUuid = in.readLine();

            // Получаем данные о зарплате из репозитория
            SalaryData salaryData = clientRepository.getSalary(clientUuid);

            if (salaryData == null) {
                out.println("NO_DATA");
                return;
            }

            if (salaryData.positionName() == null) {
                out.println("NO_JOB");
                return;
            }else{
                out.println(salaryData.positionName());
            }
            out.println(salaryData.hourlyRate());
            out.println(salaryData.totalHours());
        } catch (Exception e) {
            e.printStackTrace();
            out.println("FAILURE");
        }
    }

    // Получение списка всех клиентов
    public void getClients(PrintWriter out) {
        try {
            List<String> clients = clientRepository.getAllClients();
            out.println(clients.size());
            for (String client : clients) {
                out.println(client);

                List<String> jobs = clientRepository.getJobsByClient(client.split(",")[0]);
                StringBuilder res = new StringBuilder();
                for (String job : jobs) {
                    res.append(job);
                }
                out.println(res.toString());
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

    public void assignJobToClient(BufferedReader in, PrintWriter out) {
        try {
            String clientId = in.readLine();
            int jobId = Integer.parseInt(in.readLine());

            boolean success = clientRepository.assignJobToClient(clientId, jobId);
            out.println(success ? "SUCCESS" : "FAILURE");
            logger.log(success
                    ? "INFO: Должность ID " + jobId + " успешно назначена клиенту с UUID " + clientId + "."
                    : "ERROR: Ошибка при назначении должности клиенту с UUID " + clientId + ".");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при назначении должности: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    public void getJobsByClient(BufferedReader in, PrintWriter out) {

            try {
                String clientUuid = in.readLine();
                List<String> jobs = clientRepository.getJobsByClient(clientUuid);
                out.println(jobs.size());
                for (String job : jobs) {
                    out.println(job); // Формат: "ID: Name, Rate"
                }
                logger.log("INFO: Список должностей для клиента с UUID " + clientUuid + " успешно отправлен.");
            } catch (Exception e) {
                logger.log("ERROR: Ошибка при получении списка должностей клиента: " + e.getMessage());
                out.println("FAILURE");
            }
        }


    public void removeJob(BufferedReader in, PrintWriter out) {
        try {
            // Читаем UUID клиента из входного потока
            String clientUuid = in.readLine();

            // Читаем название должности из входного потока
            String jobName = in.readLine();

            // Получаем ID должности по названию через репозиторий
            int jobId = clientRepository.getJobIdByName(jobName.trim());
            if (jobId == -1) {
                out.println("FAILURE");
                logger.log("ERROR: Должность с названием '" + jobName + "' не найдена.");
                return;
            }

            // Удаляем должность у клиента
            boolean success = clientRepository.removeJobFromClient(clientUuid, jobId);
            out.println(success ? "SUCCESS" : "FAILURE");
            logger.log(success
                    ? "INFO: Должность '" + jobName + "' успешно удалена у клиента с UUID " + clientUuid + "."
                    : "ERROR: Не удалось удалить должность '" + jobName + "' у клиента с UUID " + clientUuid + ".");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при удалении должности: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    public void viewProfile(BufferedReader in, PrintWriter out) {
        try {
            // Чтение UUID клиента из входного потока
            String clientUuid = in.readLine();

            // Получение информации о клиенте из репозитория
            int hours = clientRepository.getHours(clientUuid);
            List<String> jobName = clientRepository.getJobsByClient(clientUuid);

            String jobInfo;
            if (!jobName.isEmpty()) {
                jobInfo = jobName.getFirst(); // Предполагаем, что клиент может иметь одну должность
            } else {
                jobInfo = "Нет должности";
            }

            // Формируем и отправляем информацию о профиле клиента
            String profile = String.format("Часы: %d. Должность: %s",
                    hours,
                    jobInfo);

            out.println(profile);
            logger.log("INFO: Информация о профиле клиента с UUID " + clientUuid + " успешно отправлена.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при обработке профиля клиента: " + e.getMessage());
            out.println("FAILURE");
        }
    }

}
