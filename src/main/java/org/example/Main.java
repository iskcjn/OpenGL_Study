package org.example;

// import com.tiangong.blockhorizon.util.LoggerUtil;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.PrintStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main implements Runnable{

    // 定义窗口大小的常量
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;

    // 定义窗口名称的常量
    private static final String WINDOW_TITLE = "Block Horizon";

    // 定义窗口是否可缩放的常量
    private static final boolean WINDOW_RESIZABLE = false;

    private long window; // 窗口句柄

    private int vaoId;   // 顶点数组对象 ID
    //private int eboId;   // 索引对象 ID
    private int vboId;   // 顶点缓冲对象 ID
    private int shaderProgramId; // 着色器程序 ID

    /**
     * 主入口
     * @param args main 的参数，用于接收命令行输入的参数。它是一个字符串数组，可以在程序启动时传递多个参数。
     */
    public static void main(String[] args) {
        try {
            // 强制设置控制台输出编码为 UTF-8
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            //LoggerUtil.error("设置输出编码UTF-8失败", e); // 使用自定义日志工具类
        }
        //new Thread(new Main()).start(); // 启动程序
        new Main().run(); // 启动程序
    }

    /**
     * 主函数
     */
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!"); // 打印 LWJGL 版本
        try{
            Init(); // 初始化
            Loop(); // 进入主循环
        }
        catch (Exception e) {
            System.out.println("Run Error:" + e);
        }
        finally {
            Destroy(); // 销毁
        }
    }

    /**
     * 初始化
     */
    public void Init() {
        // 设置错误回调，默认实现会将错误信息打印到 System.err
        GLFWErrorCallback.createPrint(System.err).set();

        // 初始化 GLFW，在此之前大多数 GLFW 函数都无法使用
        if (!glfwInit())
            throw new IllegalStateException("无法初始化 GLFW");

        // 配置 GLFW
        glfwDefaultWindowHints(); // 可选，当前窗口提示已经是默认值
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // 窗口创建后保持隐藏
        glfwWindowHint(GLFW_RESIZABLE, WINDOW_RESIZABLE ? GLFW_TRUE : GLFW_FALSE); // 窗口是否可缩放

        // 创建窗口，使用常量 WINDOW_WIDTH、WINDOW_HEIGHT 和 WINDOW_TITLE
        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("创建 GLFW 窗口失败");

        // 按键输入事件
        Input();

        // 获取线程栈并推入一个新帧
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // 获取传递给 glfwCreateWindow 的窗口大小
            glfwGetWindowSize(window, pWidth, pHeight);

            // 获取主显示器的分辨率
            GLFWVidMode videodisc = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // 将窗口居中，使用常量 WINDOW_WIDTH 和 WINDOW_HEIGHT
            assert videodisc != null;
            glfwSetWindowPos(
                    window,
                    (videodisc.width() - WINDOW_WIDTH) / 2,
                    (videodisc.height() - WINDOW_HEIGHT) / 2
            );
        } // 栈帧会自动弹出

        // 将 OpenGL 上下文设置为当前上下文
        glfwMakeContextCurrent(window);
        // 启用垂直同步
        glfwSwapInterval(1);

        // 显示窗口
        glfwShowWindow(window);

        // 初始化 OpenGL 上下文
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // 设置视口
        glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // 初始化三角形
        initTriangle();
    }

    /**
     * 主循环
     */
    public void Loop() {
        // 这一行对于 LWJGL 与 GLFW 的 OpenGL 上下文交互至关重要。
        // LWJGL 会检测当前线程中的上下文，创建 GLCapabilities 实例，
        // 并使 OpenGL 绑定可用。
        GL.createCapabilities();

        // 设置清屏颜色（红色）
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // 渲染模式：线条模式

        // 启用面剔除
        glEnable(GL_CULL_FACE);

        // 设置剔除背面
        glCullFace(GL_BACK);

        // 设置逆时针为正面
        glFrontFace(GL_CCW);

        // 运行渲染循环，直到用户尝试关闭窗口或按下 ESCAPE 键
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // 清空帧缓冲区

            render(); // 渲染

            glfwSwapBuffers(window); // 交换颜色缓冲区

            // 轮询窗口事件，上面的按键回调只会在此时被调用
            glfwPollEvents();
        }
    }

    /**
     * 设置输入回调
     */
    public void Input(){
        // 设置按键回调，每次按键按下、重复或释放时都会调用
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // 按下 ESC 键时关闭窗口
        });
        // 设置鼠标按键回调
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                System.out.println("左键按下");
            } else if (button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS) {
                System.out.println("右键按下");
            }
        });

        // 设置鼠标移动回调
        glfwSetCursorPosCallback(window, (window, X_pos, Y_pos) -> {
            System.out.println("鼠标移动到: (" + X_pos + ", " + Y_pos + ")");
            // TODO
        });
    }

    /**
     * 销毁
     */
    public void Destroy(){
        // 释放资源
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        glDeleteProgram(shaderProgramId);

        // 释放窗口回调并销毁窗口
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // 终止 GLFW 并释放错误回调
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    /**
     * 初始化三角形
     */
    private void initTriangle() {
        float aspectRatio = (float) WINDOW_HEIGHT / WINDOW_WIDTH;
        // 定义三角形的顶点数据（NDC 坐标系，范围 [-1, 1]）
        float[] vertices = {
                // 第一个三角形
                -0.5f * aspectRatio, -0.5f, 0.0f,  // 左下角
                0.5f * aspectRatio, -0.5f, 0.0f,  // 右下角
                -0.5f * aspectRatio, 0.5f, 0.0f,  // 顶部
                // 第二个三角形
                0.5f * aspectRatio, 0.5f, 0.0f,  // 右上角
                -0.5f * aspectRatio, 0.5f, 0.0f,  // 左上角
                0.5f * aspectRatio, -0.5f, 0.0f  // 底部

        };

        // 定义索引数据
//        int[] indices = {
//                0, 1, 2,  // 第一个三角形
//                1, 3, 2   // 第二个三角形
//        };

        // 创建顶点缓冲对象（VBO）
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // 创建索引缓冲对象（EBO）
//        eboId = glGenBuffers();
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // 创建顶点数组对象（VAO）
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // 设置顶点属性指针
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        // 解绑 VBO 和 VAO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        // 编译着色器程序
        shaderProgramId = compileShaders();
    }

    /**
     * 编译着色器程序
     */
    private int compileShaders() {
        // 顶点着色器源码
        String vertexShaderSource = "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "out vec4 vertexColor; // 为片段着色器指定一个颜色输出\n" +
                "void main() {\n" +
                "    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                "    vertexColor = vec4(0.5, 0.0, 0.0, 1.0); // 把输出变量设置为暗红色\n" +
                "}\0";

        // 片段着色器源码
        String fragmentShaderSource = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "in vec4 vertexColor; // 从顶点着色器传来的输入变量（名称相同， 类型相同）\n" +
                "void main() {\n" +
                "    FragColor = vertexColor;\n" +
                "}";

        // 编译顶点着色器
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkShaderCompileStatus(vertexShader);

        // 编译片段着色器
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkShaderCompileStatus(fragmentShader);

        // 链接着色器程序
        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkProgramLinkStatus(shaderProgram);

        // 删除着色器对象
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return shaderProgram;
    }

    /**
     * 检查着色器编译状态
     */
    private void checkShaderCompileStatus(int shader) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("着色器编译失败: " + glGetShaderInfoLog(shader));
        }
    }

    /**
     * 检查程序链接状态
     */
    private void checkProgramLinkStatus(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("着色器程序链接失败: " + glGetProgramInfoLog(program));
        }
    }

    /**
     * 渲染图像
     */
    public void render(){
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            System.err.println("OpenGL Error: " + error);
        }

//        System.out.println("VAO ID: " + vaoId);
//        System.out.println("VBO ID: " + vboId);
//        System.out.println("EBO ID: " + eboId);
//        System.out.println("Shader Program ID: " + shaderProgramId);

        // 使用着色器程序
        glUseProgram(shaderProgramId);

        // 绑定 VAO
        glBindVertexArray(vaoId);

        // 绘制三角形
        glDrawArrays(GL_TRIANGLES, 0, 3);
        // 绘制三角形
        glDrawArrays(GL_TRIANGLES, 3, 3);

        //glUseProgram(shaderProgramId); // 使用着色器程序
        //glBindVertexArray(vaoId);
        //glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);  // 6 是索引的数量
        //glBindVertexArray(0);

        // 解绑 VAO
        glBindVertexArray(0);

    }
}