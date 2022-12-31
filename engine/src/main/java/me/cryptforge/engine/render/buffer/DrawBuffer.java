package me.cryptforge.engine.render.buffer;

public interface DrawBuffer {

    void init();

    void flush();

    void clear();

    void free();

    int capacity();

    int count();

    default boolean hasSpace(int count) {
        return (count() + count) >= capacity();
    }

}
