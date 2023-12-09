package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientGUI extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    protected JTextField nameField;
    protected JPasswordField passwordField;
    protected JButton connectButton;
    protected JTextArea resultArea;

    public ClientGUI() {
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JLabel nameLabel = new JLabel("Name:");
        JLabel passwordLabel = new JLabel("Password:");
        nameField = new JTextField();
        passwordField = new JPasswordField();
        connectButton = new JButton("Connect");

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);
        inputPanel.add(connectButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = new String(passwordField.getPassword());
                if (password.equals("")){
                    password = "0";
                }

                connectToServer(name, password);
            }
        });
    }

    protected void connectToServer(String name, String password) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println(name + " " + password);

            String response = reader.readLine();
            resultArea.append(response + "\n");
            ClientWindow clientWindow = new ClientWindow(Integer.parseInt(response));
            this.dispose();


        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "НЕВЕРНЫЙ ЛОГИН ИЛИ ПАРОЛЬ", "EROOR", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientGUI clientGUI = new ClientGUI();
                clientGUI.setVisible(true);
            }
        });
    }
}