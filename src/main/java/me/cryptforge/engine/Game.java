package me.cryptforge.engine;

import me.cryptforge.engine.render.Renderer;

public interface Game {

    void init();

    void update();

    void render(Renderer renderer);

    void onClose();

}
