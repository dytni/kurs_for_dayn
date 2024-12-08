package com.kurs.client.clientForms;

import com.kurs.client.ClientConnection;
import com.kurs.client.utlis.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class ClientMainForm extends JFrame {
    private final ClientConnection clientConnection;
    private final String userId;

    public ClientMainForm(ClientConnection clientConnection, String userId) {
        this.clientConnection = clientConnection;
        this.userId = userId;

        setTitle("Главное меню пользователя");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton viewProfileButton = StyleUtils.createStyledButton("Просмотр профиля");
        JButton manageTasksButton = StyleUtils.createStyledButton("Расчетник");
        JButton logoutButton = StyleUtils.createStyledButton("Выйти");

        viewProfileButton.addActionListener(e -> viewProfile());
        manageTasksButton.addActionListener(e ->{
            dispose();
            new SalaryForm(clientConnection, userId);
        });
        logoutButton.addActionListener(e -> logout());

        add(viewProfileButton);
        add(manageTasksButton);
        add(logoutButton);

        setVisible(true);
    }

    private void viewProfile() {
        clientConnection.send("VIEW_PROFILE");
        clientConnection.send(userId);

        String response = clientConnection.receive();
        if ("FAILURE".equals(response)) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки профиля.");
            return;
        }

        // Создаём новое окно для отображения профиля
        JFrame profileFrame = new JFrame("Ваш профиль");
        profileFrame.setSize(400, 300);
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setLocationRelativeTo(this);
        profileFrame.setLayout(new GridLayout(3, 2, 10, 10));

        // Разделяем ответ на строки
        String[] profileData = response.split("\\.");
        String totalHours = profileData[0].split(":")[1].trim(); // "Часы: <значение>"
        String jobInfo = profileData[1].split(":")[1].trim();    // "Должность: <значение>"

        // Создаём компоненты для отображения данных
        JLabel hoursLabel = new JLabel("Часы:");
        JLabel hoursValue = new JLabel(totalHours);
        JLabel jobLabel = new JLabel("Должность:");
        JLabel jobValue = new JLabel(jobInfo);

        // Настраиваем стиль (если требуется)
        hoursLabel.setFont(new Font("Arial", Font.BOLD, 14));
        hoursValue.setFont(new Font("Arial", Font.PLAIN, 14));
        jobLabel.setFont(new Font("Arial", Font.BOLD, 14));
        jobValue.setFont(new Font("Arial", Font.PLAIN, 14));

        // Кнопка закрытия
        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> profileFrame.dispose());

        // Добавляем компоненты на панель
        profileFrame.add(hoursLabel);
        profileFrame.add(hoursValue);
        profileFrame.add(jobLabel);
        profileFrame.add(jobValue);
        profileFrame.add(new JLabel()); // Пустой элемент для выравнивания
        profileFrame.add(closeButton);

        profileFrame.setVisible(true);
    }



    private void logout() {
        dispose();
        JOptionPane.showMessageDialog(null, "Вы успешно вышли.");
    }
}
