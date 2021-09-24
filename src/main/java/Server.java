import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Server {
    private static Gson gson;
    String dst;
    String test;
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


    public void  setRouteToServer(List<Channel> routes, List<Channel> gatewayList) {
        // Лист из маршрутов до такого же сервера
        List<Channel> thereSameServers = routes.stream().filter(u -> u.dst.equals(dst)).collect(Collectors.toList());

        //  цикл последовательно проходящий по каждому шлюзу из списка объекта
        for (String gw: gateways) {
            // Переменная хранящая значение - есть ли уже маршрут до сервера через этот шлюз
            boolean thereSameServerRoute = routes.stream().anyMatch(u -> u.dst.equals(dst) && u.gateway.equals(gw));
            // метод поиска шлюза
            Channel gatewayFound = findGateway(gatewayList, findGateway(routes, gw));
            // если маршрут существует да и к тому же шлюз все еще работает - оставить все как есть
            if (thereSameServerRoute && gatewayFound.isDstConnected) {
                System.out.println("Уже существует путь до сервера " + dst + " чепез шлюз " + gw);
                return;
            }
            else {
                // если шлюз работает, а маршрута до сервера нет - создать его
                if (gatewayFound.isDstConnected && thereSameServers.size() == 0) {
                    System.out.println("Добавляю маршрут до " + dst + " через " + gatewayFound.gateway);
                    ShellExecutor.addRoute(gatewayFound.gateway, dst);
                    return;
                }
                // проходим по циклу маршрутов до такого же сервера
                for (Channel serv: thereSameServers) {
                    // если шлюз работает, а у сервера сейчас стоит другой шлюз (менее приоритетный) поставить этот шлюз
                    if (gatewayFound.isDstConnected && !serv.gateway.equals(gatewayFound.gateway)){
                        System.out.println("Удаляю маршрут до " + serv.dst + " через " + serv.gateway);
                        ShellExecutor.removeRoute(serv);
                        System.out.println("Добавляю маршрут до " + serv.dst + " через " + gatewayFound.gateway);
                        ShellExecutor.addRoute(gatewayFound.gateway, serv.dst);
                        return;
                    }
                }
            }

        }
        // если мы сюда дошли, то в данный момент нет работающих шлюзов
        System.out.println("Сервер " + dst + " не был добавлен, так как все шлюзы недоступны");
    }


    public static Channel findGateway (List<Channel> gateways, Channel route) {
        for (Channel chan: gateways) {
            if (chan.gateway.equals(route.gateway) && chan.dst.equals(route.dst))
                return chan;
        }
        return null;
    }

    public static Channel findGateway (List<Channel> routes, String gateway) {
        for (Channel chan: routes) {
            if (chan.gateway.equals(gateway))
                return chan;
        }
        return null;
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
