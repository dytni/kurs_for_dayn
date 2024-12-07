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
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton manageClientsButton = new JButton("Управление клиентами");
        JButton manageProductsButton = new JButton("Управление продуктами");
        JButton logoutButton = new JButton("Выйти");

        manageClientsButton.addActionListener(e -> {
            dispose();
            new ClientCrudForm(clientConnection, userId).setVisible(true);
        });

        manageProductsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Раздел пока не доступен."));

        logoutButton.addActionListener(e -> {
            dispose();
            JOptionPane.showMessageDialog(this, "Вы успешно вышли.");
        });

        add(manageClientsButton);
        add(manageProductsButton);
        add(logoutButton);

        setVisible(true);
    }
}
