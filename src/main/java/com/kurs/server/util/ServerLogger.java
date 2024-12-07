package com.kurs.server.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLogger {

    private final PrintWriter writer;

    public ServerLogger(String logFilePath) {
        try {
            writer = new PrintWriter(new FileWriter(logFilePath, true), true); // true для добавления в файл
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при инициализации логгера", e);
        }
    }

    public synchronized void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        writer.println("[" + timestamp + "] - " + message);
    }

    public void close() {
        writer.close();
    }
}
