package server;

import common.Dish;
import common.Order;
import common.Postcode;
import common.User;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

class CommsServerThread extends Thread {

    private Server server;

    private Socket clientSocket = null;
    private final CommsServerThread[] threads;
    private int maxClientsCount;
    private List<Postcode> currentPostcodes;
    // Stores a reference to the user of this client thread
    private User user;

    CommsServerThread(Socket clientSocket, CommsServerThread[] threads, Server server) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        BufferedReader ir;
        PrintWriter os;
        InputStream is;

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
                String[] message = line.split(":");

                if (message[0].startsWith("USER")) {
                    // REGISTER
                    if (message[0].contains("Register")) {

                        Postcode userPostcode = null;
                        for (Postcode postcode : server.getPostcodes()) {
                            if (postcode.getName().equals(message[4])) userPostcode = postcode;
                        }

                        if (userPostcode != null) {
                            server.users.add(new User(message[1], message[2], message[3], userPostcode));
                            os.println("SUCCESS Register");
                        } else {
                            os.println("FAILED Register");
                        }

                        // LOGIN
                    } else if (message[0].contains("Login")) {

                        String password = null;
                        // Find the password for that user to check it is correct
                        for (User user : server.users) {
                            if (user.getName().equals(message[1])) {
                                password = user.getPassword();
                                this.user = user;
                                break;
                            }
                        }

                        if (password != null && password.equals(message[2])) {
                            os.println("SUCCESS Login:" + user.getAddress() + ":" + user.getPostcode().getName()
                                    + ":" + user.getPostcode().getDistance());
                        } else {
                            this.user = null;
                            os.println("FAILED Login");
                        }

                        // ADD TO BASKET
                    } else if (message[0].contains("Add to basket")) {

                        Dish userDish = null;
                        // Find the dish
                        for (Dish dish : server.getDishes()) {
                            if (dish.getName().equals(message[1])) userDish = dish;
                        }
                        if (userDish != null) {
                            user.addToBasket(userDish, Integer.parseInt(message[2]));
                            os.println("SUCCESS Added");
                        } else {
                            os.println("FAILED Add to basket");
                        }

                        // UPDATE BASKET
                    } else if (message[0].contains("Update basket")) {

                        Dish userDish = null;
                        // Find the dish
                        for (Dish dish : server.getDishes()) {
                            if (dish.getName().equals(message[1])) userDish = dish;
                        }
                        if (userDish != null) {
                            user.getBasket().put(userDish, Integer.parseInt(message[2]));
                            os.println("SUCCESS Updated");
                        } else {
                            os.println("FAILED Update basket");
                        }

                        // CHECKOUT BASKET
                    } else if (message[0].contains("Checkout")) {

                        Order order = new Order(user, user.getBasket());
                        server.orderQueue.add(order);
                        user.orders.add(order);
                        user.clearBasket();
                        os.println("SUCCESS Order created");

                        // CLEAR BASKET
                    } else if (message[0].contains("Clear basket")) {

                        user.clearBasket();
                        os.println("SUCCESS Basket cleared");

                        // GET ORDERS
                    } else if (message[0].contains("Get orders")) {

                        StringBuilder osb = new StringBuilder();
                        osb.append("USER Orders:");
                        if (user != null) {
                            for (Order order : user.getOrders()) {

                                // NOT LOOPING
                                for (Map.Entry<Dish, Number> entry : order.getItems().entrySet()) {
                                    System.out.println("I RAN");
                                    Dish dish = entry.getKey();
                                    Number amount = entry.getValue();
                                    osb.append(dish.getName());
                                    osb.append(".");
                                    osb.append(dish.getDescription());
                                    osb.append(".");
                                    osb.append(dish.getPrice());
                                    osb.append(".");
                                    osb.append(dish.getRestockThreshold());
                                    osb.append(".");
                                    osb.append(dish.getRestockAmount());
                                    osb.append(" * ");
                                    osb.append(amount);
                                    osb.append(",");
                                }
                                osb.append(":");
                            }
                        }
                        os.println(osb.toString());

                    }
                } else if (message[0].startsWith("POSTCODE")) {
                    // FETCH ALL POSTCODES
                    if (message[0].contains("Fetch")) {

                        if (server.getPostcodes() != currentPostcodes) {
                            StringBuilder psb = new StringBuilder();
                            psb.append("POSTCODE All:");
                            for (Postcode postcode : server.getPostcodes()) {
                                psb.append(postcode.getCode());
                                psb.append(",");
                                psb.append(postcode.getDistance());
                                psb.append(":");
                            }
                            currentPostcodes = server.getPostcodes();
                            os.println(psb.toString());
                        } else {
                            os.println("POSTCODE No change");
                        }

                    }
                } else if (message[0].startsWith("DISH")) {
                    // FETCH ALL DISHES
                    if (message[0].contains("Fetch")) {

                        StringBuilder dsb = new StringBuilder();
                        dsb.append("DISH All:");
                        for (Dish dish : server.getDishes()) {
                            dsb.append(dish.getName());
                            dsb.append(",");
                            dsb.append(dish.getDescription());
                            dsb.append(",");
                            dsb.append(dish.getPrice());
                            dsb.append(",");
                            dsb.append(dish.getRestockThreshold());
                            dsb.append(",");
                            dsb.append(dish.getRestockAmount());
                            dsb.append(":");
                        }
                        os.println(dsb.toString());
                    }
                } else if (message[0].startsWith("ORDER")) {

                    // CHECK IF AN ORDER IS COMPLETED
                    if (message[0].contains("Is complete")) {

                        Order orderToCheck = null;
                        for (Order order : user.getOrders()) {
                            if (order.getName().equals(message[1])) orderToCheck = order;
                        }

                        if (orderToCheck != null) {
                            if (orderToCheck.getComplete()) {
                                os.println("SUCCESS Complete");
                            } else {
                                os.println("SUCCESS Incomplete");
                            }
                        } else {
                            os.println("FAILED");
                        }

                        // CHECK THE STATUS OF AN ODER
                    } else if (message[0].contains("Get status")) {

                        Order orderToCheck = null;
                        for (Order order : user.getOrders()) {
                            if (order.getName().equals(message[1])) orderToCheck = order;
                        }

                        if (orderToCheck != null) {
                            os.println("SUCCESS Status:" + orderToCheck.getStatus());
                        } else {
                            os.println("FAILED");
                        }

                        // CANCEL AN ORDER
                    } else if (message[0].contains("Cancel")) {

                        Order orderToCheck = null;
                        for (Order order : user.getOrders()) {
                            if (order.getName().equals(message[1])) orderToCheck = order;
                        }

                        if (orderToCheck != null) {
                            orderToCheck.setCancelled();
                            server.orderQueue.remove(orderToCheck);
                            server.completedOrders.remove(orderToCheck);
                            os.println("SUCCESS Cancelled");
                        } else {
                            os.println("FAILED");
                        }
                    }
                } else {
                    os.println("ERROR Unknown message type");
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
