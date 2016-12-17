package xyz.hexagons.server.util;

import xyz.hexagons.server.Launcher;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Config {
    private static final String qConfigGet = SqlUtil.getQuery("config/get");

    public static String get(String key) {
        try {
            return Launcher.withConnection(connection -> {
                PreparedStatement statement = connection.prepareStatement(qConfigGet);
                statement.setString(1, key);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    return rs.getString("value");
                }
                return null;
            });
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
