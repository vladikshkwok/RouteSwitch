import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

public class ShellExecutor {
    private static final Runtime run = Runtime.getRuntime();
    private static String cmd;
    private static Process process;
    private static final Properties properties = new Properties();


    public ShellExecutor() {
        System.out.println(LocalDateTime.now() + " [ShellExecutor] Загрузка настроек из config/config.properties");
        try {
            properties.load(new FileInputStream("config/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Получение списка маршрутов
    public static String getRoutesFromShell() {
        // cmd = "ip -j route";
//        cmd = "lib/routejson.sh";
        String[] cmd = {"/bin/sh", "-c", "ip route | grep via"};
        System.out.println(LocalDateTime.now() + " [getRoutesFromShell] Получение списка маршрутов");
        try {
            process = run.exec(cmd);
            process.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String newline, jsonString = "";
            while ((newline = br.readLine()) != null) {
                newline = newline.replaceAll(" via ", "\", \"gateway\":\"").
                        replaceAll("( dev.*)", "\" },").
                        replaceAll("(^)", "{ \"dst\":\"");
                System.out.println(LocalDateTime.now() + " [getRoutesFromShell] получен маршрут " + newline);
                jsonString += newline;
            }
            jsonString = jsonString.replaceAll("(^)", "[ ").replaceAll("(,$)", " ]");
            if (jsonString.equals("")) {
                System.out.println(LocalDateTime.now() + " [getRoutesFromShell] Не удалось получить список маршрутов");
                return null;
            }
            return jsonString;
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
            cmd = "ip route add " + dst + " via " + gateway;
            process = run.exec(cmd);
            try {
                if (process.waitFor() == 0) {
                    System.out.println(LocalDateTime.now() + " [addRoute] Добавление маршрута " + dst + " via " + gateway + " SUCCESSFUL");
                } else {
                    System.out.println(LocalDateTime.now() + " [addRoute] Добавление маршрута " + dst + " via " + gateway + " FAILED");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Удаление маршрута
    public static void removeRoute(Channel route) {
        removeRoute(route.gateway, route.dst);
    }


    public static void removeRoute(String gateway, String dst) {
        try {
            cmd = "ip route delete " + dst + " via " + gateway;
            process = run.exec(cmd);
            try {
                if (process.waitFor() == 0) {
                    System.out.println(LocalDateTime.now() + " [removeRoute] Удаление маршрута " + dst + " via " + gateway + " SUCCESSFUL");
                } else {
                    System.out.println(LocalDateTime.now() + " [removeRoute] Удаление маршрута " + dst + " via " + gateway + " FAILED");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Проверка доступности (Да, через shell, что поделать)
    public static boolean isReachable(String ipAddr) {
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
