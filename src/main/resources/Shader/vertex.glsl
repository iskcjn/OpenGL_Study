#version 330 core
layout (location = 0) in vec3 position;
layout (location = 2) in vec2 texCoord;  // 新增纹理坐标输入

layout(location = 1) in vec3 normal;


// 投影矩阵
uniform mat4 view;
// 视图矩阵
uniform mat4 projection;

out vec2 TexCoord;  // 向片段着色器传递纹理坐标

out vec3 fragNormal;

void main() {
    // gl_Position = projection * view * model * vec4(position, 1.0);

    gl_Position = projection * view * vec4(position, 1.0);

    fragNormal = normal;

    TexCoord = texCoord; // 传递纹理坐标
}
