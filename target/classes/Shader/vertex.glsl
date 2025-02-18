#version 410 core
layout (location = 0) in vec3 position;

layout (location = 1) in vec2 texCoord;

// 输出到片段着色器
out vec2 TexCoord;

// 模型矩阵
uniform mat4 model;
// 投影矩阵
uniform mat4 worldMatrix;
// 视图矩阵
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * worldMatrix * model * vec4(position, 1.0);
    TexCoord = texCoord;
}
