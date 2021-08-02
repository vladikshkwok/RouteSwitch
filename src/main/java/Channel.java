import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Channel {
    String gatewayIP;
    String checkIP;
    boolean isWorking;

    public static ArrayList<Channel> getChannelsFromJSON (String fileURL) {
        Gson gson = new Gson();
        ArrayList<Channel> channels = new ArrayList<>();

        try {
            String jsonString = new Scanner(new File(fileURL)).useDelimiter("\\Z").next();
            Collections.addAll(channels, gson.fromJson(jsonString, Channel[].class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return channels;
    }

}
