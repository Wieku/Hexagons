package xyz.hexagons.client.desktop;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.rankserv.AccountManager;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class DesktopAccountManager implements AccountManager {
    @Override
    public void loginGoogle() {
        if(Desktop.isDesktopSupported()) {
            try {
                HttpClient httpclient = HttpClients.createDefault();

                HttpGet req = new HttpGet(Settings.instance.ranking.server + "/auth/google/challenge");
                HttpResponse response = httpclient.execute(req);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream in = entity.getContent();

                    Challenge c = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), Challenge.class);
                    in.close();

                    if(c.challenge != null) {
                        Desktop.getDesktop().browse(new URI(Settings.instance.ranking.server + "/auth/google/in/" + c.challenge));
                    }
                }

            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Challenge implements Serializable {
        public String challenge;
    }
}
