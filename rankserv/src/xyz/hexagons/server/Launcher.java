package xyz.hexagons.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import xyz.hexagons.server.servlets.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Launcher {
    private static Server server = new Server(9999);
    private static ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    public static Connection connection = null;

    private static void prepareDatabase(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `games`(`id` INTEGER PRIMARY KEY AUTOINCREMENT, `map_id` varchar(36) NOT NULL, `score` bigint(20) NOT NULL,  `nick` varchar(24) NOT NULL);");
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

            ctx.addServlet(MapDone.class, "/v0/game");
            ctx.addServlet(MapLeaders.class, "/v0/leaders");

            server.start();
            server.join();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
