package xyz.hexagons.client.utils.function;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
