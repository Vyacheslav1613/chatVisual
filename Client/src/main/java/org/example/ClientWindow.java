package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final IpAddr ipAddr = new IpAddr();
    private static final String IP_ADDR = ipAddr.ip();
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private final JTextArea log = new JTextArea();
    protected final JTextField fieldNickname = new JTextField();
    protected final JTextField fieldInput = new JTextField();
    private final JTextField fieldPort = new JTextField();
    private TCPConnection connection;
    private String nick = "";

    protected ClientWindow(int port) throws Exception {

        boolean isConnected = false;

        while (!isConnected) {
            try {
                String nick = JOptionPane.showInputDialog(this, "Введите ваш никнейм", "Никнейм", JOptionPane.INFORMATION_MESSAGE);
                if (nick.equals("")){
                    JOptionPane.showMessageDialog(this, "ИМЯ НЕ СОЖЕТ БЫТЬ ПУСТЫМ", "ERROR", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                connection = new TCPConnection(this, IP_ADDR, port);
                isConnected = true;
                connection.clientConnect(nick);
                fieldNickname.setText(nick);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Не удалось подключиться к порту или порт не существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);

        add(fieldPort, BorderLayout.EAST);


        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (fieldNickname.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите ваше имя", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String msg = fieldInput.getText();
        if (msg.equals("")) return;

        if (msg.equals("/exit")) {
            onDisconnect(connection);
            System.exit(1);
        }

        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception " + e);
    }


    private synchronized void printMessage(final String msg) {
        if (fieldNickname.equals("")) return;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}