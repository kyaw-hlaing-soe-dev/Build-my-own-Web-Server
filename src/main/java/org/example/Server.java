package org.example;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    final private static int PORT = 8080;
    final private static int THREAD_POOL_SIZE =
            Math.max(2, Runtime.getRuntime().availableProcessors());
    final private static ExecutorService EXECUTOR_SERVICE =
            Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                 try {
                    Socket client = serverSocket.accept();
                    EXECUTOR_SERVICE.submit(new ServerHandler(client));
                    }
                 catch(Exception err) {
                    err.printStackTrace();
                    }
                 }
            }
        }
}
