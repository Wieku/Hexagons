package xyz.hexagons.server.util;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SqlUtil {
    public static String getQuery(String query) {
        try {
            return Resources.toString(Resources.getResource("sql/" + query + ".sql"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
