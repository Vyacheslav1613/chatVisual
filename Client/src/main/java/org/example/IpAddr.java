package org.example;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpAddr {
    public String ip() {
        String ipAddress = null;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            ipAddress = localHost.getHostAddress();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }
}