package xyz.hexagons.client.api;

import java.util.Random;

public class MapUtils {
    public static <T> void shuffle(T[] ar) {
        Random rnd = new Random();
        rnd.setSeed(System.nanoTime());
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            T a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
