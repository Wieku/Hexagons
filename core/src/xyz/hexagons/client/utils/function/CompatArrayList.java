package xyz.hexagons.client.utils.function;


import java.util.*;

public class CompatArrayList<E> extends ArrayList<E> {
    public void forEachComp(Consumer<? super E> action) {
        final int size = this.size();
        for (int i=0; i < size; i++) {
            action.accept(this.get(i));
        }
    }
}
