import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;

public class Channel {
    private static Gson gson;
    String gateway;     // шлюз
    String dst;         // куда настроен маршрут
    boolean isGwConnected;
    boolean isDstConnected;

    // Получение списка каналов из JSON-файла
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

    // Получение списка каналов из JSON-строки
    public static ArrayList<Channel> getChannelsFromJSON (String jsonString) {
        gson = new Gson();
        ArrayList<Channel> channels = new ArrayList<>();
        Channel [] objects = gson.fromJson(jsonString, Channel[].class);
        for (Channel channel: objects) {
            // отсеиваю такие маршруты как default, null и другие.
            if (channel.gateway != null && !channel.gateway.matches("[a-zA-Z]+") && channel.dst != null && !channel.dst.matches("[a-zA-Z]+")) {
                channels.add(channel);
            }
        }
        return channels;
    }

    // Проверка корректности маршрутов каналов связи
    public static void CheckRoutes (ArrayList<Channel> channels, ArrayList<Channel> routes) {
        for (Channel channel: channels) {
            boolean routeIsCorrect = false;
            for (Channel route : routes) {
                if (Objects.equals(route.dst, channel.dst) && Objects.equals(route.gateway, channel.gateway)) {
                    routeIsCorrect = true;
                } else if (Objects.equals(route.dst, channel.dst) && !Objects.equals(route.gateway, channel.gateway)) {
                    ShellExecutor.removeRoute(route);
                }
            }
            if (!routeIsCorrect) {
                ShellExecutor.addRoute(channel);
            }
        }
    }

    // Проверка доступности шлюза и пункта назначения маршрута у канала связи
    public static void checkRouteConnection (ArrayList<Channel> channels, ArrayList<Channel> routes) {

        for (Channel channel: channels) {
            System.out.println(LocalDateTime.now() + " Checking ip addr: " + channel.gateway);
            if (ShellExecutor.isReachable(channel.gateway)) {
                channel.isGwConnected = true;
                System.out.println(LocalDateTime.now() + " Checking ip addr: " + channel.dst + " through " + channel.gateway);
                channel.isDstConnected = ShellExecutor.isReachable(channel.dst);
                System.out.println("dst: " + channel.dst + " = " + channel.isDstConnected);
                Channel route = Server.findGateway(routes, channel);

                if (route == null) {
                    System.out.println("gg wp");
                    return;
                }
                System.out.println("Route to " + route.dst + " via " + route.gateway + " founded");
                route.isGwConnected = true;
                route.isDstConnected = channel.isDstConnected;
            } else {
                channel.isGwConnected = false;
            }
        }


    }

    // Конвертация каналов в JSON файл
    public static void convertToJSON(Object obj, String src) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(src)){
            gson.toJson(obj, writer);
            System.out.println(LocalDateTime.now() + " gateway list converted to JSON");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "{" +
                "gateway='" + gateway + '\'' +
                ", dst='" + dst + '\'' +
                ", isGwConnected=" + isGwConnected +
                ", isDstConnected=" + isDstConnected +
                '}';
    }
}

