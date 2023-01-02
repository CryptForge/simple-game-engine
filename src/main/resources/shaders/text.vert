#version 330 core
layout(location = 0) in vec4 coordinates;
layout(location = 1) in vec4 inTextColor;

out vec2 TexCoords;
out vec4 textColor;

uniform mat4 projection;

void main() {
    gl_Position = projection * vec4(coordinates.xy, 0.0, 1.0);
    TexCoords = coordinates.zw;
    textColor = inTextColor;
}