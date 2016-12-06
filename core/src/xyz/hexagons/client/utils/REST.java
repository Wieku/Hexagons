package xyz.hexagons.client.utils;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

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

    public static <T> Pair<T, String> getJWS(String url, Class<T> c) {
        try {
            HttpClient httpclient = HttpClients.createDefault();

            HttpGet tokenReq = new HttpGet(url);
            HttpResponse response = httpclient.execute(tokenReq);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream in = entity.getContent();
                JWSObject o = JWSObject.parse(IOUtils.toString(in, "UTF-8"));
                T t = new Gson().fromJson(o.getPayload().toString(), c);
                in.close();
                return new Pair<>(t, o.serialize());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
