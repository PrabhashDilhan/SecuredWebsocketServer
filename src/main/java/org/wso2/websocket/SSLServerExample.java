package org.wso2.websocket;

import java.io.*;
import java.security.KeyStore;
import java.util.Properties;
import java.util.Scanner;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

public class SSLServerExample {


    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        //System.out.print("Enter the path to config.properties file : ");
        String path = args[0];
        Properties prop = new Properties();
        File initialFile = new File(path);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            try {
                prop.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("property file is not found");
        }
        int port = Integer.parseInt(prop.getProperty("port"));
        String keystorepath = prop.getProperty("keystorepath");
        String storepassword = prop.getProperty("storepassword");
        String keypassword = prop.getProperty("keypassword");
        ChatServer chatserver = new ChatServer(
                port);

        // load up the key store
        String STORETYPE = "JKS";
        String KEYSTORE = keystorepath;
        String STOREPASSWORD = storepassword;
        String KEYPASSWORD = keypassword;

        KeyStore ks = KeyStore.getInstance(STORETYPE);
        File kf = new File(KEYSTORE);
        ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, KEYPASSWORD.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        chatserver.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));

        chatserver.start();

    }
}
