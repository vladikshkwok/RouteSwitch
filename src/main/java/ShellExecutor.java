import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;


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
        File routesJSON = new File("routesINFO.json");
        cmd = "ping -n -c 1 " + ipAddr;
        try {
            process = run.exec(cmd);
            InputStream stdIn = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdIn);
            BufferedReader br = new BufferedReader(isr);
            String outExec;
            String resLine = "";
            while ((outExec = br.readLine()) != null) {
                Log.log("[isReachable] Result for ping " + ipAddr + " is: " + outExec);
                if (outExec.matches("[0-9]+ packets.*"))
                    resLine=outExec;
            }
            boolean ecexCode = process.waitFor() == 0;
            if (routesJSON.exists()) {
                ArrayList<Server> servers = Server.getServersFromJSON(routesJSON);
                for (Server server : servers) {
                    File serverGwPingDir = new File("log/route_log_for_"+server.dst);

                    if (!serverGwPingDir.exists()) {
                        serverGwPingDir.mkdir();
                    }
                    for (int i=0; i < server.channels_info.size(); i++) {
                        if (ipAddr.equals(server.channels_info.get(i).gateway)) {
                            Log.log("[isReachable] Gateway " + (int) (i + 1) +
                                    "_" + server.channels_info.get(i).gateway + " for server " +
                                    server.dst + " was active: " +
                                    server.channels_info.get(i).isGwConnected);
                            PrintStream pingLog = new PrintStream(
                                    new FileOutputStream(
                                            "log/route_log_for_" + server.dst +
                                                    "/channel" + (int) (i + 1) + "." + server.channels_info.get(i).gateway,
                                            true));
                            if (server.channels_info.get(i).isGwConnected != ecexCode ||
                                    new File("log/route_log_for_" + server.dst + "/channel" +
                                            (int) (i + 1) + "." + server.channels_info.get(i).gateway).length() == 0) {
                                Process change_executability;
                                if (ecexCode)
                                    change_executability = run.exec("chmod +x log/route_log_for_" + server.dst +
                                            "/channel" + (int) (i + 1) + "." + server.channels_info.get(i).gateway);
                                else
                                    change_executability = run.exec("chmod -x log/route_log_for_" + server.dst +
                                            "/channel" + (int) (i + 1) + "." + server.channels_info.get(i).gateway);
                                pingLog.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " " + resLine);
                                change_executability.waitFor();
                            }
                        }
                    }
                }
            }
            return ecexCode;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
