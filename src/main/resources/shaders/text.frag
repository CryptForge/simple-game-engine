#version 330 core

in vec2 TexCoords;
in vec4 textColor;

out vec4 fragColor;

uniform sampler2D text;

void main() {
    vec4 sampled = vec4(1.0, 1.0, 1.0, texture(text, TexCoords).r);
    fragColor = vec4(textColor.xyz, 1.0) * sampled;
}