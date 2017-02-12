package xyz.hexagons.client.map;


import com.badlogic.gdx.files.FileHandle;
import xyz.hexagons.client.resources.ArchiveFileHandle;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class ZipMapFile extends MapFile {
    private final ZipFile zipFile;

    public ZipMapFile(ZipFile file) {
        this.zipFile = file;
    }

    @Override
    public boolean contains(String file) {
        return zipFile.getEntry(file) != null;
    }

    @Override
    public InputStream getInputStream(String file) throws IOException {
        return zipFile.getInputStream(zipFile.getEntry(file));
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }

    @Override
    public FileHandle getFileHandle(String file) {
        return new ArchiveFileHandle(zipFile, file);
    }
}
