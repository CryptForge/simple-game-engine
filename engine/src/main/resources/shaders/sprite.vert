#version 330 core
layout(location = 0) in vec2 position;
layout(location = 1) in vec4 color;
layout(location = 2) in vec2 texCoord;

out vec4 vertexColor;
out vec2 textureCoords;

uniform mat4 model;
uniform mat4 projection;

void main() {
    vertexColor = color;
    textureCoords = texCoord;
    mat4 pm = projection * model;
    gl_Position = pm * vec4(position,0.0,1.0);
}