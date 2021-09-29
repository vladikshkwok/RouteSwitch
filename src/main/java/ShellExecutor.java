import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


public class ShellExecutor {
    private static final Runtime run = Runtime.getRuntime();
    private static String cmd;
    private static Process process;

    // Получение списка маршрутов
    public static String getRoutesFromShell() {
        // cmd = "ip -j route";
//        cmd = "lib/routejson.sh";
        String[] cmd = {"/bin/sh", "-c", "ip route | grep via"};
        Log.log("[getRoutesFromShell] Получение списка маршрутов");
        try {
            process = run.exec(cmd);
            process.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String newline, jsonString = "";
            while ((newline = br.readLine()) != null) {
                newline = newline.replaceAll(" via ", "\", \"gateway\":\"").
                        replaceAll("( dev.*)", "\" },").
                        replaceAll("(^)", "{ \"dst\":\"");
                Log.log("[getRoutesFromShell] получен маршрут " + newline);
                jsonString += newline;
            }
            jsonString = jsonString.replaceAll("(^)", "[ ").replaceAll("(,$)", " ]");
            if (jsonString.equals("")) {
                Log.log(Log.severity.err, "[getRoutesFromShell] Не удалось получить список маршрутов");
                return null;
            }
            return jsonString;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Добавление маршрута
    public static boolean addRoute(Channel route) {
        return addRoute(route.gateway, route.dst);
    }

    public static boolean addRoute(String gateway, String dst) {

        try {
            cmd = "ip route add " + dst + " via " + gateway;
            process = run.exec(cmd);

            if (process.waitFor() == 0) {
                Log.log("[addRoute] Добавление маршрута " + dst + " via " + gateway + " SUCCESSFUL");
                return true;
            } else {
                Log.log(Log.severity.err, "[addRoute] Добавление маршрута " + dst + " via " + gateway + " FAILED");
                return false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Удаление маршрута
    public static boolean removeRoute(Channel route) {
        return removeRoute(route.gateway, route.dst);
    }


    public static boolean removeRoute(String gateway, String dst) {
        try {
            cmd = "ip route delete " + dst + " via " + gateway;
            process = run.exec(cmd);

            if (process.waitFor() == 0) {
                Log.log("[removeRoute] Удаление маршрута " + dst + " via " + gateway + " SUCCESSFUL");
                return true;
            } else {
                Log.log(Log.severity.err, "[removeRoute] Удаление маршрута " + dst + " via " + gateway + " FAILED");
                return false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
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
