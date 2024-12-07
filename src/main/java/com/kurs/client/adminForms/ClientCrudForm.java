package com.kurs.client.adminForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class ClientCrudForm  extends JFrame {
    private final ClientConnection clientConnection;
    private final ClientTablePanel clientTablePanel;
    private final String userId;

    public ClientCrudForm(ClientConnection clientConnection, String userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;

        setTitle("Управление клиентами");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        clientTablePanel = new ClientTablePanel(clientConnection);
        JPanel buttonPanel = createButtonPanel();

        add(clientTablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton addClientButton = StyleUtils.createStyledButton("Добавить клиента");
        JButton deleteClientButton = StyleUtils.createStyledButton("Удалить клиента");
        JButton addHoursButton = StyleUtils.createStyledButton("Добавить часы");
        JButton backButton = StyleUtils.createStyledButton("Назад");

        addClientButton.addActionListener(e -> new AddClientForm(clientConnection, clientTablePanel));
        deleteClientButton.addActionListener(e -> deleteClient());
        addHoursButton.addActionListener(e -> addHoursToClient());
        backButton.addActionListener(e -> {
            dispose();
            new AdminMainForm(clientConnection, userId).show();
        });

        buttonPanel.add(addClientButton);
        buttonPanel.add(deleteClientButton);
        buttonPanel.add(addHoursButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }


    private void deleteClient() {
        JTable clientTable = clientTablePanel.getClientTable();
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите клиента для удаления!");
            return;
        }

        String clientId = clientTable.getValueAt(selectedRow, 0).toString();

        int confirmation = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить клиента?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            clientConnection.send("DELETE_CLIENT");
            clientConnection.send(clientId);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Клиент успешно удален!");
                clientTablePanel.loadClients();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка удаления клиента!");
            }
        }
    }

    private void addHoursToClient() {
        JTable clientTable = clientTablePanel.getClientTable();
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите клиента для добавления часов!");
            return;
        }

        String clientId = clientTable.getValueAt(selectedRow, 0).toString();
        String input = JOptionPane.showInputDialog(this, "Введите количество часов:");
        if (input == null || input.isEmpty()) {
            return;
        }

        int hoursToAdd;
        try {
            hoursToAdd = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите корректное количество часов!");
            return;
        }

        clientConnection.send("ADD_HOURS");
        clientConnection.send(clientId);
        clientConnection.send(String.valueOf(hoursToAdd));

        String response = clientConnection.receive();
        if ("SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(this, "Часы успешно добавлены клиенту!");
            clientTablePanel.loadClients();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении часов!");
        }
    }
}
