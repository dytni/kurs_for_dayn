package com.kurs.client.clientForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class ClientMainForm extends JFrame {
    private final ClientConnection clientConnection;
    private final String userId;

    public ClientMainForm(ClientConnection clientConnection, String userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;

        setTitle("Главное меню пользователя");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton viewProfileButton = StyleUtils.createStyledButton("Просмотр профиля");
        JButton manageTasksButton = StyleUtils.createStyledButton("Управление задачами");
        JButton logoutButton = StyleUtils.createStyledButton("Выйти");

        viewProfileButton.addActionListener(e -> viewProfile());
        manageTasksButton.addActionListener(e -> manageTasks());
        logoutButton.addActionListener(e -> logout());

        add(viewProfileButton);
        add(manageTasksButton);
        add(logoutButton);

        setVisible(true);
    }

    private void viewProfile() {
        clientConnection.send("VIEW_PROFILE");
        clientConnection.send(userId);

        String response = clientConnection.receive();
        if ("FAILURE".equals(response)) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки профиля.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Ваш профиль:\n" + response, "Профиль", JOptionPane.INFORMATION_MESSAGE);
    }

    private void manageTasks() {
        clientConnection.send("VIEW_TASKS");
        clientConnection.send(userId);

        int taskCount;
        try {
            taskCount = Integer.parseInt(clientConnection.receive());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки задач.");
            return;
        }

        if (taskCount == 0) {
            JOptionPane.showMessageDialog(this, "У вас нет активных задач.");
            return;
        }

        StringBuilder tasks = new StringBuilder("Ваши задачи:\n");
        for (int i = 0; i < taskCount; i++) {
            tasks.append(clientConnection.receive()).append("\n");
        }

        JOptionPane.showMessageDialog(this, tasks.toString(), "Задачи", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        dispose();
        JOptionPane.showMessageDialog(null, "Вы успешно вышли.");
    }
}
