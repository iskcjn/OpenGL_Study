#version 330 core

layout (location = 0) in vec3 position;
// layout (location = 1) in vec2 texCoord;  // 新增纹理坐标输入

// 模型矩阵
uniform mat4 model;
// 投影矩阵
uniform mat4 view;
// 视图矩阵
uniform mat4 projection;
// 缩放矩阵
// uniform mat4 scale;

out vec3 TexCoord;  // 向片段着色器传递纹理坐标

void main() {
//    //gl_Position = projection * view * vec4(position, 1.0);
//    vec4 pos = projection * view * model * vec4(position, 1.0);
//    gl_Position = pos.xyww;

    // mat4 viewWithoutTranslation = mat4(mat3(view)); // 移除平移分量

    //gl_Position = projection * view * model * vec4(position, 1.0);
    //TexCoord = position; // 传递纹理坐标

    vec4 pos = view * model * vec4(position, 1.0);
    gl_Position = projection * pos; // 确保深度测试通过
    TexCoord = position;
    // gl_Position = gl_Position.xyww; // 保证深度测试始终通过
}
