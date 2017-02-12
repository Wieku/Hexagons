package xyz.hexagons.client.map;


import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DirectoryZipFile extends MapFile {
    private final File dir;

    public DirectoryZipFile(File dir) {
        this.dir = dir;
    }

    @Override
    public boolean contains(String file) {
        return new File(dir, file).exists();
    }

    @Override
    public InputStream getInputStream(String file) throws IOException {
        return new FileInputStream(new File(dir, file));
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public FileHandle getFileHandle(String file) {
        return new FileHandle(new File(dir, file));
    }
}
