#version 330 core

in  vec2 TexCoord;

out vec4 fragColor;

uniform sampler2D texture_sampler;

// 定义一个 uniform 变量来接收传入的颜色
// uniform vec4 inputColor;

void main()
{
    fragColor = texture(texture_sampler, TexCoord);
    // fragColor = vec4(0.0f, 1.0f, 0.0f, 1.0f);
    // fragColor = inputColor;
}