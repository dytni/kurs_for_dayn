package com.kurs.client.adminForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientTablePanel extends JPanel {
    private final ClientConnection clientConnection;
    private final JTable clientTable;
    private final DefaultTableModel tableModel;

    public ClientTablePanel(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        setLayout(new BorderLayout());

        // Создаем таблицу и подключаем модель
        tableModel = new DefaultTableModel(new String[]{"UUID", "Логин", "Часы"}, 0);
        clientTable = new JTable(tableModel);
        clientTable.setSelectionBackground(StyleUtils.SELECTION_BG_COLOR);
        clientTable.setSelectionForeground(StyleUtils.SELECTION_TEXT_COLOR);

        // Добавляем таблицу в панель
        JScrollPane scrollPane = new JScrollPane(clientTable);
        add(scrollPane, BorderLayout.CENTER);

        // Загружаем данные клиентов
        loadClients();
    }

    public JTable getClientTable() {
        return clientTable;
    }

    public void loadClients() {
        tableModel.setRowCount(0); // Очищаем таблицу
        clientConnection.send("VIEW_CLIENTS");

        int clientCount = Integer.parseInt(clientConnection.receive());
        for (int i = 0; i < clientCount; i++) {
            String clientData = clientConnection.receive();
            String[] clientInfo = clientData.split(",");
            tableModel.addRow(clientInfo);
        }
    }
}
