package com.tiangong.blockhorizon;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    // 相机属性
    private Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;

    // 欧拉角
    private float yaw;
    private float pitch;

    // 相机选项
    private float movementSpeed;
    private float mouseSensitivity;
    private float fov;

    // 矩阵
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    // 窗口尺寸
    private int windowWidth;
    private int windowHeight;

    // 鼠标初始位置标志
    private boolean firstMouse = true;
    private float lastX;
    private float lastY;

    public Camera(int windowWidth, int windowHeight, Vector3f position) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.position = position;
        this.worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.yaw = -90.0f;
        this.pitch = 0.0f;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        this.movementSpeed = 5.0f;
        this.mouseSensitivity = 0.1f;
        this.fov = 45.0f;

        updateCameraVectors();
        updateProjectionMatrix();
    }
    private void updateCameraVectors() {
        // 计算新的前向量
        Vector3f newFront = new Vector3f();
        newFront.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        newFront.y = (float) Math.sin(Math.toRadians(pitch));
        newFront.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front = newFront.normalize();

        // 重新计算右向量和上向量
        right = front.cross(worldUp, new Vector3f()).normalize();
        up = right.cross(front, new Vector3f()).normalize();
    }
    public void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f()
                .perspective((float) Math.toRadians(fov),
                        (float) windowWidth / windowHeight,
                        0.1f,
                        100.0f);
    }
    public Matrix4f getViewMatrix() {
        return viewMatrix = new Matrix4f()
                .lookAt(position,
                        position.add(front, new Vector3f()),
                        up);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
    public void processKeyboard(long window, float deltaTime) {
        float velocity = movementSpeed * deltaTime;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            position.add(front.mul(velocity, new Vector3f()));
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
            position.sub(front.mul(velocity, new Vector3f()));
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
            position.sub(right.mul(velocity, new Vector3f()));
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
            position.add(right.mul(velocity, new Vector3f()));
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS)
            position.add(up.mul(velocity, new Vector3f()));
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
            position.sub(up.mul(velocity, new Vector3f()));
    }

    public void processMouseMovement(double xpos, double ypos) {
        if (firstMouse) {
            lastX = (float) xpos;
            lastY = (float) ypos;
            firstMouse = false;
        }

        float xoffset = (float) xpos - lastX;
        float yoffset = lastY - (float) ypos; // 反转Y轴
        lastX = (float) xpos;
        lastY = (float) ypos;

        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;

        yaw += xoffset;
        pitch += yoffset;

        // 约束俯仰角
        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;

        updateCameraVectors();
    }

    public void processMouseScroll(float yoffset) {
        fov -= yoffset;
        if (fov < 1.0f)
            fov = 1.0f;
        if (fov > 90.0f)
            fov = 90.0f;
        updateProjectionMatrix();
    }

    // Getter方法
    public Vector3f getPosition() { return position; }
    public float getFov() { return fov; }
    public float getPitch() { return pitch; }
    public float getYaw() { return yaw; }
}