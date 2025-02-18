package com.tiangong.blockhorizon;

import com.tiangong.blockhorizon.utility.ShaderProgram;
import com.tiangong.blockhorizon.utility.Utility;
import com.tiangong.blockhorizon.world.World;
import com.tiangong.blockhorizon.world.chunk.Chunk;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class Game implements Runnable {
    // 窗口
    public static Window _window;
    // 摄像机
    public static Camera _camera;
    // 世界
    public static World _world;
    // 着色器
    public static ShaderProgram _shaderProgram;
    // 着色器程序ID
    public static int _shaderProgramId;
    // 纹理ID
    public static int _textureId;

    // 窗口大小
    public static final int Win_width = 1280;
    public static final int Win_height = 720;
    // 窗口标题
    public static final String Win_title = "Block Horizon";

    @Override
    public void run() {
        System.out.println("Game started!");
        try {
            init();
            loop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        release();
    }

    public void init() throws IOException {
        // 实例化窗口
        _window = new Window(Win_width, Win_height, Win_title);
        // 初始化窗口
        _window.init();
        // 实例化着色器
        _shaderProgram = new ShaderProgram(Window._WINDOW_HANDLE);
        _shaderProgramId = _shaderProgram.createShaderProgram();
        // 实例化摄像机
        _camera = new Camera(Win_width, Win_height, new Vector3f(0.0f, 0.0f, 3.0f));
        // 加载纹理
        _textureId = Utility.loadTexture("D:\\CODE_PJ\\JAVA_Prpject_Libray\\NewWorld-N1\\src\\main\\resources\\Texture\\Block_atlas.png");


        // 实例化世界
        _world = new World();
    }

    // 游戏循环
    private void loop() {
        // 创建 GLCapabilities 实例，并使 OpenGL 绑定可供使用。
        GL.createCapabilities();
        // 设置清除颜色
        GL11.glClearColor(0.5f, 0.5f, 0.85f, 0.0f);

        float lastTime = 0.0f;
        int counter = 0;
        // 游戏循环
        while (!glfwWindowShouldClose(Window._WINDOW_HANDLE)) {
            // 清除帧缓冲区。
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            float currentTime = (float) glfwGetTime();
            float deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            // 相机 处理键盘输入控制移动
            _camera.processKeyboard(Window._WINDOW_HANDLE, deltaTime);

            // TODO: 渲染
            for(Chunk chunk : _world._chunkList){
                chunk.mesh.Draw();
            }

            counter++;

            // 交换颜色缓冲区
            glfwSwapBuffers(Window._WINDOW_HANDLE);

            // 轮询窗口事件。上述的按键回调函数只会在此次调用期间被触发。
            glfwPollEvents();
        }
    }

    /**
     * 释放
     */
    private void release() {
        _shaderProgram.delete(_shaderProgramId);

        for(Chunk chunk : _world._chunkList){
            if(chunk != null){
                if(chunk.mesh!= null){
                    chunk.mesh.CleanUp();
                }
            }
        }

        // 释放窗口回调
        glfwFreeCallbacks(Window._WINDOW_HANDLE);
        // 销毁窗口
        glfwDestroyWindow(Window._WINDOW_HANDLE);

        // 释放GLFW
        glfwTerminate();
        // 释放GLFW错误回调
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}