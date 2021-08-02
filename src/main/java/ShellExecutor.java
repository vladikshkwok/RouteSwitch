import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ShellExecutor {
    public static String getRoutesFromShell() throws IOException, InterruptedException {
        String cmd = "ip -j route";
        Runtime run = Runtime.getRuntime();
        Process process = run.exec(cmd);
        process.waitFor();
        return new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
    }
}
