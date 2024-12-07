package com.kurs.server.repository;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

        private static DatabaseConnection instance;
        private final Connection connection;

        private DatabaseConnection() {
            try {
                String url = "jdbc:postgresql://localhost:5432/andr";
                String user = "dytni";
                String password = "1331";
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Подключение к базе данных успешно установлено!");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ошибка подключения к базе данных!");
                throw new RuntimeException(e);
            }
        }

        public static synchronized DatabaseConnection getInstance() {
            if (instance == null) {
                instance = new DatabaseConnection();
            }
            return instance;
        }

        public Connection getConnection() {
            return connection;
        }

        public void closeConnection() {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Соединение с базой данных закрыто.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


