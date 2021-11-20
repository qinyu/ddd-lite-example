package com.example.question.infrastructure;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;


@Component
public class EnvUtil {
    final Environment environment;

    private String port;
    private String hostname;

    public EnvUtil(Environment environment) {
        this.environment = environment;
    }

    private String getPort() {
        if (port == null) port = environment.getProperty("local.server.port");
        return port;
    }

    private String getHostname() throws UnknownHostException {
        if (hostname == null) hostname = InetAddress.getLocalHost().getHostAddress();
        return hostname;
    }

    public String getServerUrl() throws UnknownHostException {
        return "http://" + getHostname() + ":" + getPort();
    }
}