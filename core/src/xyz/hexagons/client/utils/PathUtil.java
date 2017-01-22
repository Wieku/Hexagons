package xyz.hexagons.client.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import xyz.hexagons.client.Instance;

import java.io.File;

public class PathUtil {
    public static String getPathForFile(String f) {
        return (Gdx.app.getType() == Application.ApplicationType.Android ? "" : "assets/") + f;
    }

    public static FileHandle getFDFileFile(String f) {
        if(Gdx.app.getType() != Application.ApplicationType.Android)
            return Gdx.files.internal(f);

        return Gdx.files.absolute(Instance.cacheFile.apply(f).getAbsolutePath());
    }
}
