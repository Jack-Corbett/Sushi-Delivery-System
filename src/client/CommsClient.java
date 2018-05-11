package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommsClient {
    private Socket clientSocket = null;
    private BufferedReader is = null;
    private PrintWriter os = null;
    //private BufferedReader inputLine = null;
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        CommsClient commsClient = new CommsClient();
        commsClient.sendMessage("Hello Server");
        System.out.println(commsClient.receiveMessage());
        System.out.println(commsClient.receiveMessage());
    }

    CommsClient() {
        Thread ClientThread = new Thread(() -> {
            try {
                clientSocket = new Socket("localhost", 2222);
                os = new PrintWriter(clientSocket.getOutputStream(), true);
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //inputLine = new BufferedReader(new InputStreamReader(System.in));
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
                        //System.out.println(line);
                        responseQueue.add(line);
                        if ((message = messageQueue.poll()) != null) {
                            //System.out.println(message);
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
                }
            }
        });
        ClientThread.start();
    }

    void sendMessage(String message) {
        messageQueue.add(message);
    }

    String receiveMessage() {
        String response = null;
        while (response == null) {
            response = responseQueue.poll();
        }
        return response;
    }
}
