import java.io.File;
import java.util.ArrayList;

public class RouteSwitch {

    public static void run (String channelSrc, String serversSrc) {
        // Каналы связи и узел для проверки, записанные техником в channels.json
        ArrayList<Channel> channels = Channel.getChannelsFromJSON(new File(channelSrc));
        // Корп.сервера со шлюзами, записанными в приоритетном порядке (наверное сотрудниками тех.поддержки)
        ArrayList<Server> servers = Server.getServersFromJSON(new File(serversSrc));
        // получение списка маршрутов заданных в данный момент на устройстве
        ArrayList<Channel> routes = Channel.getChannelsFromJSON(ShellExecutor.getRoutesFromShell());

        // для каждого канала(указанного техником) проверяется существует ли маршрут, и правильный ли он(если неправильный то удаляется), если не найден маршрут то создается новый.
        Channel.CheckRoutes(channels, routes);
        // Проверить, пингуется ли шлюз, если пингуется то пингануть узел для проверки шлюза
        Channel.checkRouteConnection(channels, routes);

        // Для каждого сервера создать свой маршрут (с наиболее предпочтительным шлюзом)
        for (Server server : servers) {
            server.setRouteToServer(routes, channels);
        }

        // сохранение полученных маршрутов в json (не знаю пока правда зачем это пригодится, если все равно по сути проверять лучше каждый раз, маршруты)
        Channel.convertToJSON(routes, "/home/vshkarubov/IdeaProjects/RouteSwitch/src/main/resources/routesInfo.json");
    }

}
