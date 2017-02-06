package xyz.hexagons.client.utils.function;

@FunctionalInterface
public interface Supplier<T> {
    T get();
}
