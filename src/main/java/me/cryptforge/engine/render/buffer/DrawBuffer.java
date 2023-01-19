package me.cryptforge.engine.render.buffer;

import me.cryptforge.engine.Freeable;

public interface DrawBuffer extends Freeable {

    void init();

    void flush();

    void clear();

    int capacity();

    int count();

    default boolean hasSpace(int count) {
        return (count() + count) <= capacity();
    }

}
