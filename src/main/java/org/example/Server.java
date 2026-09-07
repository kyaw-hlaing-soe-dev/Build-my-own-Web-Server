package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Server {

    final private static int PORT = 8080;
    final private static byte[] NOT_FOUND_HTML =
            "<h1>Not found :(</h1>".getBytes(StandardCharsets.UTF_8);

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

        // Parse the requested path from the first line in the request.
        String[] requestLine = requestsLines.get(0).split(" ");
        String path = requestLine[1];

        Path filePath = Paths.get(".", path);

        if (Files.isRegularFile(filePath)) {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
        } else {
            sendResponse(client, "404 Not Found", "text/html", NOT_FOUND_HTML);
        }

        }

    private static void sendResponse(Socket client, String status,
                                     String contentType, byte[] content) throws IOException {
        String lineBreak = "\r\n";
        OutputStream output = client.getOutputStream();

        output.write(("HTTP/1.1 " + status + lineBreak).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Type: " + contentType + lineBreak).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Length: " + content.length + lineBreak).getBytes(StandardCharsets.UTF_8));
        output.write(("Connection: close" + lineBreak).getBytes(StandardCharsets.UTF_8));
        output.write(lineBreak.getBytes(StandardCharsets.UTF_8));
        output.write(content);
        output.flush();
        client.close();
    }
}
