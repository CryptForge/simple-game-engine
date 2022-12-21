#version 330 core

in vec4 vertexColor;
in vec2 textureCoords;

out vec4 fragColor;

uniform sampler2D image;

void main() {
    vec4 texColor = vertexColor * texture(image,textureCoords);
//    vec4 texColor = vertexColor;
    if(texColor.a < 0.1) {
        discard;
    }
    fragColor = texColor;
}