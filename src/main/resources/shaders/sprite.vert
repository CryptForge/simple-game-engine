#version 330 core
layout (location = 0) in vec4 coordinates;
layout (location = 1) in vec4 color;
layout (location = 2) in vec4 uvs;
layout (location = 3) in mat3x2 matrix;

out vec4 vertexColor;
out vec2 textureCoords;

void main() {
    vec2 uv = (coordinates.zw * uvs.xy) + uvs.zw;
    gl_Position = vec4(matrix * vec3(coordinates.xy, 1.0), 0.0, 1.0);
    textureCoords = uv;
    vertexColor = color;
}