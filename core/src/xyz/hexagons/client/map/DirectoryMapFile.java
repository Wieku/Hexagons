package xyz.hexagons.client.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DirectoryMapFile extends MapFile {
    private final File dir;

    public DirectoryMapFile(File dir) {
        this.dir = dir;
    }

    public DirectoryMapFile(ZipFile zipFile, File file) throws IOException {
        File mapPath = new File(file.getParentFile(), FilenameUtils.removeExtension(file.getName()));
        if(!mapPath.exists()) {
            if(!mapPath.mkdirs())
                throw new IOException("Can't extract map zip " + file.getAbsolutePath());

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if(entry.isDirectory())
                    continue;

                File outFile = new File(mapPath, entry.getName());
                if(!outFile.getParentFile().exists())
                    if(!outFile.getParentFile().mkdirs())
                        throw new IOException("Can't create parent directory for " + outFile.getAbsolutePath());
                if(!outFile.createNewFile())
                    throw new IOException("Can't create file " + outFile.getAbsolutePath());

                OutputStream outStream = new FileOutputStream(outFile);
                InputStream inStream = zipFile.getInputStream(entry);

                System.out.println("Extracting to " + outFile.getAbsolutePath());
                IOUtils.copy(inStream, outStream);

                inStream.close();
                outStream.close();
            }
        }

        zipFile.close();
        if(!file.delete()) {
            System.out.println("Can't remove map zip " + file.getAbsolutePath());
        }

        dir = mapPath;
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
        return Gdx.files.absolute(new File(dir, file).getAbsolutePath());
    }
}
