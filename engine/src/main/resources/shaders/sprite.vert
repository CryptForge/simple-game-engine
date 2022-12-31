#version 330 core
layout(location = 0) in vec4 coordinates;
layout(location = 1) in vec4 color;
layout(location = 2) in mat4 model;

out vec4 vertexColor;
out vec2 textureCoords;

uniform mat4 projection;

void main() {
    gl_Position = projection * model * vec4(coordinates.xy,0.0,1.0);
    textureCoords = coordinates.zw;
    vertexColor = color;
}