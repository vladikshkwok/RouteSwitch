import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellExecutor {
    private static final Runtime run = Runtime.getRuntime();
    private static String cmd;
    private static Process process;

    public static String getRoutesFromShell() throws IOException, InterruptedException {
        cmd = "ip -j route";

        process = run.exec(cmd);
        process.waitFor();
        return new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
    }

    public static void addRoute(Channel route) throws IOException, InterruptedException {
        cmd = "ip route add " + route.dst + " via " + route.gateway;
        process = run.exec(cmd);
        process.waitFor();
    }

    public static void removeRoute(Channel route) throws IOException, InterruptedException {
        cmd = "ip route remove " + route.dst + " via " + route.gateway;
        process = run.exec(cmd);
        process.waitFor();

    }
}
