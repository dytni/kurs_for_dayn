package com.kurs.server;


import com.kurs.server.repository.DatabaseConnection;
import com.kurs.server.repository.DatabaseManager;
import com.kurs.server.repository.UserRepository;
import com.kurs.server.service.LoginService;
import com.kurs.server.util.ServerLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();
        LoginService loginService = new LoginService(databaseManager);
        ServerLogger logger = new ServerLogger("server_logs.txt");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.log("Сервер запущен на порту " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.log("Новое подключение от клиента: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, loginService, databaseManager, logger);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            logger.log("Ошибка сервера: " + e.getMessage());
        } finally {
            databaseManager.closeConnection();
            logger.close();
        }
    }
}
