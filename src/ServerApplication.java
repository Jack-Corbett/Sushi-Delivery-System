import server.Server;
import server.ServerWindow;

public class ServerApplication {

    public static void main(String args[]) {
        ServerApplication sa = new ServerApplication();
        Server serverInterface = sa.initialise();
        sa.launchGUI(serverInterface);
    }

    private Server initialise() {
        return new Server();
    }

    private void launchGUI(Server server) {
        new ServerWindow(server);
    }


}
