package com.tiangong.blockhorizon;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public static int _WINDOW_WIDTH;
    public static int _WINDOW_HEIGHT;
    public static String _WINDOW_TITLE;
    public static long _WINDOW_HANDLE;

    private boolean _canResize = false;
    private boolean _wireframeMode;

    /**
     * 窗口构造
     * @param Win_width
     * @param Win_height
     * @param Win_title
     */
    public Window(int Win_width, int Win_height, String Win_title){
        _WINDOW_WIDTH = Win_width;
        _WINDOW_HEIGHT = Win_height;
        _WINDOW_TITLE = Win_title;
    }

    public void init() {
        System.out.println("Window initialized!");
        // 设置GLFW错误回调
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit()) {
            System.out.println("Failed to initialize GLFW!");
        }
        // 打印GLFW版本
        System.out.println(org.lwjgl.Version.getVersion());
        // 配置 GLFW
        glfwDefaultWindowHints(); // 可选, 当前的窗口提示已经是默认值
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // 窗口创建后将保持隐藏状态
        glfwWindowHint(GLFW_RESIZABLE, _canResize ? GLFW_TRUE : GLFW_FALSE); // 窗口是否可以缩放

        // 创建窗口
        _WINDOW_HANDLE = glfwCreateWindow(_WINDOW_WIDTH, _WINDOW_HEIGHT, _WINDOW_TITLE, NULL, NULL);
        if ( _WINDOW_HANDLE == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        //<editor-fold desc="按键,鼠标 回调">
        // 设置按键的回调
        glfwSetKeyCallback(_WINDOW_HANDLE, (window, key, scancode, action, mods) -> {
            // ESCAPE 键按下
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // 我们将检测到渲染循环中的此问题
            // 检测 F1 键按下 (切换线框模式或填充模式)
            if (key == GLFW_KEY_F1 && action == GLFW_PRESS) {
                _wireframeMode = !_wireframeMode;
                // 根据标志位设置绘制模式
                if (_wireframeMode) {
                    // 线框模式
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                } else {
                    // 填充模式
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                }
            }

            // 鼠标点击 (鼠标点击交互回调)
            if(key == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){

            }

            // 鼠标移动 （用于视角移动）
            glfwSetCursorPosCallback(_WINDOW_HANDLE, (win, xpos, ypos) -> {
                // System.out.println(xpos + " " + ypos);
                Game._camera.processMouseMovement(xpos, ypos);
            });

            glfwSetScrollCallback(_WINDOW_HANDLE, (win, xoffset, yoffset) -> {
                // System.out.println("yoffset: " + yoffset);
                Game._camera.processMouseScroll((float) yoffset);
            });
            // 鼠标移动-------------------------
        });
        //</editor-fold>

        //<editor-fold desc="窗口居中">
        // 获取线程堆栈并推送一个新帧
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // 获取传递给 glfwCreateWindow 函数的窗口大小。
            glfwGetWindowSize(_WINDOW_HANDLE, pWidth, pHeight);

            // 获取主监视器的分辨率。
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // 窗口居中
            glfwSetWindowPos(
                    _WINDOW_HANDLE,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // 栈帧会自动弹出。
        //</editor-fold>

        // 创建上下文
        glfwMakeContextCurrent(_WINDOW_HANDLE);
        // 启用垂直同步，避免画面撕裂
        glfwSwapInterval(1);

        // 初始化 OpenGL 绑定
        createCapabilities();

        // 启用面剔除
        glEnable(GL_CULL_FACE);
        // 设置正面为逆时针方向
        glFrontFace(GL_CCW); // 默认是 GL_CCW 逆 ： GL_CW 顺
        glCullFace(GL_BACK);

        // 隐藏鼠标光标
        glfwSetInputMode(_WINDOW_HANDLE, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // 启用深度测试
        glEnable(GL_DEPTH_TEST);

        // 让窗口显示
        glfwShowWindow(_WINDOW_HANDLE);
    }
}
