package server;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class CommsServer {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;
    private Server server;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final CommsServerThread[] threads = new CommsServerThread[maxClientsCount];

    /*public static void main(String[] args) {
        CommsServer server = new CommsServer();
    }*/

    CommsServer(Server server) {
        this.server = server;
        int portNumber = 2222;
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("SUSHI SERVER STARTED");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Create a client socket for each connection and pass it to a new client thread.
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
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
    }
}
