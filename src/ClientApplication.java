import client.Client;
import client.ClientWindow;

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