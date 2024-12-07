package com.kurs.server;

import com.kurs.server.repository.DatabaseManager;
import com.kurs.server.service.*;
import com.kurs.server.util.ServerLogger;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final LoginService loginService;
    private final DatabaseManager dbManager;
    private final ServerLogger logger;

    public ClientHandler(Socket clientSocket, LoginService loginService, DatabaseManager dbManager, ServerLogger logger) {
        this.clientSocket = clientSocket;
        this.loginService = loginService;
        this.dbManager = dbManager;
        this.logger = logger;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            ClientService clientService = new ClientService(logger, dbManager);

            String command;
            while ((command = in.readLine()) != null) {
                logger.log("Получена команда от клиента: " + command);

                switch (command.toUpperCase()) {
                    // -------------------- Аутентификация --------------------
                    case "REGISTER":
                        String username = in.readLine();
                        String password = in.readLine();
                        boolean registered = loginService.registerUser(username, password, "client");
                        out.println(registered ? "SUCCESS" : "FAILURE");
                        break;

                    case "LOGIN":
                        username = in.readLine();
                        password = in.readLine();
                        String id = loginService.authenticateUser(username, password);
                        String role = dbManager.getUserRole(username);
                        if (role != null) {
                            out.println("SUCCESS");
                            out.println(id);
                            out.println(role);
                        } else {
                            out.println("FAILURE");
                        }
                        break;

                    // -------------------- Клиенты --------------------
                    case "VIEW_CLIENTS":
                        clientService.getClients(out);
                        break;

                    case "ADD_CLIENT":
                        clientService.addClient(in, out);
                        break;

                    case "UPDATE_CLIENT":
                        clientService.updateClient(in, out);
                        break;

                    case "DELETE_CLIENT":
                        clientService.deleteClient(in, out);
                        break;

                    case "ADD_HOURS":
                        clientService.addHoursToClient(in, out);
                        break;

                    // -------------------- Неизвестная команда --------------------
                    default:
                        logger.log("Неизвестная команда от клиента: " + command);
                        out.println("UNKNOWN_COMMAND");
                        break;
                }
            }
        } catch (IOException e) {
            logger.log("Ошибка связи с клиентом: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                logger.log("Клиент отключился");
            } catch (IOException e) {
                logger.log("Ошибка при закрытии соединения с клиентом: " + e.getMessage());
            }
        }
    }
}
