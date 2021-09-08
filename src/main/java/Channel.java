import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Channel {
    private static Gson gson;
    String gateway;
    String dst;
    boolean isGwConnected;
    boolean isDstConnected;

    public Channel () {
    }

    public static ArrayList<Channel> getChannelsFromJSON (File file) {
        ArrayList<Channel> channels = new ArrayList<>();
        gson = new Gson();
        try {
            String jsonString = new Scanner(file).useDelimiter("\\Z").next();
            Collections.addAll(channels, gson.fromJson(jsonString, Channel[].class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return channels;
    }
    public static ArrayList<Channel> getChannelsFromJSON (String jsonString) {
        gson = new Gson();
        ArrayList<Channel> channels = new ArrayList<>();
        Collections.addAll(channels, gson.fromJson(jsonString, Channel[].class));
        return channels;
    }

    public void checkConnection (String ipAddr) {
        InetAddress inAddr = null;
        try {
            inAddr = InetAddress.getByName(ipAddr);
            System.out.println("Checking ip addr: " + ipAddr);
            if (inAddr.isReachable(2000)){
                isGwConnected = true;
            } else {
                isGwConnected = false;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
