package xyz.hexagons.server.util;

import com.google.common.io.Resources;
import xyz.hexagons.server.Launcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlUtil {
    public static String getQuery(String query) {
        try {
            return Resources.toString(Resources.getResource("sql/" + query + ".sql"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getIntForQuery(String query, String... params) {
        try {
            return Launcher.withConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement(query);
                for (int i = 0; i < params.length; i++) {
                    statement.setString(i + 1, params[i]);
                }

                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            });
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
