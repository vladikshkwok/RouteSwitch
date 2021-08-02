import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Channel {
    private static Gson gson;
    String gateway;
    String dst;
    boolean isGwConnected;
    boolean isDstConnected;

    public static ArrayList<Channel> getChannelsFromJSON (String fileURL) {
        ArrayList<Channel> channels = new ArrayList<>();
        gson = new Gson();
        try {
            String jsonString = new Scanner(new File(fileURL)).useDelimiter("\\Z").next();
            Collections.addAll(channels, gson.fromJson(jsonString, Channel[].class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return channels;
    }
    public static ArrayList<Channel> getChannelsFromJSONString (String jsonString) {
        gson = new Gson();
        ArrayList<Channel> channels = new ArrayList<>();
        Collections.addAll(channels, gson.fromJson(jsonString, Channel[].class));
        return channels;
    }

    public static void CheckRoutes (ArrayList<Channel> channels) {

    }

    @Override
    public String toString() {
        return "Channel{" +
                "gateway='" + gateway + '\'' +
                ", dst='" + dst + '\'' +
                ", isGwConnected=" + isGwConnected +
                ", isDstConnected=" + isDstConnected +
                '}';
    }
}
