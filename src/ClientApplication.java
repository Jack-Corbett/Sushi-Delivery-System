import client.Client;
import client.ClientWindow;

/**
 * The main class that starts a client which can be used to place sushi orders
 * @author Jack Corbett
 */
public class ClientApplication {

    public static void main(String[] args) {
        ClientApplication ca = new ClientApplication();
        Client clientInterface = ca.initialise();
        ca.launchGUI(clientInterface);
    }

    private Client initialise() {
        return new Client();
    }

    private void launchGUI(Client client) {
        new ClientWindow(client);
    }
}