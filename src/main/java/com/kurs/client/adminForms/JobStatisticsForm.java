package com.kurs.client.adminForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class JobStatisticsForm extends JFrame {
    private final ClientConnection clientConnection;
    private final String uuid;

    public JobStatisticsForm(ClientConnection clientConnection, String uuid) {
        this.uuid = uuid;
        this.clientConnection = clientConnection;

        setTitle("Статистика по должностям");
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Панель для кнопок
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton clientCountButton = StyleUtils.createStyledButton("Количество клиентов");
        JButton totalHoursButton = StyleUtils.createStyledButton("Сумма часов");
        JButton backButton = StyleUtils.createStyledButton("Назад");

        // Панель для отображения графиков
        JPanel chartPanel = new JPanel(new BorderLayout());

        // Обработка кнопок
        clientCountButton.addActionListener(e -> {
            Map<String, Integer> data = getStatisticsFromServer("GET_CLIENT_COUNT_BY_JOB");
            displayBarChart(chartPanel, data, "Количество клиентов по должностям", "Должности", "Количество клиентов");
        });

        totalHoursButton.addActionListener(e -> {
            Map<String, Integer> data = getStatisticsFromServer("GET_TOTAL_HOURS_BY_JOB");
            displayPieChart(chartPanel, data, "Сумма часов по должностям");
        });

        backButton.addActionListener(e -> {
            dispose();
            new AdminMainForm(clientConnection, uuid).setVisible(true);
        });

        buttonPanel.add(clientCountButton);
        buttonPanel.add(totalHoursButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // Метод получения статистики с сервера
    private Map<String, Integer> getStatisticsFromServer(String command) {
        Map<String, Integer> data = new HashMap<>();
        clientConnection.send(command);

        int count = Integer.parseInt(clientConnection.receive());
        for (int i = 0; i < count; i++) {
            String[] parts = clientConnection.receive().split(",");
            data.put(parts[0], Integer.parseInt(parts[1]));
        }
        return data;
    }

    // Метод для отображения столбчатого графика
    private void displayBarChart(JPanel chartPanel, Map<String, Integer> data, String title, String categoryAxisLabel, String valueAxisLabel) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), entry.getKey(), "");
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset
        );

        updateChartPanel(chartPanel, barChart);
    }


    // Метод для отображения кругового графика
    private void displayPieChart(JPanel chartPanel, Map<String, Integer> data, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                title,
                dataset,
                true,
                true,
                false
        );

        updateChartPanel(chartPanel, pieChart);
    }


    // Метод для обновления панели графика
    private void updateChartPanel(JPanel chartPanel, JFreeChart chart) {
        chartPanel.removeAll();
        ChartPanel chartDisplayPanel = new ChartPanel(chart);
        chartPanel.add(chartDisplayPanel, BorderLayout.CENTER);
        chartPanel.validate();
    }
}
