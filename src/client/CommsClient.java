package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Handles communicating with the server using string based messages and performs relevant updates to the client
 * depending on the response.
 * @author Jack Corbett
 */
class CommsClient {
    private Socket clientSocket = null;
    private BufferedReader is = null;
    private PrintWriter os = null;
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

    /**
     * Constructor which starts a new thread to establish a connection to the server
     */
    CommsClient() {
        Thread ClientThread = new Thread(() -> {
            try {
                clientSocket = new Socket("localhost", 2222);
                os = new PrintWriter(clientSocket.getOutputStream(), true);
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (UnknownHostException e) {
                System.err.println("Cannot find server");
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to server");
            }

            if (clientSocket != null && os != null && is != null) {
                try {
                    System.out.println("SUSHI CLIENT STARTED");
                    String line;
                    String message;
                    while ((line = is.readLine()) != null) {
                        System.out.println(line);
                        responseQueue.add(line);
                        if ((message = messageQueue.take()) != null) {
                            System.out.println(message);
                            os.println(message);
                        }
                    }

                    os.close();
                    is.close();
                    clientSocket.close();
                } catch (UnknownHostException e) {
                    System.err.println("Trying to connect to unknown host: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("IOException:  " + e.getMessage());
                } catch (InterruptedException e) {
                    System.err.println("Interrupted Exception: " + e.getMessage());
                }
            }
        });
        ClientThread.start();
    }

    /**
     * Sends a message to the server by adding it to the message queue.
     * @param message String to be sent
     */
    void sendMessage(String message) {
        messageQueue.add(message);
    }

    /**
     * Relieves a message from the server by checking the response queue, waiting up to a second for the response. This
     * ensures the correct response is received without the thread being blocked if there is an issue with the server.
     * @return The server's response.
     */
    String receiveMessage() {
        try {
            return responseQueue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted - Unable to receive response from server");
        }
        System.err.println("Timeout - No response from server");
        return null;
    }
}
