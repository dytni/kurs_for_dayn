package com.kurs.client.adminForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminCrudForm extends JFrame {
    private final ClientConnection clientConnection;
    private final JTable adminTable;
    private final DefaultTableModel tableModel;
    private final String userId;

    public AdminCrudForm(ClientConnection clientConnection, String userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;

        setTitle("Управление администраторами");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Таблица для отображения администраторов
        tableModel = new DefaultTableModel(new String[]{"UUID", "Логин", "Роль"}, 0);
        adminTable = new JTable(tableModel);
        adminTable.setSelectionBackground(StyleUtils.SELECTION_BG_COLOR);
        adminTable.setSelectionForeground(StyleUtils.SELECTION_TEXT_COLOR);

        JPanel buttonPanel = createButtonPanel();

        add(new JScrollPane(adminTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadAdmins();
        setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton addAdminButton = StyleUtils.createStyledButton("Добавить администратора");
        JButton deleteAdminButton = StyleUtils.createStyledButton("Удалить администратора");
        JButton refreshButton = StyleUtils.createStyledButton("Обновить список");
        JButton backButton = StyleUtils.createStyledButton("Назад");

        addAdminButton.addActionListener(e -> addAdmin());
        deleteAdminButton.addActionListener(e -> deleteAdmin());
        refreshButton.addActionListener(e -> loadAdmins());
        backButton.addActionListener(e ->{
            dispose();
            new AdminMainForm(clientConnection, userId).setVisible(true);
        });

        buttonPanel.add(addAdminButton);
        buttonPanel.add(deleteAdminButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void loadAdmins() {
        tableModel.setRowCount(0); // Очищаем таблицу
        clientConnection.send("VIEW_ADMINS");

        try {
            int adminCount = Integer.parseInt(clientConnection.receive());
            for (int i = 0; i < adminCount; i++) {
                String adminData = clientConnection.receive();
                String[] adminInfo = adminData.split(","); // Предполагается формат: UUID,Login,Role
                tableModel.addRow(adminInfo);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки списка администраторов!");
        }
    }

    private void addAdmin() {
        JTextField loginField = StyleUtils.createStyledTextField();
        JPasswordField passwordField = StyleUtils.createStyledPasswordField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Логин:"));
        panel.add(loginField);
        panel.add(new JLabel("Пароль:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Добавить администратора", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());

            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!");
                return;
            }

            clientConnection.send("ADD_ADMIN");
            clientConnection.send(login);
            clientConnection.send(password);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Администратор успешно добавлен!");
                loadAdmins();
            } else if ("LOGIN_IN_USE".equals(response)) {
                JOptionPane.showMessageDialog(this, "Логин уже занят!");
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении администратора!");
            }
        }
    }

    private void deleteAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите администратора для удаления!");
            return;
        }

        String adminUuid = tableModel.getValueAt(selectedRow, 0).toString();

        int confirmation = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить этого администратора?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            clientConnection.send("DELETE_ADMIN");
            clientConnection.send(adminUuid);

            String response = clientConnection.receive();
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Администратор успешно удалён!");
                loadAdmins();
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении администратора!");
            }
        }
    }
}
