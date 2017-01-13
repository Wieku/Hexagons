package xyz.hexagons.server;

import com.google.gson.JsonObject;
import xyz.hexagons.server.util.Config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiLevel extends HttpServlet {
	public static final int MIN_VERSION = 1;
	public static final int CURRENT_VERSION = 1;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		JsonObject m = new JsonObject();
		m.addProperty("minVersion", MIN_VERSION);
		m.addProperty("curVersion", CURRENT_VERSION);
		resp.getWriter().print(m.toString());
	}
}
