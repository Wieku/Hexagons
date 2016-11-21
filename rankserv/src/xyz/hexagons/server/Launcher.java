package xyz.hexagons.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import xyz.hexagons.server.servlets.*;
import xyz.hexagons.server.util.SqlUtil;

import java.sql.*;

public class Launcher {
    private static Server server = new Server(9999);
    private static ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    public static Connection connection = null;
    private static final String qPerpareDatabase = SqlUtil.getQuery("prepare");
    private static final String qCheckConfig = SqlUtil.getQuery("config/check");
    private static final String qInitConfig = SqlUtil.getQuery("config/init");

    private static void prepareDatabase(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(qPerpareDatabase);

            ResultSet checkResult = statement.executeQuery(qCheckConfig);
            if(checkResult.next()) {
                if(checkResult.getInt("ok") == 0) {
                    statement.executeUpdate(qInitConfig);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rankserv.db");
            prepareDatabase(connection);

            ctx.setContextPath("/");
            server.setHandler(ctx);

            ctx.addServlet(MapDone.class,       "/v0/game");
            ctx.addServlet(MapLeaders.class,    "/v0/leaders");
            ctx.addServlet(Motd.class,          "/motd");

            server.start();
            server.join();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
