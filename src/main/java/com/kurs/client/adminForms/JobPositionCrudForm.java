package com.kurs.client.adminForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class JobPositionCrudForm extends JFrame {
    private final ClientConnection clientConnection;
    private final JTable jobTable;
    private final DefaultTableModel tableModel;
    private final String userID;

    public JobPositionCrudForm(ClientConnection clientConnection, String userID) {
        this.clientConnection = clientConnection;
        this.userID = userID;

        setTitle("Управление должностями");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Таблица для отображения данных
        tableModel = new DefaultTableModel(new String[]{"ID", "Название должности", "Оплата за час"}, 0);
        jobTable = new JTable(tableModel);
        jobTable.setSelectionBackground(StyleUtils.SELECTION_BG_COLOR);
        jobTable.setSelectionForeground(StyleUtils.SELECTION_TEXT_COLOR);

        JPanel buttonPanel = createButtonPanel();

        add(new JScrollPane(jobTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadJobPositions();
        setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton addButton = StyleUtils.createStyledButton("Добавить");
        JButton updateButton = StyleUtils.createStyledButton("Изменить");
        JButton deleteButton = StyleUtils.createStyledButton("Удалить");
        JButton backButton = StyleUtils.createStyledButton("Назад");

        addButton.addActionListener(e -> addJobPosition());
        updateButton.addActionListener(e -> updateJobPosition());
        deleteButton.addActionListener(e -> deleteJobPosition());
        backButton.addActionListener(e ->{
            dispose();
            new AdminMainForm(clientConnection, userID).show();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void loadJobPositions() {
        tableModel.setRowCount(0); // Очищаем таблицу
        clientConnection.send("VIEW_JOB_POSITIONS");

        try {
            int jobCount = Integer.parseInt(clientConnection.receive());
            for (int i = 0; i < jobCount; i++) {
                String jobData = clientConnection.receive();
                String[] jobInfo = jobData.split(","); // Предполагается формат: ID,Name,HourlyRate
                tableModel.addRow(jobInfo);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки списка должностей!");
        }
    }

    private void addJobPosition() {
        JTextField nameField = StyleUtils.createStyledTextField();
        JTextField rateField = StyleUtils.createStyledTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Название должности:"));
        panel.add(nameField);
        panel.add(new JLabel("Оплата за час:"));
        panel.add(rateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Добавить должность", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String rate = rateField.getText();

            if (name.isEmpty() || rate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!");
                return;
            }

            try {
                Double.parseDouble(rate); // Проверяем, что оплата корректное число
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Введите корректное значение оплаты!");
                return;
            }

            clientConnection.send("ADD_JOB_POSITION");
            clientConnection.send(name);
            clientConnection.send(rate);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Должность успешно добавлена!");
                loadJobPositions();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении должности!");
            }
        }
    }

    private void updateJobPosition() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите должность для изменения!");
            return;
        }

        String jobId = tableModel.getValueAt(selectedRow, 0).toString();
        String currentName = tableModel.getValueAt(selectedRow, 1).toString();
        String currentRate = tableModel.getValueAt(selectedRow, 2).toString();

        JTextField nameField = StyleUtils.createStyledTextField();
        nameField.setText(currentName);
        JTextField rateField = StyleUtils.createStyledTextField();
        rateField.setText(currentRate);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Название должности:"));
        panel.add(nameField);
        panel.add(new JLabel("Оплата за час:"));
        panel.add(rateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Изменить должность", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText();
            String newRate = rateField.getText();

            if (newName.isEmpty() || newRate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!");
                return;
            }

            try {
                Double.parseDouble(newRate); // Проверяем, что оплата корректное число
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Введите корректное значение оплаты!");
                return;
            }

            clientConnection.send("UPDATE_JOB_POSITION");
            clientConnection.send(jobId);
            clientConnection.send(newName);
            clientConnection.send(newRate);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Должность успешно обновлена!");
                loadJobPositions();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении должности!");
            }
        }
    }

    private void deleteJobPosition() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите должность для удаления!");
            return;
        }

        String jobId = tableModel.getValueAt(selectedRow, 0).toString();

        int confirmation = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить эту должность?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            clientConnection.send("DELETE_JOB_POSITION");
            clientConnection.send(jobId);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Должность успешно удалена!");
                loadJobPositions();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении должности!");
            }
        }
    }
}
