package com.tiangong.blockhorizon;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {
    public Vector3f position; // 摄像机位置
    private Vector3f front;    // 摄像机前方方向
    private Vector3f up;       // 摄像机上方向
    private Vector3f right;    // 摄像机右方向
    private Vector3f worldUp;  // 世界上方向

    private float yaw = -90.0f;   // 偏航角（左右旋转）
    private float pitch = 0.0f; // 俯仰角（上下旋转）
    float sensitivity = 0.1f; // 鼠标灵敏度

    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        this.position = position;
        this.worldUp = up;
        this.yaw = yaw;
        this.pitch = pitch;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        updateCameraVectors();
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, position.add(front, new Vector3f()), up);
    }

    public void processKeyboard(float deltaTime) {
        float velocity = 2.5f * deltaTime; // 移动速度

        if (GLFW.glfwGetKey(Main.window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            position.add(front.mul(velocity, new Vector3f()));
        }
        if (GLFW.glfwGetKey(Main.window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            position.sub(front.mul(velocity, new Vector3f()));
        }
        if (GLFW.glfwGetKey(Main.window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            position.sub(right.mul(velocity, new Vector3f()));
        }
        if (GLFW.glfwGetKey(Main.window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            position.add(right.mul(velocity, new Vector3f()));
        }
    }

    public void processMouseMovement(float xoffset, float yoffset) {
        //sensitivity = 0.1f; // 鼠标灵敏度
        xoffset *= sensitivity;
        yoffset *= sensitivity;

        yaw += xoffset;
        pitch += yoffset;

        // 限制俯仰角
        if (pitch > 89.0f) {
            pitch = 89.0f;
        }
        if (pitch < -89.0f) {
            pitch = -89.0f;
        }

        updateCameraVectors();
    }

    private void updateCameraVectors() {
        // 计算新的前方方向
        Vector3f front = new Vector3f();
        front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        this.front = front.normalize();

        // 计算右方向和上方向
        this.right = front.cross(worldUp, new Vector3f()).normalize();
        this.up = right.cross(front, new Vector3f()).normalize();
    }
}
