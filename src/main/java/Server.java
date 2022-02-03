import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Server {
    private static Gson gson;
    String dst;
    String[] gateways;
    ArrayList<Channel> channels_info;


    public static ArrayList<Server> getServersFromJSON(File file) {
        Log.log("[getServersFromJSON] Получение списка серверов из " + file);
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

    public static ArrayList<Server> getServersFromJSON(String jsonString) {
        gson = new Gson();
        ArrayList<Server> channels = new ArrayList<>();
        Collections.addAll(channels, gson.fromJson(jsonString, Server[].class));
        return channels;
    }


    public void setRouteToServer(List<Channel> routes, List<Channel> gatewayList) {
        channels_info = new ArrayList<>();
        for (String gw : gateways) {
            Channel gatewayFound = findGateway(gatewayList, gw);
            if (gatewayFound != null)
                channels_info.add(gatewayFound);
        }
        //  цикл последовательно проходящий по каждому шлюзу из списка объекта
        for (String gw : gateways) {
            // метод поиска шлюза
            Log.log("[setRouteToServer] Поиск шлюза " + gw + " для сервера " + dst + " среди настроенных каналов в настроеном списке каналов");
            Channel gatewayFound = findGateway(gatewayList, gw);
            if (gatewayFound == null) {
                Log.log(Log.severity.err, "[setRouteToServer] Шлюз " + gw + " не найден в списке настроенных каналов");
                return;
            }
            if (!gatewayFound.isDstConnected) {
                Log.log("[setRouteToServer] Маршрут через " + gw + " в данный момент недоступен");
                continue;
            }
            Channel route = findDst(routes, dst);
            if (route == null) {
                Log.log("[setRouteToServer] Нет маршрута до сервера " + dst + " через " + gw + " добавляю");
                ShellExecutor.addRoute(gw, dst);
                return;
            }
            Log.log("[setRouteToServer] Найден маршрут до сервера " + dst + " через " + route.gateway);
            if (gw.equals(route.gateway)) {
                Log.log("[setRouteToServer] В данный момент маршрут до сервера " + dst + " через " + gw + " работает");
                return;
            }
            Log.log("[setRouteToServer] В данный момент шлюз " + gw + " доступен и приоритетнее чем " + route.gateway + ", строю маршрут до " + dst + " через " + gw);
            ShellExecutor.removeRoute(route.gateway, dst);
            ShellExecutor.addRoute(gw, dst);
            return;


//            // если маршрут существует да и к тому же шлюз все еще работает - оставить все как есть
//            if (thereSameServerRoute && gatewayFound.isDstConnected) {
//                System.out.println(LocalDateTime.now() + " Уже существует путь до сервера " + dst + " чепез шлюз " + gw);
//                return;
//            }
//
//            // если шлюз работает, а маршрута до сервера нет - создать его
//            if (gatewayFound.isDstConnected && thereSameServers.size() == 0) {
//                System.out.println(LocalDateTime.now() + " Добавляю маршрут до " + dst + " через " + gatewayFound.gateway);
//                ShellExecutor.addRoute(gatewayFound.gateway, dst);
//                return;
//            }
//            // проходим по циклу маршрутов до такого же сервера
//            for (Channel serv : thereSameServers) {
//                // если шлюз работает, а у сервера сейчас стоит другой шлюз (менее приоритетный) поставить этот шлюз
//                if (gatewayFound.isDstConnected && !serv.gateway.equals(gatewayFound.gateway)) {
//                    System.out.println(LocalDateTime.now() + " Удаляю маршрут до " + serv.dst + " через " + serv.gateway);
//                    ShellExecutor.removeRoute(serv);
//                    System.out.println(LocalDateTime.now() + " Добавляю маршрут до " + serv.dst + " через " + gatewayFound.gateway);
//                    ShellExecutor.addRoute(gatewayFound.gateway, serv.dst);
//                    return;
//                }
            //}


        }
        // если мы сюда дошли, то в данный момент нет работающих шлюзов
        Log.log(Log.severity.err, "[setRouteToServer] Сервер " + dst + " не был добавлен, так как все шлюзы недоступны");
    }


    public static Channel findGateway(List<Channel> gateways, Channel route) {
        Log.log("[findGateway] Поиск шлюза " + route.gateway + " в списке проверенных шлюзов");
        for (Channel chan : gateways) {
            if (chan.gateway.equals(route.gateway) && chan.dst.equals(route.dst))
                return chan;
        }
        return null;
    }

    public static Channel findGateway(List<Channel> routes, String node) {
        Log.log("[findGateway] Поиск шлюза " + node + " в списке проверенных шлюзов");
        for (Channel chan : routes) {
            if (chan.gateway.equals(node))
                return chan;
        }
        return null;
    }

    public static Channel findDst(List<Channel> routes, String node) {
        Log.log("[findDst] Поиск cервера " + node + " в списке проверенных маршрутов");
        for (Channel chan : routes) {
            if (chan.dst.equals(node))
                return chan;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder gatewayString = new StringBuilder();
        for (String gateway : gateways)
            gatewayString.append("gateway: '").append(gateway).append(", ");
        gatewayString.append("\b\b");
        return "Server{" +
                "dst='" + dst + ", '" +
                gatewayString + "}";
    }
}
