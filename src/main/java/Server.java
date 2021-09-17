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

    public void  setRouteToServer(ArrayList<Channel> routes) {
        boolean needRoute = false;
        for (String gw: gateways) {
            for (Channel route : routes) {
                if (gw.equals(route.gateway) && route.isDstConnected) {
                    needRoute = true;
                } else {
                    System.out.println("Skip " + dst + " through " + gw);
                }
                if (gw.equals(route.gateway) && dst.equals(route.dst))
                    needRoute = false;
            }
        }
        if (needRoute) {
            System.out.println(dst + " --- Нужен маршрут");
        }
    }


    @Override
    public String toString() {
        StringBuilder gatewayString = new StringBuilder();
        for (String gateway: gateways)
            gatewayString.append("gateway: '").append(gateway).append(", ");
        gatewayString.append("\b\b");
        return "Server{" +
                "dst='" + dst + ", '" +
                gatewayString + "}";
    }
}
