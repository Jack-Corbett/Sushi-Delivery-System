import server.Server;
import server.ServerWindow;

import java.io.FileNotFoundException;

/**
 *
 * @author Jack Corbett
 */
public class ServerApplication {

    public static void main(String args[]) {
        ServerApplication sa = new ServerApplication();
        Server serverInterface = sa.initialise();
        sa.launchGUI(serverInterface);
    }

    private Server initialise() {
        Server server = new Server();
        try {
            server.loadConfiguration("config.txt");
        } catch (FileNotFoundException e) {
            System.err.println("Failed to load configuration");
        }
        return server;
    }

    private void launchGUI(Server server) {
        new ServerWindow(server);
    }
}
