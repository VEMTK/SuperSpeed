package superclean.solution.com.superspeed.utils;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpUtil {

    public static final ExecutorService ExecutorService = Executors.newScheduledThreadPool(20);

    public static URLConnection getUrlConnection (String strUrl) {
        try {
            URL url = new URL(strUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
            return connection;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }
}
