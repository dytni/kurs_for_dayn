package com.kurs.client;

import com.kurs.client.loginForms.LoginForm;

import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientConnection clientConnection = new ClientConnection("localhost", 8080);
            new LoginForm(clientConnection).show();
        });
    }
}
