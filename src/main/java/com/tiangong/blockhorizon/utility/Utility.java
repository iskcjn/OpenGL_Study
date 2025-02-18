package com.tiangong.blockhorizon.utility;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Utility {
    /**
     * 从指定路径读取 GLSL 文件内容并返回为字符串
     *
     * @param filePath GLSL 文件的路径
     * @return 文件内容的字符串表示
     * @throws IOException 如果读取文件时发生错误
     */
    public static String readGLSLFile(String filePath) throws IOException {
        StringBuilder shaderSource = new StringBuilder();
        try (InputStream inputStream = Utility.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + filePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    shaderSource.append(line).append("\n");
                }
            }
        }
        return shaderSource.toString();
    }

    /**
     * 加载纹理
     *
     * @param filePath 纹理文件的路径
     * @return 纹理ID
     * @throws IOException 如果读取文件时发生错误
     */
    public static int loadTexture(String filePath) throws IOException {
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // 设置纹理参数
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // GL_NEAREST GL_LINEAR
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // 加载图像
        STBImage.stbi_set_flip_vertically_on_load(true);
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = STBImage.stbi_load(filePath, width, height, channels, 4);
        if (image == null) {
            throw new RuntimeException("无法加载纹理: " + STBImage.stbi_failure_reason());
        }

        // 上传纹理数据
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        // 禁用多重映射
        // glGenerateMipmap(GL_TEXTURE_2D);

        // 释放内存
        STBImage.stbi_image_free(image);

        return textureID;
    }

    /**
     * 合并两个数组
     * @param array1 第一个数组
     * @param array2 第二个数组
     * @return 合并后的数组
     * */
    public static int[] concatenateArrays(int[] array1, int[] array2){
        // 计算新数组的长度
        int newLength = array1.length + array2.length;
        // 创建新数组
        int[] result = new int[newLength];

        // 复制第一个数组到新数组
        System.arraycopy(array1, 0, result, 0, array1.length);
        // 复制第二个数组到新数组
        System.arraycopy(array2, 0, result, array1.length, array2.length);

        return result;
    }
}
