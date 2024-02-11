package org.cptgum.superhopperswebui.utils.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.cptgum.superhopperswebui.utils.LoggerUtils;
public class FetchIP {

    public static String getIP() {
        try {
            URL url = new URL("http://checkip.amazonaws.com");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            return br.readLine().trim();
        } catch (IOException e) {
            LoggerUtils.logError("Error fetching external IP" + e);
            return null;
        }
    }
}
