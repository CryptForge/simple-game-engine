package me.cryptforge.engine;

import org.jetbrains.annotations.ApiStatus;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.opengl.GL33.*;

@ApiStatus.Experimental
public class Renderer {

    private final Application application;

    private final Matrix3x2f projectionMatrix;

    private final Shader spriteShader;
    private final int quadVAO;

    protected Renderer(Application application) {
        this.application = application;

        projectionMatrix = new Matrix3x2f()
                .view(0f, application.getWidth(), application.getHeight(), 0f);

        // init sprite rendering
        spriteShader = new Shader("/shaders/simple.vert", "/shaders/simple.frag");
        spriteShader.use();
        spriteShader.setInt("image", 0);
        spriteShader.setProjectionMatrix("projection", projectionMatrix);

        quadVAO = glGenVertexArrays();
        final int VBO = glGenBuffers();

        final float[] quadVertices = {
                0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f,

                0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 0.0f
        };

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);

        glBindVertexArray(quadVAO);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    /**
     * Clears the screen with specified color
     * @param red Red (0-255)
     * @param green Green (0-255)
     * @param blue Blue (0-255)
     * @param alpha Alpha (0-1)
     */
    public void clear(int red, int green, int blue, float alpha) {
        glClearColor(
                Math.min(Math.abs(red), 255f) / 255f,
                Math.min(Math.abs(green), 255f) / 255f,
                Math.min(Math.abs(blue), 255f) / 255f,
                alpha);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Creates a sprite builder
     * @param texture Texture to draw
     * @return A new sprite builder
     */
    public SpriteBuilder sprite(Texture texture) {
        return new SpriteBuilder(this, texture);
    }

    public void drawSprite(Texture texture, float posX, float posY, float sizeX, float sizeY, float rotation, float r, float g, float b) {
        spriteShader.use();

        final Matrix4f model = new Matrix4f()
                .translate(posX, posY, 0)
                .translate(0.5f * sizeX, 0.5f * sizeY, 0f)
                .rotate((float) Math.toRadians(rotation), 0, 0, 1)
                .translate(-0.5f * sizeX, -0.5f * sizeY, 0f)
                .scale(sizeX, sizeY, 0);

        spriteShader.setMatrix4f("model", model);
        spriteShader.setVector3f("spriteColor", r, g, b);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        glBindVertexArray(quadVAO);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
    }

    public Vector2f convertMouseToWorld(float mouseX, float mouseY) {
        return new Matrix3x2f(projectionMatrix)
                .unproject(
                        mouseX,
                        application.getHeight() - mouseY,
                        new int[] {0,0,application.getWidth(),application.getHeight()},
                        new Vector2f()
                );
    }
}
