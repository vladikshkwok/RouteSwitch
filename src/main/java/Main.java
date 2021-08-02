import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
       ArrayList<Channel> channels = Channel.getChannelsFromJSON("/home/vladikshk/IdeaProjects/RouteSwitch/src/main/resources/channels.json");

        for (Channel channel: channels) {
            System.out.println(channel);
        }

        ArrayList<Channel> routes = new ArrayList<>();
        System.out.println("Actual routes:");
        try {
            routes = Channel.getChannelsFromJSONString(ShellExecutor.getRoutesFromShell());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        for (Channel route: routes) {
            System.out.println(route);
        }


    }
}
