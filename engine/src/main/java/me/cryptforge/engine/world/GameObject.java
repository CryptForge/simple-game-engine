package me.cryptforge.engine.world;

import me.cryptforge.engine.asset.Texture;
import me.cryptforge.engine.render.Color;

import java.util.UUID;

public abstract class GameObject {

    private final UUID uuid;
    private final Transform transform;
    private Color color;
    private Texture texture;

    public GameObject(UUID uuid) {
        this.uuid = uuid;
        this.transform = new Transform();
        this.color = Color.WHITE;
    }

    public GameObject() {
        this(UUID.randomUUID());
    }

    public void update() {}

    public Transform transform() {
        return transform;
    }

    public Texture texture() {
        return texture;
    }

    public Color color() {
        return color;
    }

    public UUID uuid() {
        return uuid;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
