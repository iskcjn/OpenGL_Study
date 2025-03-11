package com.tiangong.blockhorizon;

import com.tiangong.blockhorizon.utility.Utility;
import com.tiangong.blockhorizon.utility.noise.PerlinNoise;
import com.tiangong.blockhorizon.window.Window;
import com.tiangong.blockhorizon.world.World;
import com.tiangong.blockhorizon.world.chunk.Chunk;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

public class Game implements Runnable {
    public static int WINDOW_WIDTH = 1280;
    public static int WINDOW_HEIGHT = 720;
    public static String WINDOW_TITLE = "Block Horizon";
    public static Window window;
    public static Camera _camera;
    public static ShaderProgram _shaderProgram;
    public static int _shaderProgramId;
    public static int _textureId;

    // 种子
    public static int seed = 123456;
    // 噪声
    // public static PerlinNoise perlin;
    // 世界
    public static World _world;
    // 世界重生点
    public static Vector3f respawnPoint = new Vector3f(0.0f, 0.0f, 0.0f);

    public Game(){

    }


    @Override
    public void run() {
        try {
            init();
            loop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        release();
    }

    public void init() throws IOException {
        // 创建窗口
        window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);
        window.init();

        _textureId = Utility.loadTexture("D:\\CODE_PJ\\JAVA_Project_Libray\\Block Horizon-N2\\src\\main\\resources\\Textures\\block_atlas.png");

        // 初始化摄像机
        _camera = new Camera(WINDOW_WIDTH, WINDOW_HEIGHT, new Vector3f(0.0f, 0.0f, -6.0f));
        // 创建着色器程序
        _shaderProgram = new ShaderProgram(Window.WINDOW_HANDLE);
        _shaderProgramId = _shaderProgram.createShaderProgram("Shader/vertexLight.glsl", "Shader/fragmentLight.glsl");
        // _shaderProgramId = _shaderProgram.createShaderProgram("Shader/vertex.glsl", "Shader/fragment.glsl");

        // 初始化世界重生点
        Random random = new Random();
        respawnPoint = new Vector3f(random.nextInt(Chunk.CHUNK_WIDTH), 0.0f, random.nextInt(Chunk.CHUNK_DEPTH));



        // 初始化世界
        _world = new World();
    }

    public void loop() throws IOException {
        // 创建 GLCapabilities 实例，并使 OpenGL 绑定可供使用。
        GL.createCapabilities();
        // 设置清屏颜色
        GL11.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        // 帧率控制参数
        final double TARGET_FPS = 60.0;
        final double TARGET_FRAME_TIME = 1.0 / TARGET_FPS;

        double lastTime = 0.0f;
        int frameCount = 0;
        double lastFPSCheck = lastTime;

//        for (int i = 0; i < 16; i++) {
//            _world.addChunk(-1, i, -2);
//            _world.upChunkData();
//        }

        while(!glfwWindowShouldClose(Window.WINDOW_HANDLE)){

            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;


            // 帧率限制
            if (deltaTime < TARGET_FRAME_TIME) {
                try {
                    long sleepTime = (long)((TARGET_FRAME_TIME - deltaTime) * 1000);
                    Thread.sleep(sleepTime);

                    // 再次获取当前时间
                    currentTime = glfwGetTime();
                    deltaTime = currentTime - lastTime;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            _camera.processKeyboard(Window.WINDOW_HANDLE, (float) deltaTime);

            // 清除帧缓冲区。
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // System.out.println("玩家位置: X " + _camera.getPosition().x + ", Y " + _camera.getPosition().y + ", Z " + _camera.getPosition().z);

            // 渲染
            for (Chunk chunk : _world.chunks) {
                if(Utility.getDistance(new Vector3i(chunk.chunkPos.x*16,chunk.chunkPos.y*16,chunk.chunkPos.z*16), new Vector3i((int) _camera.getPosition().x, (int) _camera.getPosition().y, (int) _camera.getPosition().z)) < 16*5){

                    chunk.render.draw();
                }
            }

            // 交换颜色缓冲区
            glfwSwapBuffers(Window.WINDOW_HANDLE);
            // 轮询窗口事件。上述的按键回调函数只会在此次调用期间被触发。
            glfwPollEvents();

            // FPS计数器（可选）
            frameCount++;
            if (currentTime - lastFPSCheck >= 1.0) {
                // System.out.println("FPS: " + frameCount);
                frameCount = 0;
                lastFPSCheck = currentTime;
            }

            lastTime = currentTime;
        }
    }

    /**
     * 释放
     */
    private void release() {
        _shaderProgram.delete(_shaderProgramId);

        for (Chunk chunk : _world.chunks) {
            chunk.render.cleanup();
        }

        // 释放窗口回调
        glfwFreeCallbacks(Window.WINDOW_HANDLE);
        // 销毁窗口
        glfwDestroyWindow(Window.WINDOW_HANDLE);

        // 释放GLFW
        glfwTerminate();
        // 释放GLFW错误回调
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
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