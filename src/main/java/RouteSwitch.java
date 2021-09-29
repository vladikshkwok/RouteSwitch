import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;

public class RouteSwitch {

    private static final Properties properties = new Properties();

    public static void run () {
        try {
            properties.load(new FileInputStream("config/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String serverJSON, channelJSON, routesJSON, logFile;
        PrintStream o = null;
        try {
            logFile = properties.getProperty("logFile");
            o = new PrintStream(logFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream console = System.out;
        System.setOut(o);
        System.out.println(LocalDateTime.now() + " [RouteSwitch.run] RouteSwitch ЗАПУЩЕН");
        serverJSON = properties.getProperty("serverJSONPath");
        if (serverJSON.equals("default")) {
            serverJSON = "config/servers.json";
        }

        channelJSON = properties.getProperty("channelJSONPath");
        if (channelJSON.equals("default")) {
            channelJSON = "config/channels.json";
        }

        routesJSON = properties.getProperty("routesJSONPath");
        if (routesJSON.equals("default")) {
            routesJSON = "log/routesINFO.json";
        }


        System.out.println(LocalDateTime.now() + " [RouteSwitch.run] Получаю список каналов связи из channels.JSON");
        // Каналы связи и узел для проверки, записанные техником в channels.json
        ArrayList<Channel> channels = Channel.getChannelsFromJSON(new File(channelJSON));
        System.out.println(LocalDateTime.now() + " [RouteSwitch.run] Получаю список серверов со шлюзами из servers.JSON");
        // Корп.сервера со шлюзами, записанными в приоритетном порядке (наверное сотрудниками тех.поддержки)
        ArrayList<Server> servers = Server.getServersFromJSON(new File(serverJSON));
        System.out.println(LocalDateTime.now() + " [RouteSwitch.run] Получаю список текущих маршрутов в системе");
        // получение списка маршрутов заданных в данный момент на устройстве
        ArrayList<Channel> routes = Channel.getChannelsFromJSON(ShellExecutor.getRoutesFromShell());

        System.out.println(LocalDateTime.now() + " [RouteSwitch.run] Проверяю доступность шлюзов и строю маршруты для проверки шлюзов");
        // для каждого канала(указанного техником) проверяется существует ли маршрут, и правильный ли он(если неправильный то удаляется), если не найден маршрут то создается новый.
        Channel.CheckRoutes(channels, routes);
        System.out.println(LocalDateTime.now() + " [RouteSwitch.run] Проверяю доступность маршрута");
        // Проверить, пингуется ли шлюз, если пингуется то пингануть узел для проверки шлюза
        Channel.checkRouteConnection(channels, routes);

        System.out.println(LocalDateTime.now() + " [RouteSwitch.run] Строю маршруты до серверов");
        // Для каждого сервера создать свой маршрут (с наиболее предпочтительным шлюзом)
        for (Server server : servers) {
            server.setRouteToServer(routes, channels);
        }

        // сохранение полученных маршрутов в json (не знаю пока правда зачем это пригодится, если все равно по сути проверять лучше каждый раз, маршруты)
        // Channel.convertToJSON(routes, routesJSON);
        System.out.println(LocalDateTime.now() + " [RouteSwitch.run] RouteSwitch ЗАВЕРШИЛ СВОЮ РАБОТУ");
    }

}
