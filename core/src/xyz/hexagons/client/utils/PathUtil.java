package xyz.hexagons.client.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class PathUtil {
    public static String getPathForFile(String f) {
        return (Gdx.app.getType()== Application.ApplicationType.Android?"":"assets/") + f;
    }
}
