package com.kurs.server.service;

import com.kurs.server.repository.JobStatisticsRepository;
import com.kurs.server.util.ServerLogger;

import java.io.PrintWriter;
import java.util.Map;

public class JobStatisticsService {
    private final JobStatisticsRepository statisticsRepository;
    private final ServerLogger logger;

    public JobStatisticsService(JobStatisticsRepository statisticsRepository, ServerLogger logger) {
        this.statisticsRepository = statisticsRepository;
        this.logger = logger;
    }

    public void getClientCountByJob(PrintWriter out) {
        try {
            Map<String, Integer> stats = statisticsRepository.getClientCountByJob();
            out.println(stats.size());
            stats.forEach((position, count) -> out.println(position + "," + count));
            logger.log("INFO: Статистика количества клиентов по должностям отправлена клиенту.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при получении статистики клиентов по должностям: " + e.getMessage());
            out.println("FAILURE");
        }
    }

    public void getTotalHoursByJob(PrintWriter out) {
        try {
            Map<String, Integer> stats = statisticsRepository.getTotalHoursByJob();
            out.println(stats.size());
            stats.forEach((position, hours) -> out.println(position + "," + hours));
            logger.log("INFO: Статистика суммарных часов по должностям отправлена клиенту.");
        } catch (Exception e) {
            logger.log("ERROR: Ошибка при получении статистики часов по должностям: " + e.getMessage());
            out.println("FAILURE");
        }
    }
}
