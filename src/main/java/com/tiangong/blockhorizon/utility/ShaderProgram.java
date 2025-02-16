package com.tiangong.blockhorizon.utility;

import org.lwjgl.opengl.GL20;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final long _windowHandle;

    public ShaderProgram(long handle) {
        this._windowHandle = handle;
    }

    /**
     * 创建和编译着色器程序。
     *
     * @return 着色器程序ID
     */
    public int createShaderProgram() throws IOException {
        // 顶点着色器代码
        String vertexShaderSource = Utility.readGLSLFile("Shader/vertex.glsl");

        // 片段着色器代码
        String fragmentShaderSource = Utility.readGLSLFile("Shader/fragment.glsl");

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

        return programID;
    }

//    // 构造函数，创建着色器程序
//    public ShaderProgram() {
//        programId = GL20.glCreateProgram();
//    }
//
//    // 加载并编译顶点着色器（vertex shader）
//    public void attachVertexShader(String filename) throws Exception {
//        int shaderId = loadShader(filename, GL20.GL_VERTEX_SHADER);
//        GL20.glAttachShader(programId, shaderId);
//    }
//
//    // 加载并编译片段着色器（fragment shader）
//    public void attachFragmentShader(String filename) throws Exception {
//        int shaderId = loadShader(filename, GL20.GL_FRAGMENT_SHADER);
//        GL20.glAttachShader(programId, shaderId);
//    }
//
//    // 链接着色器程序
//    public void link() {
//        GL20.glLinkProgram(programId);
//
//        // 检查链接状态
//        int status = GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS);
//        if (status != GL11.GL_TRUE) {
//            String error = GL20.glGetProgramInfoLog(programId);
//            throw new RuntimeException("Error linking shader program: " + error);
//        }
//    }
//
//    // 激活着色器程序
//    public void use() {
//        GL20.glUseProgram(programId);
//    }
//
//    // 获取着色器程序 ID
//    public int getProgramId() {
//        return programId;
//    }
//
//    // 加载并编译着色器
//    private int loadShader(String filename, int shaderType) throws Exception {
//        int shaderId = GL20.glCreateShader(shaderType);
//
//        // 读取着色器文件内容
//        String shaderCode = Utility.readGLSLFile(filename);
//
//        // 编译着色器
//        GL20.glShaderSource(shaderId, shaderCode);
//        GL20.glCompileShader(shaderId);
//
//        // 检查编译状态
//        int status = GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS);
//        if (status != GL11.GL_TRUE) {
//            String error = GL20.glGetShaderInfoLog(shaderId);
//            GL20.glDeleteShader(shaderId);
//            throw new RuntimeException("Error compiling shader: " + error);
//        }
//
//        return shaderId;
//    }
//
    // 删除着色器程序
    public void delete(int programId) {
        GL20.glDeleteProgram(programId);
    }
}
