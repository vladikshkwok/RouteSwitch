import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Server {
    private static Gson gson;
    String dst;
    String [] gateways;


    public static ArrayList<Server> getServersFromJSON (File file) {
        ArrayList<Server> channels = new ArrayList<>();
        gson = new Gson();
        try {
            String jsonString = new Scanner(file).useDelimiter("\\Z").next();
            Collections.addAll(channels, gson.fromJson(jsonString, Server[].class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return channels;
    }
    public static ArrayList<Server> getServersFromJSON (String jsonString) {
        gson = new Gson();
        ArrayList<Server> channels = new ArrayList<>();
        Collections.addAll(channels, gson.fromJson(jsonString, Server[].class));
        return channels;
    }

    @Override
    public String toString() {
        String gatewayString = "";
        for (String gateway: gateways)
            gatewayString += "gateway: '" + gateway + ", ";
        gatewayString += "\b\b";
        return "Server{" +
                "dst='" + dst + ", '" +
                gatewayString + "}";
    }
}
