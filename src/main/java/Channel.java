import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
    public static ArrayList<Channel> getChannelsFromJSON(File file) {
        Log.log("[getChannelsFromJSON] Получение списка каналов из " + file);
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
    public static ArrayList<Channel> getChannelsFromJSON(String jsonString) {
        gson = new Gson();
        Log.log("[getChannelsFromJSON] Получение списка каналов из " + jsonString);
        ArrayList<Channel> channels = new ArrayList<>();
        Channel[] objects = gson.fromJson(jsonString, Channel[].class);
        for (Channel channel : objects) {
            // отсеиваю такие маршруты как default, null и другие.
            if (channel.gateway != null && !channel.gateway.matches("[a-zA-Z]+") && channel.dst != null && !channel.dst.matches("[a-zA-Z]+")) {
                Log.log("[getChannelsFromJSON] Добавлен канал " + channel + " в объект списка каналов");
                channels.add(channel);
            }
        }
        return channels;
    }

    // Проверка корректности маршрутов каналов связи
    public static void CheckRoutes(ArrayList<Channel> channels, ArrayList<Channel> routes) {
        Log.log("[CheckRoutes] Сравнение настроенных каналов связи с списком маршрутов");
        for (Channel channel : channels) {
            boolean routeIsCorrect = false;
            for (Channel route : routes) {
                if (Objects.equals(route.dst, channel.dst) && Objects.equals(route.gateway, channel.gateway)) {
                    Log.log("[CheckRoutes] Канал " + channel + " совпадает с уже построенным маршрутом " + route);
                    routeIsCorrect = true;
                } else if (Objects.equals(route.dst, channel.dst) && !Objects.equals(route.gateway, channel.gateway)) {
                    Log.log("[CheckRoutes] До " + channel.dst + " найден другой маршрут через " + route.gateway);
                    ShellExecutor.removeRoute(route);
                    ShellExecutor.addRoute(channel);
                }
            }
            if (!routeIsCorrect) {
                Log.log("[CheckRoutes] Среди списка маршрутов не найден маршрут " + channel);
                ShellExecutor.addRoute(channel);
            }
        }
    }

    // Проверка доступности шлюза и пункта назначения маршрута у канала связи
    public static void checkRouteConnection(ArrayList<Channel> channels, ArrayList<Channel> routes) {
        for (Channel channel : channels) {
            Log.log("[checkRouteConnection] Проверяю ip адрес: " + channel.gateway);
            if (ShellExecutor.isReachable(channel.gateway)) {
                Log.log("[checkRouteConnection] Шлюз " + channel.gateway + " доступен");
                channel.isGwConnected = true;
                Log.log("[checkRouteConnection] Проверяю ip адрес: " + channel.dst + " через " + channel.gateway);
                channel.isDstConnected = ShellExecutor.isReachable(channel.dst);
                Log.log("[checkRouteConnection] ip адрес: " + channel.dst + " через " + channel.gateway + " = " + channel.isDstConnected);
//                Channel route = Server.findGateway(routes, channel);
//                if (route == null) {
//                    System.out.println(LocalDateTime.now() + " Маршрут " + route.dst + " via " + route.gateway + " не найден");
//                    return;
//                }
//                System.out.println(LocalDateTime.now() + " Маршрут " + route.dst + " via " + route.gateway + " найден");
//                route.isGwConnected = true;
//                route.isDstConnected = channel.isDstConnected;
            } else {
                Log.log("[checkRouteConnection] Шлюз " + channel.gateway + " недоступен");
                channel.isGwConnected = false;
            }
        }


    }

    // Конвертация каналов в JSON файл
    public static void convertToJSON(Object obj, String src) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(src)) {
            gson.toJson(obj, writer);
            Log.log("[convertToJSON] переданный объект " + obj + " конвертирован в JSON в " + src);
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

