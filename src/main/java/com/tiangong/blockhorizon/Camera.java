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
        this.front = new Vector3f(0.0f, 0.0f, -1.0f); // 修改为左手坐标系的前方向


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
        // System.out.println(newFront);
        front = newFront.normalize();

        // 重新计算右向量和上向量
        right = front.cross(worldUp, new Vector3f()).normalize();
        // up = right.cross(front, new Vector3f()).normalize();
        up = worldUp;
    }

    public void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f()
                .perspective((float) Math.toRadians(fov),
                        (float) windowWidth / windowHeight,
                        0.1f,
                        1000.0f);
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
//        fov -= yoffset;
//        if (fov < 1.0f)
//            fov = 1.0f;
//        if (fov > 90.0f)
//            fov = 90.0f;
//        updateProjectionMatrix();
    }

    // Getter方法
    public Vector3f getPosition() {
        return new Vector3f(position.x, position.y, -position.z);
    }
    public float getFov() { return fov; }
    public float getPitch() { return pitch; }
    public float getYaw() { return yaw; }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position.x, position.y, -position.z);
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