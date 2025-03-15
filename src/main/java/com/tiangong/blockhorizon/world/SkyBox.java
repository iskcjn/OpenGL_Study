package com.tiangong.blockhorizon.world;

import com.tiangong.blockhorizon.Game;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

public class SkyBox {
    public IntBuffer textureID;
    IntBuffer vaoBuffer;
    IntBuffer vboBuffer;

    public SkyBox() throws IOException {

    }


    public void loadTexture() throws IOException {
        // 加载天空盒纹理
        textureID = BufferUtils.createIntBuffer(1);
        glGenTextures(textureID);
        glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID.get(0));

        String[] faces = {
                "right.jpg",
                "left.jpg",
                "top.jpg",
                "bottom.jpg",
                "front.jpg",
                "back.jpg"
        };

        for (int i = 0; i < faces.length; i++) {
            String path = "D:\\CODE_PJ\\JAVA_Project_Libray\\Block Horizon-N2\\src\\main\\resources\\Textures\\" + faces[i];
            System.out.println(path);
            BufferedImage image = ImageIO.read(new File(path));
            ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

            int[] pixels = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = pixels[y * image.getWidth() + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
                    buffer.put((byte) ((pixel >> 8) & 0xFF));  // G
                    buffer.put((byte) (pixel & 0xFF));         // B
                    buffer.put((byte) ((pixel >> 24) & 0xFF));  // A
                }
            }
            buffer.flip();
            // INFO 改为RGBA渲染
            // glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB, image.getWidth(), image.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    }
    // 天空盒顶点数据
    float[] skyboxVertices = {
            // 顶点位置
            // 右
            -1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            // 左
            -1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,
            // 上
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            // 下
            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,
            // 前
            -1.0f,  1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,
            // 后
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f


    };
    public void bindTexture() {
        // 创建 VAO 和 VBO
        vaoBuffer = BufferUtils.createIntBuffer(1);
        vboBuffer = BufferUtils.createIntBuffer(1);
        glGenVertexArrays(vaoBuffer);
        glGenBuffers(vboBuffer);

        glBindVertexArray(vaoBuffer.get(0));
        glBindBuffer(GL_ARRAY_BUFFER, vboBuffer.get(0));
        // 创建新的缓冲区数据
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(skyboxVertices.length);
        vertexBuffer.put(skyboxVertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
        glBindVertexArray(0);
    }
    public void draw() {
        // 禁用深度写入，只进行深度测试
        glDepthMask(false);
        glDepthFunc(GL_LEQUAL);
        // 使用着色器
        Game._shaderProgramSkyBox.use(Game._skyBox_shaderProgramId);

        // 设置视图矩阵（移除平移部分）
        Matrix4f viewMatrix = Game._camera.getViewMatrix();
        Matrix4f viewWithoutTranslation = new Matrix4f(viewMatrix);
        viewWithoutTranslation.m30(0);
        viewWithoutTranslation.m31(0);
        viewWithoutTranslation.m32(0);

        // 设置投影矩阵
        Matrix4f projectionMatrix = Game._camera.getProjectionMatrix();

        // 设置模型矩阵（单位矩阵）
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity().scale(64.0f);

        // 设置着色器中的矩阵
        Game._shaderProgramSkyBox.setUniformMatrix4fv("model", modelMatrix, Game._skyBox_shaderProgramId);
        // 设置着色器中的矩阵
        Game._shaderProgramSkyBox.setUniformMatrix4fv("view", viewWithoutTranslation, Game._skyBox_shaderProgramId);
        Game._shaderProgramSkyBox.setUniformMatrix4fv("projection", projectionMatrix, Game._skyBox_shaderProgramId);

        // 绑定纹理
        glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID.get(0));

        // 绑定VAO并绘制
        glBindVertexArray(vaoBuffer.get(0));
        glDrawArrays(GL_TRIANGLES, 0, skyboxVertices.length / 3);
        glBindVertexArray(0);

        // 恢复深度写入
        glDepthMask(true);
        glDepthFunc(GL_LESS);
    }

}
