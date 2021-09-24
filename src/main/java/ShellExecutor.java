import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Properties;

public class ShellExecutor {
    private static final Runtime run = Runtime.getRuntime();
    private static String cmd;
    private static Process process;
    private static final Properties properties = new Properties();


    // Получение списка маршрутов
    public static String getRoutesFromShell(){
        cmd = "ip -j route";

        try {
            process = run.exec(cmd);
            process.waitFor();
            return new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Добавление маршрута
    public static void addRoute(Channel route) {
        addRoute(route.gateway, route.dst);
    }

    public static void addRoute(String gateway, String dst) {
        try {
            properties.load(new FileInputStream("/home/vshkarubov/IdeaProjects/RouteSwitch/src/main/resources/config.properties"));
            String[] cmd = {"/bin/bash","-c","echo " + properties.getProperty("password") + "| sudo -S ip route add " + dst + " via " + gateway};
            process = run.exec(cmd);
            try {
                if (process.waitFor() == 0) {
                    System.out.println(LocalDateTime.now() + " Add route to " + dst + " via " + gateway + " SUCCESSFUL");
                } else {
                    System.out.println(LocalDateTime.now() + " Add route to " + dst + " via " + gateway + " FAILED");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Удаление маршрута
    public static void removeRoute(Channel route)  {
        try {
            properties.load(new FileInputStream("/home/vshkarubov/IdeaProjects/RouteSwitch/src/main/resources/config.properties"));
            String[] cmd = {"/bin/bash","-c","echo " + properties.getProperty("password") + "| sudo -S ip route delete " + route.dst + " via " + route.gateway};
            process = run.exec(cmd);
            if (process.waitFor() == 0) {
                System.out.println(LocalDateTime.now() + " Remove route to " + route.dst + " via " + route.gateway + " SUCCESSFUL");
            } else {
                System.out.println(LocalDateTime.now() + " Remove route to " + route.dst + " via " + route.gateway + " FAILED");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Проверка доступности (Да, через shell, что поделать)
    public static boolean isReachable(String ipAddr){
        cmd = "ping -n -c 1 " + ipAddr;
        try {
            process = run.exec(cmd);
            return (process.waitFor() == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
