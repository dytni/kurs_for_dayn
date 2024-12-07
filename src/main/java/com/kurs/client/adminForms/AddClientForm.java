package com.kurs.client.adminForms;

import javax.swing.*;
import java.awt.*;
import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;

public class AddClientForm extends JFrame {
    private final ClientConnection clientConnection;
    private final ClientTablePanel clientTablePanel;

    public AddClientForm(ClientConnection clientConnection, ClientTablePanel clientTablePanel) {
        this.clientConnection = clientConnection;
        this.clientTablePanel = clientTablePanel;

        setTitle("Добавить клиента");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(clientTablePanel);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel loginLabel = StyleUtils.createStyledLabel("Логин:");
        JTextField loginField = StyleUtils.createStyledTextField();

        JLabel passwordLabel = StyleUtils.createStyledLabel("Пароль:");
        JPasswordField passwordField = StyleUtils.createStyledPasswordField();

        JLabel hoursLabel = StyleUtils.createStyledLabel("Часы:");
        JTextField hoursField = StyleUtils.createStyledTextField();

        JButton addButton = StyleUtils.createStyledButton("Добавить");
        JButton cancelButton = StyleUtils.createStyledButton("Отмена");

        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(hoursLabel);
        panel.add(hoursField);
        panel.add(addButton);
        panel.add(cancelButton);

        addButton.addActionListener(e -> {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());
            String hoursText = hoursField.getText();

            if (login.isEmpty() || password.isEmpty() || hoursText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!");
                return;
            }

            int hours;
            try {
                hours = Integer.parseInt(hoursText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректное количество часов!");
                return;
            }

            clientConnection.send("ADD_CLIENT");
            clientConnection.send(login);
            clientConnection.send(password);
            clientConnection.send(String.valueOf(hours));

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Клиент успешно добавлен!");
                clientTablePanel.loadClients();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении клиента!");
            }
        });

        cancelButton.addActionListener(e -> dispose());

        add(panel);
        setVisible(true);
    }
}

