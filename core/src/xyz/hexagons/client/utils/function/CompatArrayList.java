package xyz.hexagons.client.utils.function;


import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Objects;

public class CompatArrayList<E> extends ArrayList<E> {
    public void forEachComp(Consumer<? super E> action) {
        final int size = this.size();
        for (int i=0; i < size; i++) {
            action.accept(this.get(i));
        }
    }
}
