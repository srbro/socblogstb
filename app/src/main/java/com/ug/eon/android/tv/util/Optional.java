package com.ug.eon.android.tv.util;

/**
 * Created by goran.arandjelovic on 5/3/18.
 */

import java.util.Objects;

public class Optional<T> {
    private T value;

    public interface Function<T, R> {
        R apply(T value);
    }

    public interface Action<T> {
        void apply(T value);
    }

    private Optional() { this.value = null; }
    private Optional(T value) { this.value = Objects.requireNonNull(value); }
    public static <T> Optional<T> empty() { return new Optional<>(); }
    public static <T> Optional<T> of(T value) { return new Optional<>(value); }

    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public <R> Optional<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper);
        if(value == null) {
            return empty();
        }
        return Optional.ofNullable(mapper.apply(value));
    }

    public void ifPresent(Action<T> action) {
        if (value != null) {
            action.apply(value);
        }
    }

    public boolean isPresent() {
        return value != null;
    }

    public T get() {
        return value;
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }
}
