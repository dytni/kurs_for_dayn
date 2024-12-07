package com.kurs;


import com.kurs.client.Client;
import com.kurs.server.Server;

public class Main {
    public static void main(String[] args) {
        Thread serverThread = new Thread(() -> {
            try {
                System.out.println("Запуск сервера...");
                Server.main(new String[]{}); // Запуск метода main из Server
            } catch (Exception e) {
                System.err.println("Ошибка при запуске сервера: " + e.getMessage());
            }
        });
        serverThread.start();

        try {
            Thread.sleep(2000); // Задержка 5 секунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Запуск клиента
        System.out.println("Запуск клиента...");
        Client.main(new String[]{}); // Запуск метода main из ClientApp
    }
}
