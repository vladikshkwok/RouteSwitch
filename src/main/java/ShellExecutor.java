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


    public ShellExecutor () {
        try {
            properties.load(new FileInputStream("/home/ts/routeSwitch/config/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Получение списка маршрутов
    public static String getRoutesFromShell(){
        // cmd = "ip -j route";
        cmd = "/home/ts/routeSwitch/lib/routejson.sh";
        System.out.println(cmd);
        try {
            process = run.exec(cmd);
            process.waitFor();
            return new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
            cmd = "ip route add " + dst + " via " + gateway;
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
        removeRoute(route.gateway, route.dst);
    }


    public static void removeRoute(String gateway, String dst) {
        try {
            cmd = "ip route delete " + dst + " via " + gateway;
            process = run.exec(cmd);
            try {
                if (process.waitFor() == 0) {
                    System.out.println(LocalDateTime.now() + " Remove route to " + dst + " via " + gateway + " SUCCESSFUL");
                } else {
                    System.out.println(LocalDateTime.now() + " Remove route to " + dst + " via " + gateway + " FAILED");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
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
