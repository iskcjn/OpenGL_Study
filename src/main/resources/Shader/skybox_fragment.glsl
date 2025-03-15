#version 330 core

in  vec3 TexCoord;// 接收来自顶点着色器的纹理坐标 UV

out vec4 FragColor; // 输出的片段颜色

uniform samplerCube skybox;  // 启用纹理采样器


void main() {
    vec3 color = texture(skybox, TexCoord).rgb;
    vec3 ambient = vec3(0.8) * color;
    // FragColor = texture(skybox, TexCoord);
    FragColor = vec4(ambient, 1.0);
}
