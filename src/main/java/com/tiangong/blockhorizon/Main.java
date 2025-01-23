package com.tiangong.blockhorizon;

import com.tiangong.blockhorizon.world.Block;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    // 窗口句柄
    public static long window;

    // 窗口宽高
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;

    // 窗口标题
    private String WindowTitle = "Hello World!";

    // 窗口可否缩放
    private boolean resizable = false;

    // 顶点数据
    private float[] vertices = {
            -0.5f, -0.5f, -0.5f,
             0.5f, -0.5f, -0.5f,
             0.5f,  0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f,  0.5f,
             0.5f, -0.5f,  0.5f,
             0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f
    };

    // 索引数据
    private int[] indices = {
            2, 1, 0, 0, 3, 2, // 后面
            4, 5, 6, 6, 7, 4, // 前面
            0, 1, 5, 5, 4, 0, // 底面
            1, 2, 6, 6, 5, 1, // 右面
            2, 3, 7, 7, 6, 2, // 顶面
            3, 0, 4, 4, 7, 3  // 左面
    };

    // VAO和VBO的ID
    private int vaoID;
    private int vboID;
    private int eboID;
    private int shaderProgramID;

    private List<Block> blocks; // 存储所有方块

    private Camera camera;      // 摄像机实例

    public Main(){
        blocks = new ArrayList<>();
        // 添加方块
        for (int i = -30; i < 30; i+=5) {
            for (int j = -30; j < 30; j+=5) {
                for (int k = -30; k < 30; k+=5) {
                    blocks.add(new Block(i, j, k)); // 添加方块
                }
            }
        }

        // 初始化摄像机
        camera = new Camera(
                new Vector3f(0.0f, 0.0f, 3.0f), // 初始位置
                new Vector3f(0.0f, 1.0f, 0.0f), // 世界上方向
                -90.0f, // 初始偏航角
                0.0f    // 初始俯仰角
        );
    }

    /**
     * 主运行方法，初始化窗口、渲染循环和资源释放。
     */
    public void run() {
        // 设置控制台输出编码为UTF-8
        System.setProperty("file.encoding", "UTF-8");

        System.out.println("Hello LWJGL " + org.lwjgl.Version.getVersion() + "!");

        init();
        loop();

        // 释放资源
        glDeleteVertexArrays(vaoID);
        glDeleteBuffers(vboID);
        glDeleteBuffers(eboID);
        glDeleteProgram(shaderProgramID);

        // 释放窗口回调并销毁窗口
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // 终止 GLFW 并且释放错误回调
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /**
     * 初始化GLFW、窗口、OpenGL上下文、着色器程序和顶点数据。
     */
    private void init() {
        // 设置错误回调。默认实现将在System.err中打印错误消息。
        GLFWErrorCallback.createPrint(System.err).set();

        // 初始化GLFW。在执行此操作之前，大多数GLFW函数将无法正常工作。
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // 配置GLFW
        glfwDefaultWindowHints(); // 配置GLFW
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // 窗口在创建后将保持隐藏状态
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE); // 窗口是否可以调整大小
        // 设置OpenGL版本
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // 创建窗口
        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WindowTitle, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // 绑定输入事件
        inputEvent();

        // 获取线程堆栈并推送新帧
        try (MemoryStack stack = stackPush()) { // 获取线程堆栈并推送新帧
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // 获取传递给glfwCreateWindow的窗口大小
            glfwGetWindowSize(window, pWidth, pHeight);

            // 获取主监视器的分辨率
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // 居中窗口
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // 堆栈帧会自动弹出

        // 使OpenGL上下文当前
        glfwMakeContextCurrent(window); // 使OpenGL上下文当前
        // 启用垂直同步
        glfwSwapInterval(1); // 启用垂直同步

        // 初始化 OpenGL 绑定
        createCapabilities();


        // 启用面剔除
        glEnable(GL_CULL_FACE);
        // 设置正面为逆时针方向
        glFrontFace(GL_CCW); // 默认是 GL_CCW 逆 ： GL_CW 顺
        glCullFace(GL_BACK);

        // 隐藏鼠标光标
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // 使窗口可见
        glfwShowWindow(window); // 使窗口可见

        // 创建和编译着色器程序
        shaderProgramID = createShaderProgram();

        // 设置顶点数据
        setupVertices();

        // 启用深度测试
        glEnable(GL_DEPTH_TEST);
    }

    /**
     * 输入事件处理。
     */
    private void inputEvent() {
        // 设置按键回调。每次按键按下、重复或释放时都会调用此回调。
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // 我们将在渲染循环中检测到这一点
            }
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
            // System.out.println("鼠标移动到: (" + X_pos + ", " + Y_pos + ")");
        });
    }

    /**
     * 创建和编译着色器程序。
     * @return 着色器程序ID
     */
    private int createShaderProgram() {
        // 顶点着色器代码
        String vertexShaderSource = "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "uniform mat4 model;      // 方块的变换矩阵\n" +
                "uniform mat4 view;       // 视图矩阵\n" +
                "uniform mat4 projection; // 投影矩阵\n" +
                "void main()\n" +
                "{\n" +
                "   // 将顶点位置从局部空间转换到世界空间\n" +
                "   gl_Position = projection * view * model * vec4(aPos, 1.0);\n" +
                "}\0";

        // 片段着色器代码
        String fragmentShaderSource = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "void main()\n" +
                "{\n" +
                "   FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
                "}\0";

        // 设置当前上下文
        glfwMakeContextCurrent(window);
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

    /**
     * 设置顶点数据，包括VAO、VBO和EBO。
     */
    private void setupVertices() {
        // 创建VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // 创建VBO
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // 创建EBO
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        // 设置顶点属性指针
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // 解绑VAO
        glBindVertexArray(0);
    }

    /**
     * 渲染循环，持续渲染直到窗口关闭。
     */
    private void loop() {
        // 初始化 OpenGL 绑定
        createCapabilities();
        // 设置清除颜色
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f); // 设置清除颜色

        // 初始化时间变量
        float deltaTime = 0.0f; // 当前帧与上一帧的时间差
        float lastFrame = 0.0f; // 上一帧的时间
        int frameCount = 0; // 帧计数器
        float fps = 0.0f;   // 当前帧率
        float lastFpsTime = 0.0f; // 上一次计算帧率的时间

        // 运行渲染循环，直到用户尝试关闭窗口或按下ESCAPE键。
        while (!glfwWindowShouldClose(window)) {
            // 计算时间差
            float currentFrame = (float) glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            // 更新帧计数器
            frameCount++;

            // 每秒钟计算一次帧率
            if (currentFrame - lastFpsTime >= 1.0f) {
                fps = frameCount / (currentFrame - lastFpsTime); // 计算帧率
                frameCount = 0; // 重置帧计数器
                lastFpsTime = currentFrame; // 更新上一次计算帧率的时间

                // 输出帧率
                System.out.println("FPS: " + fps);
            }

            //System.out.println("PlayerPos:" + camera.position); // 输出玩家位置

            // 处理输入
            processInput(deltaTime);

            // 渲染
            render();

            // 交换缓冲区并检查事件
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private double lastX = WINDOW_WIDTH / 2.0; // 上一帧的鼠标X位置
    private double lastY = WINDOW_HEIGHT / 2.0; // 上一帧的鼠标Y位置
    private boolean firstMouse = true; // 是否是第一次获取鼠标位置
    /**
     * 输入事件，包括键盘和鼠标。
     * @param deltaTime
     */
    private void processInput(float deltaTime) {
        // 处理键盘输入
        camera.processKeyboard(deltaTime);

        // 处理鼠标输入
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        // 获取鼠标位置
        glfwGetCursorPos(window, xpos, ypos);

        // 如果是第一次获取鼠标位置，初始化 lastX 和 lastY
        if (firstMouse) {
            lastX = xpos[0];
            lastY = ypos[0];
            firstMouse = false;
        }

        // 将鼠标位置设置为窗口中心
        float xoffset = (float) (xpos[0] - lastX);
        float yoffset = (float) (lastY - ypos[0]); // 反转Y轴

        // 更新上一帧的鼠标位置
        lastX = xpos[0];
        lastY = ypos[0];

        camera.processMouseMovement(xoffset, yoffset);

        // 将鼠标光标重置到窗口中心
        // glfwSetCursorPos(window, WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);
    }

    /**
     * 渲染内容
     */
    private void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // 使用着色器程序
        glUseProgram(shaderProgramID);

        // 绑定VAO
        glBindVertexArray(vaoID);

        // 设置视图矩阵和投影矩阵
        Matrix4f view = camera.getViewMatrix();
        Matrix4f projection = new Matrix4f().perspective(
                (float) Math.toRadians(45.0f), // 视野角度
                (float) WINDOW_WIDTH / WINDOW_HEIGHT, // 宽高比
                0.1f, // 近平面
                100.0f // 远平面
        );

        // 获取着色器中 uniform 的位置
        int viewLoc = GL20.glGetUniformLocation(shaderProgramID, "view");
        int projectionLoc = GL20.glGetUniformLocation(shaderProgramID, "projection");

        // 传递视图矩阵和投影矩阵
        GL20.glUniformMatrix4fv(viewLoc, false, view.get(new float[16]));
        GL20.glUniformMatrix4fv(projectionLoc, false, projection.get(new float[16]));

        // 渲染每个方块
        for (Block block : blocks) {
            // 获取方块的变换矩阵
            Matrix4f model = block.getModelMatrix();

            // 获取着色器中 model uniform 的位置
            int modelLoc = GL20.glGetUniformLocation(shaderProgramID, "model");

            // 传递变换矩阵
            GL20.glUniformMatrix4fv(modelLoc, false, model.get(new float[16]));

            // 绘制方块
            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        }

        // 添加日志或断点以确保 VAO 和 VBO 已正确绑定
        //System.out.println("VAO ID: " + vaoID);
        //System.out.println("VBO ID: " + vboID);
        //System.out.println("EBO ID: " + eboID);

        // 绘制正方体
        //glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

    public static void main(String[] args) {
        new Main().run();
    }
}