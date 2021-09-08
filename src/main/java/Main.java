import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
       ArrayList<Channel> channels = Channel.getChannelsFromJSON( new File("/home/vshkarubov/IdeaProjects/RouteSwitch/src/main/resources/channels.json"));

        for (Channel channel: channels) {
            channel.checkConnection(channel.gateway);
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
    }
}
