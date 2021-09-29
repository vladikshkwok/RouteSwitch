import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

public class Log {
    public enum severity {
        inf, deb, err
    }

    public static void log(Log.severity severity, String msg) {
        switch (severity) {
            case inf:
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        + " INFO " + msg); break;
            case err:
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        + "\u001B[31m ERROR \u001B[0m" + msg); break;
            case deb:
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        + " DEBUG " + msg); break;
        }
    }

    public static void log(String msg) {
        log(severity.inf, msg);
    }
}