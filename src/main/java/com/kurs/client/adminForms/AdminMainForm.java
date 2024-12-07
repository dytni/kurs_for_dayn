package com.kurs.client.adminForms;

import com.kurs.client.ClientConnection;

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
        JButton manageClientsButton = new JButton("Управление клиентами");
        JButton manageAdminsButton = new JButton("Управление администраторами");
        JButton manageJobPositionsButton = new JButton("Управление должностями");
        JButton logoutButton = new JButton("Выйти");

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

        // Добавляем кнопки на форму
        add(manageClientsButton);
        add(manageAdminsButton);
        add(manageJobPositionsButton);
        add(logoutButton);

        setVisible(true);
    }
}
