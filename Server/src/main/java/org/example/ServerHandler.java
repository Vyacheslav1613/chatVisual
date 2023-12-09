package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ServerHandler extends Thread implements TCPConnectionListener {
    private Socket clientSocket;
    private HashMap<String, Integer> serverMap;
    protected final List<TCPConnection> connections = new ArrayList<>();

    public ServerHandler(Socket clientSocket, HashMap<String, Integer> serverMap) {
        this.clientSocket = clientSocket;
        this.serverMap = serverMap;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            String request = reader.readLine();
            String[] parts = request.split("\\s+");
            String name = parts[0];
            String password = parts[1];
            String key = name + "." + password;

            if (serverMap.containsKey(key)) {
                int port = serverMap.get(key);
                writer.println(port);

                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(port);
                    while (true) {
                        Socket socket = serverSocket.accept();
                        new Thread(() -> {
                            try {
                                new TCPConnection(this, socket);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                    }
                } catch (IOException e) {
                } finally {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                }
            } else {
                writer.println("Ошибка подключения");
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
            String formattedDate = dateFormat.format(date);
            String messageWithTimestamp = "[" + formattedDate + "] " + value;
            sendToAllConnections(messageWithTimestamp);
        }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Абонент " + tcpConnection + " отключился");
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        //System.out.println("TCPConnection exeptions: " + e);
    }

    private void sendToAllConnections(String value){
        System.out.println(value);
        for (int i = 0; i < connections.size(); i++) {
            connections.get(i).sendString(value);
        }
    }
}
