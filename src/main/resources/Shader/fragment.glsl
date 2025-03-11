#version 330 core

in  vec2 TexCoord;// 接收来自顶点着色器的纹理坐标 UV

// 输入的顶点法线
in vec3 fragNormal;

out vec4 fragColor; // 输出的片段颜色

uniform sampler2D textureSampler;  // 启用纹理采样器

void main()
{
    //fragColor = vec4(0.0f, 1.0f, 0.0f, 1.0f);

    fragColor = texture(textureSampler, TexCoord);
}