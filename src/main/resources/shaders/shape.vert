#version 330 core
layout(location = 0) in vec4 coordinates;
layout(location = 1) in vec4 color;

out vec4 vertexColor;

uniform mat3x2 projection;

void main() {
    gl_Position = vec4(projection * vec3(coordinates.xy,1.0), 0.0, 1.0);
    vertexColor = color;
}