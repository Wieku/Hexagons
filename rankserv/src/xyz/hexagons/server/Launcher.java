package xyz.hexagons.server;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.postgresql.util.PSQLException;
import xyz.hexagons.server.auth.*;
import xyz.hexagons.server.rank.*;
import xyz.hexagons.server.util.SqlUtil;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class Launcher {
    private static Server server = new Server(9999);
    private static ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    private static HikariDataSource dataSource = null;
    private static final String qPrepareDatabase = SqlUtil.getQuery("prepare");
    private static final String qCheckConfig = SqlUtil.getQuery("config/check");
    private static final String qInitConfig = SqlUtil.getQuery("config/init");
    private static final Scanner input = new Scanner(System.in);

    private static void prepareDatabase() {
        try {
            withConnection(connection -> {
                Statement statement = connection.createStatement();
                statement.executeUpdate(qPrepareDatabase);

                ResultSet checkResult = statement.executeQuery(qCheckConfig);
                if(checkResult.next()) {
                    if(checkResult.getString("ok").equals("f")) {
                        PreparedStatement cf = connection.prepareStatement(qInitConfig);
                        System.out.println("Google OAuth ClientID:");
                        cf.setString(1, input.nextLine());
                        System.out.println("Google OAuth ClientSecret:");
                        cf.setString(2, input.nextLine());
                        cf.executeUpdate();
                    }
                }

                return null;
            });

        } catch (SQLException e) {
            e.printStackTrace();
			if(e instanceof PSQLException) {
				System.err.println(((PSQLException) e).getServerErrorMessage());
			}
        } catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static <R> R withConnection(SqlFunction<Connection, R> f) throws SQLException, IOException {
        R result;
        Connection conn = dataSource.getConnection();
        try {
            result = f.apply(conn);
        } finally {
            conn.close();
        }

        return result;
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(Settings.instance != null){
                System.out.println("Saving settings");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try {
                    Files.write(gson.toJson(Settings.instance), new File("settings.json"), Charsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            HikariConfig config = new HikariConfig("db.properties");
            //config.addDataSourceProperty("cachePrepStmts", "true");
            //config.addDataSourceProperty("prepStmtCacheSize", "250");
            //config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);

            prepareDatabase();

            ctx.setContextPath("/");
            server.setHandler(ctx);

            ctx.addServlet(MapDone.class,           "/v1/game");
            ctx.addServlet(MapLeaders.class,        "/v1/leaders");
            ctx.addServlet(GetNick.class,           "/v1/nick");
            ctx.addServlet(PlayerRank.class,        "/v1/rank");

            ctx.addServlet(Motd.class,              "/motd");
			ctx.addServlet(ApiLevel.class,          "/api");

            ctx.addServlet(AuthToken.class,         "/v1/auth/challenge");
			ctx.addServlet(AuthPoll.class,          "/v1/auth/poll");

            ctx.addServlet(GoogleAuthGame.class,    "/v1/auth/google/in");
            ctx.addServlet(GoogleAuthSite.class,    "/v1/auth/google/in/site");
            ctx.addServlet(GoogleAuthOut.class,     "/auth/google/out");

			ctx.addServlet(SteamAuthSite.class,     "/v1/auth/steam/in/site");
			ctx.addServlet(SteamAuthOut.class,      "/auth/steam/out");

            server.start();
            server.join();

            dataSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface SqlFunction<T, R> {
        R apply(T t) throws SQLException, IOException;
    }
}
