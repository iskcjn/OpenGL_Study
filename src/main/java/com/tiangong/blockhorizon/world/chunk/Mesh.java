package com.tiangong.blockhorizon.world.chunk;

import com.tiangong.blockhorizon.Game;
import com.tiangong.blockhorizon.world.chunk.block.Block;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class Mesh {
    int _vao, _vbo, _ebo;
    private float[] vertices;
    private float[] uv;
    private int[] indices;

    Vector3i _chunkPos;
    Map<Vector3i, int[]> _chunkMap;

    public Mesh(Vector3i chunkPos, Map<Vector3i, int[]> chunkMap) {
        this.vertices = Block.vertices;
        this.indices = new int[]{};
        this._chunkPos = chunkPos;
        this._chunkMap = chunkMap;
    }

    public void updateIndices() {
        int totalIndices = 0;
        for (int[] blockIndices : _chunkMap.values()) {
            totalIndices += blockIndices.length;
        }
        this.indices = new int[totalIndices];
        int index = 0;
        for (int[] blockIndices : _chunkMap.values()) {
            System.arraycopy(blockIndices, 0, this.indices, index, blockIndices.length);
            index += blockIndices.length;
        }
    }

    public void setMap(Map<Vector3i, int[]> chunkMap) {
        this._chunkMap = chunkMap;
        updateIndices();
    }

    public void bind() {
        // 生成 VAO
        _vao = GL30.glGenVertexArrays();
        // 绑定 VAO
        GL30.glBindVertexArray(_vao);

        // 生成 VBO
        _vbo = GL30.glGenBuffers();
        // 绑定 VBO
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, _vbo);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // 创建EBO
        _ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _ebo);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        // 设置顶点属性指针
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
    }

    public void Draw() {
        // 使用着色器程序
        glUseProgram(Game._shaderProgramId);
        // System.out.println(Game._shaderProgramId);

        // 绑定纹理
        // glActiveTexture(GL_TEXTURE0);
        // glBindTexture(GL_TEXTURE_2D, textureID);
        // glUniform1i(glGetUniformLocation(shaderProgram._shaderProgramID, "texture"), 0);

        // 绑定VAO
        glBindVertexArray(_vao);

        // 设置投影矩阵和视图矩阵
        Matrix4f projection = Game._camera.getProjectionMatrix();
        Matrix4f view = Game._camera.getViewMatrix();

        // 获取并设置 uniform 变量
        int modelLoc = glGetUniformLocation(Game._shaderProgramId, "model");
        // 获取 uniform 变量的位置
        int projLoc = glGetUniformLocation(Game._shaderProgramId, "projectionMatrix");
        int viewLoc = glGetUniformLocation(Game._shaderProgramId, "worldMatrix");

        // 获取 uniform 变量的位置
        int colorUniformLocation = GL20.glGetUniformLocation(Game._shaderProgramId, "inputColor");

        // 将投影矩阵数据传递到着色器
        glUniformMatrix4fv(projLoc, false, projection.get(new float[16]));
        glUniformMatrix4fv(viewLoc, false, view.get(new float[16]));

        int offset = 0;
        for (Map.Entry<Vector3i, int[]> entry : _chunkMap.entrySet()) {
            Vector3i key = entry.getKey();
            int[] blockIndices = entry.getValue();

            // 计算模型矩阵
            Matrix4f modelLoction = new Matrix4f().translate(
                    (_chunkPos.x * Chunk.CHUNK_WIDTH) + key.x,
                    (_chunkPos.y * Chunk.CHUNK_HEIGHT) + key.y,
                    (_chunkPos.z * Chunk.CHUNK_DEPTH) + key.z
            );

            //设置颜色值，例如红色 (1.0, 0.0, 0.0, 1.0)
            float r = 0.0f;
            float g = 0.5f;
            float b = 0.0f;
            float a = 1.0f;
            GL20.glUniform4f(colorUniformLocation, r, g, b, a);
//            if(key.x == 0){
//                // 设置颜色值，例如红色 (1.0, 0.0, 0.0, 1.0)
//                float r = 1.0f;
//                float g = 0.0f;
//                float b = 0.0f;
//                float a = 1.0f;
//                GL20.glUniform4f(colorUniformLocation, r, g, b, a);
//            }

            // 将位置数据传递到着色器
            glUniformMatrix4fv(modelLoc, false, modelLoction.get(new float[16]));

            glDrawElements(GL_TRIANGLES, blockIndices.length, GL_UNSIGNED_INT, offset * Integer.BYTES);
            offset += blockIndices.length;
        }
    }

    public void CleanUp() {
        // 清理资源
        glDeleteBuffers(_vbo);
        glDeleteBuffers(_ebo);
        glDeleteVertexArrays(_vao);

        MemoryUtil.memFree(FloatBuffer.wrap(vertices));
        MemoryUtil.memFree(IntBuffer.wrap(indices));
        _chunkMap.clear();
    }
}
