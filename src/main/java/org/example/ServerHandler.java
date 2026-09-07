package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class ServerHandler implements Runnable {

    final private static String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    final private static String DYNAMIC = "/dynamic.html";
    final private static byte[] NOT_FOUND_HTML =
            "<h1>Not found :(</h1>".getBytes(StandardCharsets.UTF_8);

    private final Socket client;

    ServerHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (Socket clientSocket = client) {
            Thread.sleep(10000);
            System.out.println("Current time in seconds: " + Instant.now().getEpochSecond());

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            List<String> requestLines = new ArrayList<>();

            String line;
            do {
                line = br.readLine();
                requestLines.add(line);
            } while (!line.isBlank());

            String[] requestLine = requestLines.get(0).split(" ");
            String path = requestLine[1];

            Path filePath = Paths.get(".", path);

            if (DYNAMIC.equals(path)) {
                sendResponse(clientSocket, "200 OK", "text/html", getDynamicResponse());
            } else if (Files.isRegularFile(filePath)) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                sendResponse(clientSocket, "200 OK", contentType, Files.readAllBytes(filePath));
            } else {
                sendResponse(clientSocket, "404 Not Found", "text/html", NOT_FOUND_HTML);
            }
        } catch (InterruptedException err) {
            Thread.currentThread().interrupt();
        } catch (IOException | RuntimeException err) {
            err.printStackTrace();
        }
    }

    private static byte[] getDynamicResponse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_NOW);
        String response = String.format(
                "<h1>Dynamic response</h1> Today is %s",
                LocalDateTime.now().format(formatter));

        return response.getBytes(StandardCharsets.UTF_8);
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
    }
}
