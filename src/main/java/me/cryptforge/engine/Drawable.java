package me.cryptforge.engine;

import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.render.Color;
import org.joml.Matrix3x2f;

public interface Drawable {

    Matrix3x2f transform();

    Color color();

    Texture texture();

}
