#version 330 core
layout(location = 0) in vec4 position;
layout(location = 1) in vec4 color;

out vec4 vertexColor;

uniform mat4 projection;

void main() {
    vertexColor = color;
    gl_Position = projection * vec4(position.xy,0.0,1.0);
}