package com.kurs.client.clientForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.loginForms.LoginForm;
import com.kurs.client.utlis.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class SalaryForm extends JFrame {
    private final ClientConnection clientConnection;
    private final String userId;

    public SalaryForm(ClientConnection clientConnection, String userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;

        setTitle("Профиль");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        // Отправляем запрос на сервер для получения профиля
        clientConnection.send("GET_SALARY");
        clientConnection.send(userId);

        String response = clientConnection.receive();
        if ("NO_DATA".equals(response)) {
            JOptionPane.showMessageDialog(this, "Данные профиля не найдены.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        } else if ("NO_JOB".equals(response)) {
            JOptionPane.showMessageDialog(this, "У вас нет назначенной должности.", "Информация", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        } else if ("FAILURE".equals(response)) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке профиля.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Разбираем ответ от сервера
        String position = response; // "Должность: <значение>"
        double hourlyRate = Double.parseDouble(clientConnection.receive()); // "Ставка: <значение>"
        int totalHours = Integer.parseInt(clientConnection.receive()); // "Часы: <значение>"

        // Рассчитываем зарплату
        double salary = hourlyRate * totalHours;

        // Создаем компоненты
        JLabel positionLabel = StyleUtils.createStyledLabel("Должность:");
        JLabel positionValue = StyleUtils.createStyledLabel(position);

        JLabel hourlyRateLabel = StyleUtils.createStyledLabel("Ставка (в час):");
        JLabel hourlyRateValue = StyleUtils.createStyledLabel(String.format("%.2f", hourlyRate));

        JLabel totalHoursLabel = StyleUtils.createStyledLabel("Часы:");
        JLabel totalHoursValue = StyleUtils.createStyledLabel(String.valueOf(totalHours));

        JLabel salaryLabel = StyleUtils.createStyledLabel("Зарплата:");
        JLabel salaryValue = StyleUtils.createStyledLabel(String.format("%.2f", salary));

        JButton closeButton = StyleUtils.createStyledButton("Закрыть");
        closeButton.addActionListener(e ->{
            dispose();
            new ClientMainForm(clientConnection, userId);
        });

        // Добавляем компоненты на панель
        add(positionLabel);
        add(positionValue);
        add(hourlyRateLabel);
        add(hourlyRateValue);
        add(totalHoursLabel);
        add(totalHoursValue);
        add(salaryLabel);
        add(salaryValue);
        add(new JLabel()); // Пустой элемент для выравнивания
        add(closeButton);

        setVisible(true);
    }
}
