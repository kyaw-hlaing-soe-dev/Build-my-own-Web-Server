package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    final private static int PORT = 8080;

    public static void main(String[] args) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                 try {
                    Socket client = serverSocket.accept();
                    handleClient(client);
                    }
                 catch(Exception err) {
                    err.printStackTrace();
                    }
                 }
            }
        }

    private static void handleClient(Socket client) throws IOException {

        // Get the input stream
        BufferedReader br = new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        List<String> requestsLines = new ArrayList<>();

        String line;
        // Read all lines in the request until you find a blank line
        do {
            line = br.readLine();
            requestsLines.add(line);
            } while(!line.isBlank());

        }
}