#version 330 core

// 顶点着色器

layout(location=0) in vec3 position;
layout(location=1) in vec3 normal;
layout(location=2) in vec2 texCoord;

// 投影矩阵
uniform mat4 view;
// 视图矩阵
uniform mat4 projection;

// 传递纹理坐标到片段着色器
out vec2 TexCoord;
// 传递法线坐标到片段着色器
out vec3 fragNormal;
// 传递顶点世界坐标到片段着色器
out vec3 vWorldPos;

void main() {
    // 计算顶点的世界坐标
    gl_Position = projection * view * vec4(position, 1.0);

    // 传递法线坐标
    fragNormal = normal;
    // 传递纹理坐标
    TexCoord = texCoord;
    // 传递顶点世界坐标
    vWorldPos = position;
}
