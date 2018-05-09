package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommsClient implements Runnable {
    private Socket clientSocket = null;
    private BufferedReader is = null;
    private PrintWriter os = null;
    private BufferedReader inputLine = null;
    public BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        new Thread(new CommsClient()).start();
    }

    @Override
    public void run() {

        // EXAMPLE DATA
        queue.add("User has logged in");
        queue.add("User Ordered 5 Sweet Rolls");
        queue.add("User Emptied Basket");
        queue.add("User Logged Out");

        try {
            clientSocket = new Socket("localhost", 2222);
            os = new PrintWriter(clientSocket.getOutputStream(), true);
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            inputLine = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host");
        }

        if (clientSocket != null && os != null && is != null) {
            try {
                System.out.println("SUSHI CLIENT STARTED");
                String line;
                String message;
                while ((line = is.readLine()) != null) {
                    System.out.println(line);
                    if ((message = queue.poll()) != null) {
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
            }
        }
    }
}
