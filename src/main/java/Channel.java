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
        System.out.println(LocalDateTime.now() + " [getChannelsFromJSON] Получение списка каналов из " + file);
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
        System.out.println(LocalDateTime.now() + " [getChannelsFromJSON] Получение списка каналов из " + jsonString);
        ArrayList<Channel> channels = new ArrayList<>();
        Channel [] objects = gson.fromJson(jsonString, Channel[].class);
        for (Channel channel: objects) {
            // отсеиваю такие маршруты как default, null и другие.
            if (channel.gateway != null && !channel.gateway.matches("[a-zA-Z]+") && channel.dst != null && !channel.dst.matches("[a-zA-Z]+")) {
                System.out.println(LocalDateTime.now() + " [getChannelsFromJSON] Добавлен канал " + channel + " в объект списка каналов");
                channels.add(channel);
            }
        }
        return channels;
    }

    // Проверка корректности маршрутов каналов связи
    public static void CheckRoutes (ArrayList<Channel> channels, ArrayList<Channel> routes) {
        System.out.println(LocalDateTime.now() + " [CheckRoutes] Сравнение настроенных каналов связи с списком маршрутов");
        for (Channel channel: channels) {
            boolean routeIsCorrect = false;
            for (Channel route : routes) {
                if (Objects.equals(route.dst, channel.dst) && Objects.equals(route.gateway, channel.gateway)) {
                    System.out.println(LocalDateTime.now() + " [CheckRoutes] Канал " + channel + " совпадает с уже построенным маршрутом " + route);
                    routeIsCorrect = true;
                } else if (Objects.equals(route.dst, channel.dst) && !Objects.equals(route.gateway, channel.gateway)) {
                    System.out.println(LocalDateTime.now() + " [CheckRoutes] До " + channel.dst + " найден другой маршрут через " + route.gateway);
                    ShellExecutor.removeRoute(route);
                }
            }
            if (!routeIsCorrect) {
                System.out.println(LocalDateTime.now() + " [CheckRoutes] Среди списка маршрутов не найден маршрут " + channel);
                ShellExecutor.addRoute(channel);
            }
        }
    }

    // Проверка доступности шлюза и пункта назначения маршрута у канала связи
    public static void checkRouteConnection (ArrayList<Channel> channels, ArrayList<Channel> routes) {
        for (Channel channel: channels) {
            System.out.println(LocalDateTime.now() + " [checkRouteConnection] Проверяю ip адрес: " + channel.gateway);
            if (ShellExecutor.isReachable(channel.gateway)) {
                System.out.println(LocalDateTime.now() + " [checkRouteConnection] Шлюз " + channel.gateway + " доступен");
                channel.isGwConnected = true;
                System.out.println(LocalDateTime.now() + " [checkRouteConnection] Проверяю ip адрес: " + channel.dst + " через " + channel.gateway);
                channel.isDstConnected = ShellExecutor.isReachable(channel.dst);
                System.out.println(LocalDateTime.now() + " [checkRouteConnection] ip адрес: " + channel.dst + " через " + channel.gateway + " = " + channel.isDstConnected);
                Channel route = Server.findGateway(routes, channel);

//                if (route == null) {
//                    System.out.println(LocalDateTime.now() + " Маршрут " + route.dst + " via " + route.gateway + " не найден");
//                    return;
//                }
//                System.out.println(LocalDateTime.now() + " Маршрут " + route.dst + " via " + route.gateway + " найден");
//                route.isGwConnected = true;
//                route.isDstConnected = channel.isDstConnected;
            } else {
                System.out.println(LocalDateTime.now() + " [checkRouteConnection] Шлюз " + channel.gateway + " недоступен");
                channel.isGwConnected = false;
            }
        }


    }

    // Конвертация каналов в JSON файл
    public static void convertToJSON(Object obj, String src) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(src)){
            gson.toJson(obj, writer);
            System.out.println(LocalDateTime.now() + " [convertToJSON] переданный объект " + obj + " конвертирован в JSON в " + src);
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

