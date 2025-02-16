package com.tiangong.blockhorizon.world.chunk.block;

import com.tiangong.blockhorizon.utility.BlockType;
import com.tiangong.blockhorizon.utility.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Block {
    public static final float[] vertices = new float[]{
            // 前面 VO
            -0.5f, -0.5f, 0.5f,
            // V1
            -0.5f, 0.5f, 0.5f,
            // V2
            0.5f, 0.5f, 0.5f,
            // V3
            0.5f, -0.5f, 0.5f,
            // 后面 V4
            0.5f, -0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, 0.5f, -0.5f,
            // V7
            -0.5f, -0.5f, -0.5f,
    };
    // 定义每个面的顶点索引
    private static final int[][] FACE_INDICES = {
            // 前面
            {0, 3, 2, 2, 1, 0},
            // 后面
            {4, 7, 6, 6, 5, 4},
            // 左面
            {7, 0, 1, 1, 6, 7},
            // 右面
            {3, 4, 5, 5, 2, 3},
            // 上面
            {1, 2, 5, 5, 6, 1},
            // 下面
            {7, 4, 3, 3, 0, 7}
    };

    /**
     * 获取方块的顶点索引
     *
     * @return
     */
    public static int[] GetBlockIndices2(BlockType blockType, int[] face) {
        System.out.println("获取方块的顶点索引,传入面" + face[0] + " " + face[1] + " " + face[2] + " " + face[3] + " " + face[4] + " " + face[5]);

        // 如果所有面都不显示，直接返回空数组
        boolean allFacesHidden = true;
        for (int f : face) {
            if (f == 1) {
                allFacesHidden = false;
                break;
            }
        }
        if (allFacesHidden) {
            System.out.println("此方块没有面接触空气");
            return new int[]{};
        }

        // 动态构建索引列表
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < face.length; i++) {
            if (face[i] == 1) {
                int[] indices = FACE_INDICES[i];
                for (int index : indices) {
                    indexList.add(index);
                }
            }
        }

        // 将列表转换为数组
        int[] drawface = new int[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            drawface[i] = indexList.get(i);
        }

        return drawface;
    }
    public static int[] GetBlockIndices2(BlockType blockType, boolean[] face) {
        System.out.println("获取方块的顶点索引,传入面" + face[0] + " " + face[1] + " " + face[2] + " " + face[3] + " " + face[4] + " " + face[5]);

        // 如果所有面都不显示，直接返回空数组
        boolean allFacesHidden = true;
        for (boolean f : face) {
            if (f == false) {
                allFacesHidden = false;
                break;
            }
        }
        if (allFacesHidden) {
            System.out.println("此方块没有面接触空气");
            return new int[]{};
        }

        // 动态构建索引列表
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < face.length; i++) {
            if (face[i] == false) {
                int[] indices = FACE_INDICES[i];
                for (int index : indices) {
                    indexList.add(index);
                }
            }
        }

        // 将列表转换为数组
        int[] drawface = new int[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            drawface[i] = indexList.get(i);
        }

        return drawface;
    }
}
