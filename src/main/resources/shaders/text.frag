#version 330 core

in vec2 textureCoords;
in vec4 textColor;

out vec4 fragColor;

uniform sampler2D bitmap;

void main() {
    vec4 sampled = vec4(1.0, 1.0, 1.0, texture(bitmap, textureCoords).r);
    fragColor = vec4(textColor.xyz, 1.0) * sampled;
}