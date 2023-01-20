package me.cryptforge.engine.system;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

public interface WindowSystem extends EngineSystem {

    void resize(int width, int height);

    void center();

    void setPosition(int x, int y);

    int width();

    int height();

    int worldWidth();

    int worldHeight();

    @NotNull Matrix3x2f projectionMatrix();

}
