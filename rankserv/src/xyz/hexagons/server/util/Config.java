package xyz.hexagons.server.util;

import xyz.hexagons.server.Launcher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Config {
    private static final String qConfigGet = SqlUtil.getQuery("config/get");

    public static String get(String key) {
        try {
            PreparedStatement statement = Launcher.connection.prepareStatement(qConfigGet);
            statement.setString(1, key);
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                return rs.getString("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
