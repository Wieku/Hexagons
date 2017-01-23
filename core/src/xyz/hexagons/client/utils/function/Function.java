package xyz.hexagons.client.utils.function;

@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
