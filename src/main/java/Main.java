import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
       ArrayList<Channel> channels = Channel.getChannelsFromJSON("/home/vladikshk/IdeaProjects/RouteSwitch/src/main/resources/channels.json");

        for (Channel channel: channels) {
            System.out.println("Key: " + channel.gatewayIP + "   Value: "+ channel.checkIP);
        }


    }
}
