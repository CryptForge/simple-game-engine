#version 330 core
layout (location = 0) in vec4 coordinates;
layout (location = 1) in vec4 color;
layout (location = 2) in mat3x2 model;

out vec4 vertexColor;
out vec2 textureCoords;

uniform mat3x2 projection;

void main() {
    vec2 position = model * vec3(coordinates.xy, 1.0);
    gl_Position = vec4(projection * vec3(position, 1.0), 0.0, 1.0);
    textureCoords = coordinates.zw;
    vertexColor = color;
}