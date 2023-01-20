package me.cryptforge.engine.system.impl;

import me.cryptforge.engine.system.WindowSystem;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

public final class WindowSystemImpl implements WindowSystem {

    private final long windowId;
    private final Matrix3x2f projectionMatrix;
    private final Vector2i windowSize;
    private final Vector2i worldSize;

    public WindowSystemImpl(long windowId, int width, int height, int worldWidth, int worldHeight) {
        this.windowId = windowId;
        this.windowSize = new Vector2i(width, height);
        this.worldSize = new Vector2i(worldWidth, worldHeight);
        this.projectionMatrix = new Matrix3x2f()
                .view(0f, worldWidth, worldHeight, 0f);
    }

    @Override
    public void resize(int width, int height) {
        glViewport(0, 0, width, height);
        this.windowSize.x = width;
        this.windowSize.y = height;
    }

    @Override
    public void center() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowId, pWidth, pHeight);

            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            setPosition(
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        }
    }

    @Override
    public void setPosition(int x, int y) {
        glfwSetWindowPos(windowId, x, y);
    }

    @Override
    public int width() {
        return windowSize.x;
    }

    @Override
    public int height() {
        return windowSize.y;
    }

    @Override
    public int worldWidth() {
        return worldSize.x;
    }

    @Override
    public int worldHeight() {
        return worldSize.y;
    }

    @Override
    public @NotNull Matrix3x2f projectionMatrix() {
        return projectionMatrix;
    }
}
