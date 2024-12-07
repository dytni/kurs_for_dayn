package com.kurs.client.adminForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ClientCrudForm extends JFrame {
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
        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton addClientButton = StyleUtils.createStyledButton("Добавить клиента");
        JButton deleteClientButton = StyleUtils.createStyledButton("Удалить клиента");
        JButton addHoursButton = StyleUtils.createStyledButton("Добавить часы");
        JButton assignJobButton = StyleUtils.createStyledButton("Назначить должность");
        JButton removeJobButton = StyleUtils.createStyledButton("Удалить должность");
        JButton backButton = StyleUtils.createStyledButton("Назад");

        addClientButton.addActionListener(e -> new AddClientForm(clientConnection, clientTablePanel));
        deleteClientButton.addActionListener(e -> deleteClient());
        addHoursButton.addActionListener(e -> addHoursToClient());
        assignJobButton.addActionListener(e -> assignJobToClient());
        removeJobButton.addActionListener(e -> removeJobFromClient());
        backButton.addActionListener(e -> {
            dispose();
            new AdminMainForm(clientConnection, userId).show();
        });

        buttonPanel.add(addClientButton);
        buttonPanel.add(deleteClientButton);
        buttonPanel.add(addHoursButton);
        buttonPanel.add(assignJobButton);
        buttonPanel.add(removeJobButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void removeJobFromClient() {
        JTable clientTable = clientTablePanel.getClientTable();
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите клиента для удаления должности!");
            return;
        }

        String clientId = clientTable.getValueAt(selectedRow, 0).toString();

        // Получение текущих должностей клиента
        clientConnection.send("GET_CLIENT_JOBS");
        clientConnection.send(clientId);

        int jobCount;
        try {
            jobCount = Integer.parseInt(clientConnection.receive());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки списка должностей клиента!");
            return;
        }

        if (jobCount == 0) {
            JOptionPane.showMessageDialog(this, "У клиента нет назначенных должностей.");
            return;
        }

        String[] jobOptions = new String[jobCount];
        for (int i = 0; i < jobCount; i++) {
            jobOptions[i] = clientConnection.receive(); // Получение названия должности
        }

        // Отображение выпадающего списка с текущими должностями
        String selectedJob = (String) JOptionPane.showInputDialog(
                this,
                "Выберите должность для удаления:",
                "Удаление должности",
                JOptionPane.QUESTION_MESSAGE,
                null,
                jobOptions,
                jobOptions[0]
        );

        if (selectedJob == null || selectedJob.isEmpty()) {
            return; // Пользователь закрыл диалог или ничего не выбрал
        }

        try {
            clientConnection.send("REMOVE_JOB");
            clientConnection.send(clientId);
            clientConnection.send(selectedJob); // Отправляем название выбранной должности

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Должность успешно удалена у клиента!");
                clientTablePanel.loadClients();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении должности!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка обработки данных должности!");
        }
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

    private void assignJobToClient() {
        JTable clientTable = clientTablePanel.getClientTable();
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите клиента для назначения должности!");
            return;
        }

        String clientId = clientTable.getValueAt(selectedRow, 0).toString();

        // Проверка на наличие должности у клиента
        clientConnection.send("GET_CLIENT_JOBS");
        clientConnection.send(clientId);

        int existingJobCount;
        try {
            existingJobCount = Integer.parseInt(clientConnection.receive());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки списка должностей клиента!");
            return;
        }

        if (existingJobCount > 0) {
            JOptionPane.showMessageDialog(this, "У клиента уже есть должность. Сначала удалите текущую должность.");
            return;
        }

        // Запрос списка всех должностей
        clientConnection.send("VIEW_JOB_POSITIONS");

        int jobCount;
        try {
            jobCount = Integer.parseInt(clientConnection.receive());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки списка должностей!");
            return;
        }

        if (jobCount == 0) {
            JOptionPane.showMessageDialog(this, "Список должностей пуст. Добавьте должности перед назначением.");
            return;
        }

        String[] jobOptions = new String[jobCount];
        String[] ids = new String[jobCount];
        for (int i = 0; i < jobCount; i++) {
            String temp = clientConnection.receive();
            jobOptions[i] = temp.split(",")[1]; // Формат: "ID: position_name"
            ids[i] = temp.split(",")[0];
        }

        // Отображение выпадающего списка с должностями
        String selectedJob = (String) JOptionPane.showInputDialog(
                this,
                "Выберите должность для клиента:",
                "Назначение должности",
                JOptionPane.QUESTION_MESSAGE,
                null,
                jobOptions,
                jobOptions[0]
        );

        if (selectedJob == null || selectedJob.isEmpty()) {
            return; // Пользователь закрыл диалог или ничего не выбрал
        }

        try {
            // Извлекаем ID должности из строки
            List<String> list = Arrays.stream(jobOptions).toList();
            String jobId = ids[list.indexOf(selectedJob)];

            clientConnection.send("ASSIGN_JOB");
            clientConnection.send(clientId);
            clientConnection.send(jobId);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Должность успешно назначена клиенту!");
                clientTablePanel.loadClients();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при назначении должности!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка обработки данных должности!");
        }
    }

}
