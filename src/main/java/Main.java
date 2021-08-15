import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
       ArrayList<Channel> channels = Channel.getChannelsFromJSON(new File("/home/vladikshk/IdeaProjects/RouteSwitch/src/main/resources/channels.json"));
       ArrayList<Server> servers = Server.getServersFromJSON(new File("/home/vladikshk/IdeaProjects/RouteSwitch/src/main/resources/servers.json"));

        for (Channel channel: channels) {
            System.out.println(channel);
        }

        ArrayList<Channel> routes = new ArrayList<>();
        System.out.println("Actual routes:");
        try {
            routes = Channel.getChannelsFromJSON(ShellExecutor.getRoutesFromShell());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        for (Channel route: routes) {
            System.out.println(route);
        }


        for (Server server: servers) {
            System.out.println(server);
        }

    }
}
