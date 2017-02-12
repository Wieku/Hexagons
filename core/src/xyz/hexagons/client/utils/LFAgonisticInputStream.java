package xyz.hexagons.client.utils;


import java.io.IOException;
import java.io.InputStream;

public class LFAgonisticInputStream extends InputStream {
    private final InputStream is;

    public LFAgonisticInputStream(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        int c;
        do {
            c = is.read();
        } while(c != -1 && c == '\r');
        return c;
    }
}
