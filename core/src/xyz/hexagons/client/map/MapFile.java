package xyz.hexagons.client.map;


import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.InputStream;

abstract public class MapFile {
    public abstract boolean contains(String file);
    public abstract InputStream getInputStream(String file) throws IOException;
    public abstract void close() throws IOException;
    public abstract FileHandle getFileHandle(String file);
}
