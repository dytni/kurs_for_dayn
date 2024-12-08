package com.kurs.client.adminForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class AdminMainForm extends JFrame {
    private final ClientConnection clientConnection;
    private final String userId;

    public AdminMainForm(ClientConnection clientConnection, String userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;

        setTitle("Главное меню администратора");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        // Кнопки меню
        JButton manageClientsButton = StyleUtils.createStyledButton("Управление клиентами");
        JButton manageAdminsButton = StyleUtils.createStyledButton("Управление администраторами");
        JButton manageJobPositionsButton = StyleUtils.createStyledButton("Управление должностями");
        JButton stats = StyleUtils.createStyledButton("Статистика");
        JButton logoutButton = StyleUtils.createStyledButton("Выйти");

        // Слушатели событий для кнопок
        manageClientsButton.addActionListener(e -> {
            dispose();
            new ClientCrudForm(clientConnection, userId).setVisible(true);
        });

        manageAdminsButton.addActionListener(e -> {
            dispose();
            new AdminCrudForm(clientConnection, userId).setVisible(true);
        });

        manageJobPositionsButton.addActionListener(e -> {
            dispose();
            new JobPositionCrudForm(clientConnection, userId).setVisible(true);
        });

        logoutButton.addActionListener(e -> {
            dispose();
            JOptionPane.showMessageDialog(this, "Вы успешно вышли.");
        });
        stats.addActionListener(e -> {
            dispose();
            new JobStatisticsForm(clientConnection, userId).setVisible(true);
        });

        // Добавляем кнопки на форму
        add(manageClientsButton);
        add(manageAdminsButton);
        add(manageJobPositionsButton);
        add(stats);
        add(logoutButton);

        setVisible(true);
    }
}
