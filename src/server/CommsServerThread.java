package server;

import java.io.*;
import java.net.Socket;

class CommsServerThread extends Thread {

    private BufferedReader ir = null;
    private PrintWriter os = null;
    private InputStream is = null;
    private Socket clientSocket = null;
    private final CommsServerThread[] threads;
    private int maxClientsCount;

    public CommsServerThread(Socket clientSocket, CommsServerThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        CommsServerThread[] threads = this.threads;

        try {
            is = clientSocket.getInputStream();
            ir = new BufferedReader(new InputStreamReader(is));
            os = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("CLIENT CONNECTED");
            os.println("READY FOR COMMUNICATION");

            String line;
            while ((line = ir.readLine()) != null) {
                System.out.println(line);
                os.println("Message Received");


                // Add conditions here
                if (line.contains("User Logged Out")) {
                    System.out.println("Closing Client Thread");
                    break;
                }
            }

            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }

            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
