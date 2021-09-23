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

//    public void  setRouteToServer(ArrayList<Channel> routes) {
//        for (String gw: gateways) {
//            for (Channel route : routes) {
//                if (gw.equals(route.gateway) && route.isDstConnected) {
//                    List<Channel> thereSameServers = routes.stream().filter(u -> u.dst.equals(dst)).collect(Collectors.toList());
//                    if (thereSameServers.size() > 0) {
//                        for (Channel serv: thereSameServers) {
//                            if (gw.equals(serv.gateway)) {
//                                System.out.println("Сервер " + serv.dst + " через шлюз " + serv.gateway + " уже есть, не удаляю");
//                                return;
//                            }
//                            else {
//                                System.out.println("Удаляю сервер " + serv.dst + ", прописанный через шлюз " + serv.gateway);
//                                return;
//                            }
//                        }
//                    } else {
//                        System.out.println("Создаю роут до сервера " + dst + " через шлюз " + gw);
//                        return;
//                    }
//                } else {
//                    System.out.println("Skip " + dst + " through " + gw);
//                }
//            }
//        }
//
//    }


    public void  setRouteToServer(List<Channel> routes, List<Channel> gatewayList) {
        List<Channel> thereSameServers = routes.stream().filter(u -> u.dst.equals(dst)).collect(Collectors.toList());
        for (String gw: gateways) {
            boolean thereSameServerRoute = routes.stream().anyMatch(u -> u.dst.equals(dst) && u.gateway.equals(gw));
            if (thereSameServerRoute)
                return;
            else {
                Channel gatewayFound = findGateway(routes, findGateway(gatewayList, gw));
                for (Channel serv: thereSameServers) {
                    if (gatewayFound.isDstConnected && !serv.gateway.equals(gatewayFound.gateway)){
                        System.out.println("Удаляю маршрут до " + serv.dst + " через " + serv.gateway);
                        System.out.println("Добавляю маршрут до " + serv.dst + " через " + gatewayFound.gateway);
                    }
                }
            }
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


    public static Channel findGateway (List<Channel> routes, Channel gateway) {
        for (Channel chan: routes) {
            if (chan.gateway.equals(gateway.gateway) && chan.dst.equals(gateway.gateway)) ;
            return chan;
        }
        return null;
    }
    public static Channel findGateway (List<Channel> routes, String gateway) {
        for (Channel chan: routes) {
            if (chan.gateway.equals(gateway)) ;
            return chan;
        }
        return null;
    }
}
