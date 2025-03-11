package com.tiangong.blockhorizon;

import com.tiangong.blockhorizon.utility.Utility;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class ShaderProgram {
    // public int shaderProgramID;

    private long _windowHandle;

    public ShaderProgram(long windowHandle) {
        this._windowHandle = windowHandle;
    }

    /**
     * 创建和编译着色器程序。
     *
     * @return 着色器程序ID
     */
    public int createShaderProgram(String vertexShaderFilePath, String fragmentShaderFilePath) throws IOException {
        // 顶点着色器代码
        String vertexShaderSource = Utility.readGLSLFile(vertexShaderFilePath);

        // 片段着色器代码
        String fragmentShaderSource = Utility.readGLSLFile(fragmentShaderFilePath);

        // 设置当前上下文
        glfwMakeContextCurrent(_windowHandle);
        createCapabilities(); // 初始化 OpenGL 绑定

        // 创建顶点着色器
        int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexShaderSource);
        glCompileShader(vertexShaderID);
        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Vertex Shader compilation failed!");
            System.err.println(glGetShaderInfoLog(vertexShaderID));
            return -1;
        }

        // 创建片段着色器
        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentShaderSource);
        glCompileShader(fragmentShaderID);
        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Fragment Shader compilation failed!");
            System.err.println(glGetShaderInfoLog(fragmentShaderID));
            return -1;
        }

        // 创建着色器程序
        int programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Linking of shaders failed!");
            System.err.println(glGetProgramInfoLog(programID));
            return -1;
        }

        // shaderProgramID = programID;
        return programID;
    }

    public void use() {
        GL20.glUseProgram(Game._shaderProgramId);
    }

    public void setUniform(String name, float value) {
        GL20.glUniform1f(GL20.glGetUniformLocation(Game._shaderProgramId, name), value);
    }

    public void setUniform(String name, int value) {
        GL20.glUniform1i(GL20.glGetUniformLocation(Game._shaderProgramId, name), value);
    }

    public void setUniform(String name, float x, float y, float z) {
        GL20.glUniform3f(GL20.glGetUniformLocation(Game._shaderProgramId, name), x, y, z);
    }

    public void setUniformMatrix4fv(String name, float[] matrix) {
        // 获取Uniform变量的位置
        int location = GL20.glGetUniformLocation(Game._shaderProgramId, name);
        // 创建一个FloatBuffer并填充矩阵数据
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(matrix);
        buffer.flip(); // 翻转缓冲区以准备读取
        // 设置Uniform矩阵
        GL20.glUniformMatrix4fv(location, false, buffer);
    }

    // 删除着色器程序
    public void delete(int programId) {
        GL20.glDeleteProgram(programId);
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