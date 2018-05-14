package server;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

class CommsServer {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;
    private Boolean running;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final CommsServerThread[] threads = new CommsServerThread[maxClientsCount];

    CommsServer(Server server) {
        Thread ServerThread = new Thread(() -> {
            running = true;
            int portNumber = 2222;
            try {
                serverSocket = new ServerSocket(portNumber);
                System.out.println("SUSHI SERVER STARTED");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            // Create a client socket for each connection and pass it to a new client thread.
            while (running) {
                try {
                    clientSocket = serverSocket.accept();
                    int i;
                    for (i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == null) {
                            (threads[i] = new CommsServerThread(clientSocket, threads, server)).start();
                            break;
                        }
                    }
                    if (i == maxClientsCount) {
                        PrintStream os = new PrintStream(clientSocket.getOutputStream());
                        os.println("Server too busy. Try later.");
                        os.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        ServerThread.start();
    }
}
