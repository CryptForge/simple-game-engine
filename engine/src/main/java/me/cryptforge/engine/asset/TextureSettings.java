package me.cryptforge.engine.asset;

public record TextureSettings(
        boolean generateMipmap,
        TextureFilter downFilter,
        TextureFilter upFilter
) {

    public static final class Builder {
        private boolean generateMipmap = false;
        private TextureFilter downFilter = TextureFilter.LINEAR;
        private TextureFilter upFilter = TextureFilter.LINEAR;

        private Builder() {}

        public Builder generateMipmap(boolean generateMipmap) {
            this.generateMipmap = generateMipmap;
            return this;
        }

        public Builder downscaleFilter(TextureFilter filter) {
            this.downFilter = filter;
            return this;
        }

        public Builder upscaleFilter(TextureFilter filter) {
            this.upFilter = filter;
            return this;
        }

        public TextureSettings build() {
            return new TextureSettings(generateMipmap,downFilter,upFilter);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
