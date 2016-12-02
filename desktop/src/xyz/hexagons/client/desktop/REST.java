package xyz.hexagons.client.desktop;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class REST {
    public static <T> T get(String url, Class<T> c) {
        try {
            HttpClient httpclient = HttpClients.createDefault();

            HttpGet tokenReq = new HttpGet(url);
            HttpResponse response = httpclient.execute(tokenReq);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream in = entity.getContent();
                T t = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), c);
                in.close();
                return t;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
