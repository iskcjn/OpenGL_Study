package com.tiangong.blockhorizon.world;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Block {
    private Vector3f position; // 方块的位置
    private Matrix4f modelMatrix; // 方块的变换矩阵

    public Block(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
        this.modelMatrix = new Matrix4f().identity().translate(position); // 初始化变换矩阵
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
}