package com.tiangong.blockhorizon.utility;

import com.tiangong.blockhorizon.world.chunk.block.BlockFace;
import com.tiangong.blockhorizon.world.chunk.block.BlockType;
import org.joml.Vector2f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Utility {
    /**
     * 从指定路径读取 GLSL 文件内容并返回为字符串
     *
     * @param filePath GLSL 文件的路径
     * @return 文件内容的字符串表示
     * @throws IOException 如果读取文件时发生错误
     */
    public static String readGLSLFile(String filePath) throws IOException {
        StringBuilder shaderSource = new StringBuilder();
        try (InputStream inputStream = Utility.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + filePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    shaderSource.append(line).append("\n");
                }
            }
        }
        return shaderSource.toString();
    }

    /**
     * 加载纹理
     *
     * @param filePath 纹理文件的路径
     * @return 纹理ID
     * @throws IOException 如果读取文件时发生错误
     */
    public static int loadTexture(String filePath) throws IOException {
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // 设置纹理参数
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // GL_NEAREST GL_LINEAR
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // 加载图像
        STBImage.stbi_set_flip_vertically_on_load(true);
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = STBImage.stbi_load(filePath, width, height, channels, 4);
        if (image == null) {
            throw new RuntimeException("无法加载纹理: " + STBImage.stbi_failure_reason());
        }

        // 上传纹理数据
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        // 禁用多重映射
        // glGenerateMipmap(GL_TEXTURE_2D);

        // 释放内存
        STBImage.stbi_image_free(image);

        return textureID;
    }

    /**
     * 合并两个数组
     * @param array1 第一个数组
     * @param array2 第二个数组
     * @return 合并后的数组
     * */
    public static int[] concatenateArrays(int[] array1, int[] array2){
        // System.out.println("Utility: concatenateArrays" + "arr1 len:" + array1.length + "arr2 len:" + array2.length);
        // 计算新数组的长度
        int newLength = array1.length + array2.length;
        // 创建新数组
        int[] result = new int[newLength];

        // 复制第一个数组到新数组
        System.arraycopy(array1, 0, result, 0, array1.length);
        // 复制第二个数组到新数组
        System.arraycopy(array2, 0, result, array1.length, array2.length);

        return result;
    }
    /**
     * 合并两个数组
     * @param array1 第一个数组
     * @param array2 第二个数组
     * @return 合并后的数组
     * */
    public static float[] concatenateArrays(float[] array1, float[] array2){
        // System.out.println("Utility: concatenateArrays" + "arr1 len:" + array1.length + "arr2 len:" + array2.length);
        // 计算新数组的长度
        int newLength = array1.length + array2.length;
        // 创建新数组
        float[] result = new float[newLength];

        // 复制第一个数组到新数组
        System.arraycopy(array1, 0, result, 0, array1.length);
        // 复制第二个数组到新数组
        System.arraycopy(array2, 0, result, array1.length, array2.length);

        return result;
    }

    /**
     * 获取方块的纹理UV
     * @param blockType 方块类型
     * @param blockFace 方块面
     * @return 纹理UV
     * */
    public static Vector2f[] GetBlockTextureUV(BlockType blockType, BlockFace blockFace){
        int AtlasWIDTH = 256;
        int AtlasHEIGHT = 256;
        int PixeWIDTH = 16;
        int PixeHEIGHT = 16;
        float onePixelUV = (float) PixeWIDTH / (float) AtlasWIDTH;
        switch (blockType){
            case GRASS:
                if(blockFace == BlockFace.TOP){
                    Vector2f uv = GetUVByLoc(1, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                                          new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.BOTTOM){
                    Vector2f uv = GetUVByLoc(2, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.FRONT){
                    Vector2f uv = GetUVByLoc(0, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.BACK){
                    Vector2f uv = GetUVByLoc(0, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.LEFT){
                    Vector2f uv = GetUVByLoc(0, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.RIGHT){
                    Vector2f uv = GetUVByLoc(0, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
            case DIRT:
                if(blockFace == BlockFace.TOP){
                    Vector2f uv = GetUVByLoc(2, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.BOTTOM){
                    Vector2f uv = GetUVByLoc(2, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.FRONT){
                    Vector2f uv = GetUVByLoc(2, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.BACK){
                    Vector2f uv = GetUVByLoc(2, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.LEFT){
                    Vector2f uv = GetUVByLoc(2, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
                if(blockFace == BlockFace.RIGHT){
                    Vector2f uv = GetUVByLoc(2, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                    return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                            new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
                }
            default:
                Vector2f uv = GetUVByLoc(2, 0, AtlasWIDTH, AtlasHEIGHT, PixeWIDTH, PixeHEIGHT);
                return new Vector2f[]{uv, new Vector2f(uv.x + onePixelUV, uv.y), new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV),
                        new Vector2f(uv.x + onePixelUV, uv.y + onePixelUV), new Vector2f(uv.x, uv.y + onePixelUV), uv};
        }
    }

    /**
     * 坐标转换
     * @param x
     * @param y
     * @param AtlasWIDTH
     * @param AtlasHEIGHT
     * @param PixeWIDTH
     * @param PixeHEIGHT
     * @return
     */
    public static Vector2f GetUVByLoc(int x, int y, int AtlasWIDTH, int AtlasHEIGHT, int PixeWIDTH, int PixeHEIGHT){
        float u = ((float)x * PixeWIDTH) / AtlasWIDTH;
        float v = (((float) (AtlasHEIGHT / PixeHEIGHT) - 1 - (float)y) * PixeHEIGHT) / AtlasHEIGHT;
        return new Vector2f(u, v);
    }

    /**
     * 获取不同面的顶点和UV数组
     * @param x
     * @param y
     * @param z
     * @param blockFace
     * @param blockType
     * @return
     */
    public static float[] CreateVerticesAndUVS(float x, float y, float z, BlockFace blockFace, BlockType blockType){
        switch (blockFace){
            case TOP:
                Vector2f[] topUV = GetBlockTextureUV(blockType, BlockFace.TOP);
                return new float[]{
                        -0.5f+x, 0.5f+y, 0.5f+z, 0.0f+x, 1.0f+y, 0.0f+z, topUV[0].x, topUV[0].y,
                        0.5f+x, 0.5f+y, 0.5f+z, 0.0f+x, 1.0f+y, 0.0f+z, topUV[1].x, topUV[1].y,
                        0.5f+x, 0.5f+y, -0.5f+z, 0.0f+x, 1.0f+y, 0.0f+z, topUV[2].x, topUV[2].y,
                        0.5f+x, 0.5f+y, -0.5f+z, 0.0f+x, 1.0f+y, 0.0f+z, topUV[3].x, topUV[3].y,
                        -0.5f+x, 0.5f+y, -0.5f+z, 0.0f+x, 1.0f+y, 0.0f+z, topUV[4].x, topUV[4].y,
                        -0.5f+x, 0.5f+y, 0.5f+z, 0.0f+x, 1.0f+y, 0.0f+z, topUV[5].x, topUV[5].y,
                };
            case BOTTOM:
                Vector2f[] bottomUV = GetBlockTextureUV(blockType, BlockFace.BOTTOM);
                return new float[]{
                        -0.5f+x, -0.5f+y, -0.5f+z, 0.0f+x, -1.0f+y, 0.0f+z, bottomUV[0].x, bottomUV[0].y,
                        0.5f+x, -0.5f+y, -0.5f+z, 0.0f+x, -1.0f+y, 0.0f+z, bottomUV[1].x, bottomUV[1].y,
                        0.5f+x, -0.5f+y, 0.5f+z, 0.0f+x, -1.0f+y, 0.0f+z, bottomUV[2].x, bottomUV[2].y,
                        0.5f+x, -0.5f+y, 0.5f+z, 0.0f+x, -1.0f+y, 0.0f+z, bottomUV[3].x, bottomUV[3].y,
                        -0.5f+x, -0.5f+y, 0.5f+z, 0.0f+x, -1.0f+y, 0.0f+z, bottomUV[4].x, bottomUV[4].y,
                        -0.5f+x, -0.5f+y, -0.5f+z, 0.0f+x, -1.0f+y, 0.0f+z, bottomUV[5].x, bottomUV[5].y,
                };
            case FRONT:
                Vector2f[] fontUV = GetBlockTextureUV(blockType, BlockFace.FRONT);
                return new float[]{
                        -0.5f+x, -0.5f+y, 0.5f+z, 0.0f+x, 0.0f+y, 1.0f+z, fontUV[0].x, fontUV[0].y,
                        0.5f+x, -0.5f+y, 0.5f+z, 0.0f+x, 0.0f+y, 1.0f+z, fontUV[1].x, fontUV[1].y,
                        0.5f+x, 0.5f+y, 0.5f+z, 0.0f+x, 0.0f+y, 1.0f+z, fontUV[2].x, fontUV[2].y,
                        0.5f+x, 0.5f+y, 0.5f+z, 0.0f+x, 0.0f+y, 1.0f+z, fontUV[3].x, fontUV[3].y,
                        -0.5f+x, 0.5f+y, 0.5f+z, 0.0f+x, 0.0f+y, 1.0f+z, fontUV[4].x, fontUV[4].y,
                        -0.5f+x, -0.5f+y, 0.5f+z, 0.0f+x, 0.0f+y, 1.0f+z, fontUV[5].x, fontUV[5].y,
                };
            case BACK:
                Vector2f[] backUV = GetBlockTextureUV(blockType, BlockFace.BACK);
                return new float[]{
                        0.5f+x, -0.5f+y, -0.5f+z, 0.0f+x, 0.0f+y, -1.0f+z, backUV[0].x, backUV[0].y,
                        -0.5f+x, -0.5f+y, -0.5f+z, 0.0f+x, 0.0f+y, -1.0f+z, backUV[1].x, backUV[1].y,
                        -0.5f+x, 0.5f+y, -0.5f+z, 0.0f+x, 0.0f+y, -1.0f+z, backUV[2].x, backUV[2].y,
                        -0.5f+x, 0.5f+y, -0.5f+z, 0.0f+x, 0.0f+y, -1.0f+z, backUV[3].x, backUV[3].y,
                        0.5f+x, 0.5f+y, -0.5f+z, 0.0f+x, 0.0f+y, -1.0f+z, backUV[4].x, backUV[4].y,
                        0.5f+x, -0.5f+y, -0.5f+z, 0.0f+x, 0.0f+y, -1.0f+z, backUV[5].x, backUV[5].y,
                };
            case LEFT:
                Vector2f[] leftUV = GetBlockTextureUV(blockType, BlockFace.LEFT);
                return new float[]{
                        -0.5f+x, -0.5f+y, -0.5f+z, -1.0f+x, 0.0f+y, 0.0f+z, leftUV[0].x, leftUV[0].y,
                        -0.5f+x, -0.5f+y, 0.5f+z, -1.0f+x, 0.0f+y, 0.0f+z, leftUV[1].x, leftUV[1].y,
                        -0.5f+x, 0.5f+y, 0.5f+z, -1.0f+x, 0.0f+y, 0.0f+z, leftUV[2].x, leftUV[2].y,
                        -0.5f+x, 0.5f+y, 0.5f+z, -1.0f+x, 0.0f+y, 0.0f+z, leftUV[3].x, leftUV[3].y,
                        -0.5f+x, 0.5f+y, -0.5f+z, -1.0f+x, 0.0f+y, 0.0f+z, leftUV[4].x, leftUV[4].y,
                        -0.5f+x, -0.5f+y, -0.5f+z, -1.0f+x, 0.0f+y, 0.0f+z, leftUV[5].x, leftUV[5].y,
                };
            case RIGHT:
                Vector2f[] rightUV = GetBlockTextureUV(blockType, BlockFace.RIGHT);
                return new float[]{
                        0.5f+x, -0.5f+y, 0.5f+z, 1.0f+x, 0.0f+y, 0.0f+z, rightUV[0].x, rightUV[0].y,
                        0.5f+x, -0.5f+y, -0.5f+z, 1.0f+x, 0.0f+y, 0.0f+z, rightUV[1].x, rightUV[1].y,
                        0.5f+x, 0.5f+y, -0.5f+z, 1.0f+x, 0.0f+y, 0.0f+z, rightUV[2].x, rightUV[2].y,
                        0.5f+x, 0.5f+y, -0.5f+z, 1.0f+x, 0.0f+y, 0.0f+z, rightUV[3].x, rightUV[3].y,
                        0.5f+x, 0.5f+y, 0.5f+z, 1.0f+x, 0.0f+y, 0.0f+z, rightUV[4].x, rightUV[4].y,
                        0.5f+x, -0.5f+y, 0.5f+z, 1.0f+x, 0.0f+y, 0.0f+z, rightUV[5].x, rightUV[5].y,
                };
            default:
                return new float[0];
        }
    }

    /**
     * 判断是否为固体方块
     * @param blockType 方块类型
     * @return 是否为固体方块
     */
    public static boolean isSolid(BlockType blockType){
        return switch (blockType) {
            case AIR -> false;
            default -> true;
        };
    }

    public static double getDistance(Vector3i p1, Vector3i p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y) + (p1.z - p2.z) * (p1.z - p2.z));
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