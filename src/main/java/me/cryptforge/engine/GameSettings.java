package me.cryptforge.engine;

import org.joml.Vector2i;

public record GameSettings(
        String title,
        Vector2i worldSize,
        Vector2i windowSize,
        int targetFps,
        int updateRate,
        boolean resizable,
        boolean vsync
) {

    public static class Builder {

        private String title = "Java Game";
        private Vector2i worldSize = new Vector2i(1280, 720);
        private Vector2i windowSize = new Vector2i(worldSize);
        private int targetFps = 60;
        private int updateRate = 60;
        private boolean resizeable = false;
        private boolean vsync = true;

        private Builder() {

        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder worldSize(int width, int height) {
            this.worldSize = new Vector2i(width, height);
            return this;
        }

        public Builder windowSize(int width, int height) {
            this.windowSize = new Vector2i(width, height);
            return this;
        }

        public Builder targetFps(int fps) {
            this.targetFps = fps;
            return this;
        }

        public Builder updateRate(int rate) {
            this.updateRate = rate;
            return this;
        }

        public Builder resizeable(boolean resizeable) {
            this.resizeable = resizeable;
            return this;
        }

        public Builder vsync(boolean vsync) {
            this.vsync = vsync;
            return this;
        }

        public GameSettings build() {
            return new GameSettings(
                    title,
                    worldSize,
                    windowSize,
                    targetFps,
                    updateRate,
                    resizeable,
                    vsync
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
