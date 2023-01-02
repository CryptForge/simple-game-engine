package me.cryptforge.engine.world;

import org.joml.Matrix4f;

public class Transform {

    private final Matrix4f matrix;

    private float x, y;
    private float scaleX, scaleY;
    private float rotation;
    private boolean changed;

    public Transform() {
        matrix = new Matrix4f();
    }

    public Transform setPosition(float x, float y) {
        markChanged();
        this.x = x;
        this.y = y;
        return this;
    }

    public Transform add(float deltaX, float deltaY) {
        markChanged();
        this.x += deltaX;
        this.y += deltaY;
        return this;
    }

    public Transform subtract(float deltaX, float deltaY) {
        markChanged();
        this.x -= deltaX;
        this.y = deltaY;
        return this;
    }

    public Transform setRotation(float rotation) {
        markChanged();
        this.rotation = rotation;
        return this;
    }

    public Transform setScale(float scaleX, float scaleY) {
        markChanged();
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        return this;
    }

    private void markChanged() {
        this.changed = true;
    }

    public Matrix4f matrix() {
        if (!changed) {
            return matrix;
        }
        matrix.translation(x, y, 0)
              .translate(0.5f * scaleX, 0.5f * scaleY, 0f)
              .rotate((float) Math.toRadians(rotation), 0, 0, 1)
              .translate(-0.5f * scaleX, -0.5f * scaleY, 0f)
              .scale(scaleX, scaleY, 0);
        return matrix;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float scaleX() {
        return scaleX;
    }

    public float scaleY() {
        return scaleY;
    }

    public float rotation() {
        return rotation;
    }

    public boolean changed() {
        return changed;
    }
}
