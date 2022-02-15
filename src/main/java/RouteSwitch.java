import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class RouteSwitch {

    private static final Properties properties = new Properties();

    public static void run() {
        String serverJSON, channelJSON, routesJSON;
        FileInputStream fis;
        File file;

        try {
            fis = new FileInputStream("config/routeSwitch.config");
            properties.load(fis);
        } catch (IOException e) {
            //e.printStackTrace();
            Log.log(Log.severity.err, e.getLocalizedMessage());
            return;
        }
        PrintStream o;
        String logFile = properties.getProperty("logFile");
        if (!(logFile == null || logFile.equalsIgnoreCase("debug"))) {
            try {
                o = new PrintStream(new FileOutputStream(logFile, true));
                System.setOut(o);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.log("[RouteSwitch.run] RouteSwitch ЗАПУЩЕН");
        serverJSON = properties.getProperty("serverJSONPath");
        if (serverJSON == null) {
            Log.log(Log.severity.err, "[RouteSwitch.run] не найден параметр serverJSONPath. стандартный = config/servers.json");
            serverJSON = "config/servers.json";
        }

        channelJSON = properties.getProperty("channelJSONPath");
        if (channelJSON == null) {
            Log.log(Log.severity.err, "[RouteSwitch.run] не найден параметр serverJSONPath. стандартный = config/channels.json");
            channelJSON = "config/channels.json";
        }

        routesJSON = "routesINFO.json";

        Log.log("[RouteSwitch.run] Получаю список каналов связи из channels.JSON");
        file = new File(channelJSON);
        if (!file.exists()) {
            Log.log(Log.severity.err, "[RouteSwitch.run] не найден файл " + channelJSON);
            return;
        }
        // Каналы связи и узел для проверки, записанные техником в channels.json
        ArrayList<Channel> channels = Channel.getChannelsFromJSON(file);

        file = new File(serverJSON);
        if (!file.exists()) {
            Log.log(Log.severity.err, "[RouteSwitch.run] не найден файл " + serverJSON);
            return;
        }
        Log.log("[RouteSwitch.run] Получаю список серверов со шлюзами из servers.JSON");
        // Корп.сервера со шлюзами, записанными в приоритетном порядке (наверное сотрудниками тех.поддержки)
        ArrayList<Server> servers = Server.getServersFromJSON(file);

        Log.log("[RouteSwitch.run] Получаю список текущих маршрутов в системе");
        // получение списка маршрутов заданных в данный момент на устройстве
        ArrayList<Channel> routes = Channel.getChannelsFromJSON(ShellExecutor.getRoutesFromShell());

        Log.log("[RouteSwitch.run] Проверяю доступность шлюзов и строю маршруты для проверки шлюзов");
        // для каждого канала(указанного техником) проверяется существует ли маршрут, и правильный ли он(если неправильный то удаляется), если не найден маршрут то создается новый.
        Channel.CheckRoutes(channels, routes);
        Log.log("[RouteSwitch.run] Проверяю доступность маршрутов");
        // Проверить, пингуется ли шлюз, если пингуется то пингануть узел для проверки шлюза
        Channel.checkRouteConnection(channels, routes);

        Log.log("[RouteSwitch.run] Строю маршруты до серверов");
        // Для каждого сервера создать свой маршрут (с наиболее предпочтительным шлюзом)
        for (Server server : servers) {
            server.setRouteToServer(routes, channels);
        }

        // сохранение полученных маршрутов в json (не знаю пока правда зачем это пригодится, если все равно по сути проверять лучше каждый раз, маршруты)
        if (!new File(routesJSON).exists()) {
            Channel.convertToJSON(servers, routesJSON);
            Channel.checkRouteConnection(channels, routes);
        } else
            Channel.convertToJSON(servers, routesJSON);
        Log.log("[RouteSwitch.run] RouteSwitch ЗАВЕРШИЛ СВОЮ РАБОТУ");
    }

}
