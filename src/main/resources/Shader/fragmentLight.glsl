#version 330 core

// 接收来自顶点着色器的纹理坐标 UV
in  vec2 TexCoord;
// 输入的顶点世界坐标
in vec3 vWorldPos;
// 输入的顶点法线
in vec3 fragNormal;


// 输出的片段颜色
out vec4 fragColor;


// 启用纹理采样器
uniform sampler2D textureSampler;


// 镜面反射颜色
uniform vec3 uSpecColor;
// 高光指数
uniform float uShininess;
// 光源方向（平行光，即太阳光）
uniform vec3 uLightDir;
// 光源颜色
uniform vec3 uLightColor;
// 摄像机位置
uniform vec3 uCameraPos;

void main()
{
    //fragColor = vec4(0.0f, 1.0f, 0.0f, 1.0f);

    // 归一化法线
    vec3 worldNormal = normalize(fragNormal);
    // 归一化光源方向
    vec3 lightDir = normalize(-uLightDir);
    // 计算视角方向（从片段到摄像机）
    vec3 viewDir = normalize(uCameraPos - vWorldPos);
    // 计算反射方向
    vec3 reflectDir = reflect(-lightDir, worldNormal);

    // 纹理颜色
    vec3 uColor = texture(textureSampler, TexCoord).rgb;

    // 环境光
    vec3 ambient = vec3(0.1) * uColor;

    // 漫反射
    float NdotL = max(0.0, dot(worldNormal, lightDir));
    vec3 diffuse = uLightColor * uColor * NdotL;

    // 镜面反射
    vec3 specular = uSpecColor * pow(max(0.0, dot(viewDir, reflectDir)), uShininess);

    // 最终颜色
    vec3 finalColor = ambient + diffuse + specular;

    fragColor = vec4(finalColor, 1.0);

    // fragColor = texture(textureSampler, TexCoord);
}