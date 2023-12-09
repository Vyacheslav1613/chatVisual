package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class MainServer extends JFrame {
    private static final int SERVER_PORT = 8888;
    private HashMap<String, Integer> serverMap;
    private JTextField NameServer;
    private JTextField Password;
    private static JTextField Port;
    private static  int PORT;

   //private static final int

    public MainServer() {
        serverMap = new HashMap<>();
        serverMap.put("serv.20", 8630);
    }

    public void start() {
        visual();
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Main Server started on port " + SERVER_PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ServerHandler serverHandler = new ServerHandler(clientSocket, serverMap);
                serverHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void visual() {
        Thread thread = new Thread(() -> {
            setTitle("Создание сервера");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(400, 200);
            setLayout(new GridLayout(4, 2));

            JLabel label1 = new JLabel("Имя сервера");
            NameServer = new JTextField();
            JLabel label2 = new JLabel("Пароль (при необходимости)");
            Password = new JTextField();
            JLabel label3 = new JLabel("Порт");
            Port = new JTextField();
            JPanel panel = new JPanel(new GridLayout(4, 2));

            add(label1);
            add(NameServer);
            add(label2);
            add(Password);
            add(label3);
            add(Port);
            JButton createButton = new JButton("Создать");
            createButton.addActionListener(e -> {
                String value1 = NameServer.getText();
                String value2 = Password.getText();
                String value3 = Port.getText();
                if (!value3.equals("")) {
                    PORT = Integer.parseInt(value3);
                } else {
                    PORT = newPort();
                }
                if (value2.equals("")){
                    value2 = "0";
                }
                serverMap.put(value1 +"." +value2, PORT);
                JOptionPane.showMessageDialog(this, "Создан новый чат " + value1 + "\n" +
                        "пароль для клиента: " + Password.getText(), "НОВЫЙ ЧАТ", JOptionPane.INFORMATION_MESSAGE);
            });

            add(panel, BorderLayout.CENTER);
            add(createButton, BorderLayout.SOUTH);
            setVisible(true);
        });
        thread.start();
    }
    private int newPort() {
        Random random = new Random();
        int min = 1;
        int max = 10000;
        int x = random.nextInt((max-min+1) + min);
        for (String name : serverMap.keySet()) {
            if (serverMap.get(name) == x){
                newPort();
            }
        }
        return x;
    }
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        mainServer.start();
    }
}