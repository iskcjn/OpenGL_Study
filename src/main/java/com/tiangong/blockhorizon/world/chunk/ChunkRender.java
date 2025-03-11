package com.tiangong.blockhorizon.world.chunk;

import com.tiangong.blockhorizon.Game;
import com.tiangong.blockhorizon.utility.Utility;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

public class ChunkRender {
    // vao, vbo
    int _vao = -1, _vbo = -1;
    public int textureID;
    public float[] vertices;

    public Vector3i chunkPos;

    public ChunkRender(Vector3i chunkPos, int textureID) {
        vertices = new float[0];
        this.textureID = textureID;
        this.chunkPos = chunkPos;
    }

    public void updateVertices(float[] newVertices) {
        this.vertices = newVertices;

        if (_vbo != -1) {
            // 绑定现有VBO
            glBindBuffer(GL_ARRAY_BUFFER, _vbo);

            // 创建新的缓冲区数据
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
            vertexBuffer.put(vertices).flip();

            // 更新缓冲区数据（使用DYNAMIC_DRAW更适合频繁更新）
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

            // 解绑VBO
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
    }

    public void bind(){
        // 添加空数据保护
        if (vertices.length == 0) {
            System.err.println("Warning: Trying to bind empty vertices");
            return;
        }

        // 清理旧资源
        if (_vao != -1) {
            glDeleteVertexArrays(_vao);
            _vao = -1;
        }
        if (_vbo != -1) {
            glDeleteBuffers(_vbo);
            _vbo = -1;
        }

        // 生成 VAO
        _vao = glGenVertexArrays();
        // 绑定 VAO
        glBindVertexArray(_vao);

        // 生成 VBO
        _vbo = glGenBuffers();
        // 绑定 VBO
        glBindBuffer(GL_ARRAY_BUFFER, _vbo);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

        // 设置顶点属性指针
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // 法线坐标属性
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // 纹理坐标属性
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);



        // 解绑VAO，安全起见
        glBindVertexArray(0);
    }

    public void draw() {
        // System.out.println("vertices.length: " + vertices.length);

        // 使用着色器程序
        // glUseProgram(Game._shaderProgramId);
        Game._shaderProgram.use();

        // 绑定纹理
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, Game._textureId);

        // 绑定VAO
        glBindVertexArray(_vao);


        // 设置投影矩阵和视图矩阵
        Matrix4f projection = Game._camera.getProjectionMatrix();
        Matrix4f view = Game._camera.getViewMatrix();

        // 获取 uniform 变量的位置
        int projLoc = glGetUniformLocation(Game._shaderProgramId, "projection");
        int viewLoc = glGetUniformLocation(Game._shaderProgramId, "view");

        // 将投影矩阵数据传递到着色器
        glUniformMatrix4fv(projLoc, false, projection.get(new float[16]));
        glUniformMatrix4fv(viewLoc, false, view.get(new float[16]));

        // 传入UV坐标
        glUniform1i(glGetUniformLocation(Game._shaderProgramId, "textureSampler"), 0);

        // 设置Uniform变量
        Game._shaderProgram.setUniform("uSpecColor", 0.3f, 0.3f, 0.3f); // 镜面反射颜色
        Game._shaderProgram.setUniform("uShininess", 32.0f); // 高光指数
        Game._shaderProgram.setUniform("uLightDir", 10.0f, -1.0f, -10.0f); // 光源方向
        Game._shaderProgram.setUniform("uLightColor", 1.0f, 1.0f, 1.0f); // 光源颜色
        Vector3f _cameraPos = Game._camera.getPosition();
        Game._shaderProgram.setUniform("uCameraPos", _cameraPos.x, _cameraPos.y, _cameraPos.z); // 摄像机位置

        // 添加实际绘制调用
        glDrawArrays(GL_TRIANGLES, 0, vertices.length / 8); // 每个顶点包含3位置+2纹理坐标

        // 添加清理代码
        glBindVertexArray(0);
        glUseProgram(0);
    }

    // 添加延迟初始化方法
    public void initBuffer() {
        if (vertices.length > 0 && _vao == -1) {
            bind();
        }
    }

    public void cleanup() {
        if (_vao != -1) {
            glDeleteVertexArrays(_vao);
            _vao = -1;
        }
        if (_vbo != -1) {
            glDeleteBuffers(_vbo);
            _vbo = -1;
        }
    }
}
/*
MIT License

Copyright <2025> <iskcjn>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



版权所有 (2025) <iskcjn>
特此授予任何获得本软件和相关文档文件（“软件”）的人员无偿许可，允许其不受限制地处理本软件，包括但不限于使用、复制、修改、合并、发布、分发、许可或出售软件副本的权利，并允许接收本软件的人员做同样的事情，但须遵守以下条件：
上述版权声明和本许可声明应包含在本软件的所有副本或重要部分中。
本软件按“原样”提供，不附带任何明示或暗示的保证，包括但不限于适销性、特定用途适用性和非侵权的保证。在任何情况下，作者或版权所有者均不对任何索赔、损害或其他责任负责，无论是因合同、侵权行为还是其他原因引起的，与本软件或其使用或其他交易有关。
*/