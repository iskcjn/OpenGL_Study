package com.tiangong.blockhorizon.world.chunk;

import com.tiangong.blockhorizon.Game;
import com.tiangong.blockhorizon.utility.Utility;
import com.tiangong.blockhorizon.utility.noise.PerlinNoise;
import com.tiangong.blockhorizon.world.chunk.block.Block;
import com.tiangong.blockhorizon.world.chunk.block.BlockFace;
import com.tiangong.blockhorizon.world.chunk.block.BlockType;
import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Chunk {
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 16;
    public static final int CHUNK_DEPTH = 16;

    public ChunkRender render;

    public Vector3i chunkPos;

    public Block[] blocks;

    // 顶点，UV 数组
    public float[] vertices;

    public Chunk(int x, int y, int z) throws IOException {
        this.chunkPos = new Vector3i(x, y, z);
        this.blocks = new Block[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_DEPTH];

        this.vertices = new float[0];

        render = new ChunkRender(chunkPos, Game._textureId);

        generate();

    }

    /**
     * 生成方块
     */
    public void generate() {
        this.vertices = new float[0];

        // 越小起伏越小
        double scale = 0.034;

        int offset = 64;

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int z = 0; z < CHUNK_DEPTH; z++) {
                for (int x = 0; x < CHUNK_WIDTH; x++) {
                    int blockX = chunkPos.x * CHUNK_WIDTH + x;
                    int blockY = chunkPos.y * CHUNK_HEIGHT + y;
                    int blockZ = chunkPos.z * CHUNK_DEPTH + z;

                    double value = PerlinNoise.calculateHeight(blockX, blockZ, scale) + 60;
                    if(value < offset){
                        value = offset;
                    }

                    // 确保值在0-255之间（防止浮点精度溢出）
                    value = (float) Math.max(0, Math.min(120, value));
                    //System.out.println("噪声:" + value + ", Y:"+blockY);
                    //System.out.println("方块：X_"+blockX+", Y_"+blockY+", Z_+"+blockZ+"噪声:" + value);

                    if(Game.respawnPoint.x == blockX && Game.respawnPoint.z == blockZ){
                        Game.respawnPoint.y = (float) (value+2);
                        Game._camera.setPosition(Game.respawnPoint);
                    }


                    if(blockY > value){
                        //System.out.println("噪声:" + value);
                        Block block = new Block(blockX, blockY, blockZ, BlockType.AIR);
                        block.isSolid = Utility.isSolid(block.type);
                        setBlock(x, y, z, block);
                        continue;
                    }

                    if(blockY > value - 1){
                        //System.out.println("草方块:" + value);
                        Block block = new Block(blockX, blockY, blockZ, BlockType.GRASS);
                        block.isSolid = Utility.isSolid(block.type);
                        setBlock(x, y, z, block);
                    }
                    else{
                        Block block = new Block(blockX, blockY, blockZ, BlockType.DIRT);
                        block.isSolid = Utility.isSolid(block.type);
                        setBlock(x, y, z, block);
                    }
                }
            }
        }

        // generateMesh();
        // render.updateVertices(vertices);
        // render.initBuffer();
    }

    //TODO 优化面剔除

    /**
     * 生成顶点数据以及 UV 数组
     */
    public void generateMesh(HashMap<Vector3i, Chunk> chunks) {
        int totalBlocksProcessed = 0;
        long start = System.nanoTime();

        // 使用可扩展容器替代数组拼接（关键优化）
        ArrayList<Float> vertexList = new ArrayList<>();

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int z = 0; z < CHUNK_DEPTH; z++) {
                for (int x = 0; x < CHUNK_WIDTH; x++) {
                    totalBlocksProcessed++;

                    Block block = getBlock(x, y, z);
                    BlockType type = block.type; // 缓存类型减少访问开销

                    if(block.isSolid){
                        // 批量处理面生成逻辑
                        processFace(vertexList, x, y, z, type, BlockFace.TOP, chunks);
                        processFace(vertexList, x, y, z, type, BlockFace.BOTTOM, chunks);
                        processFace(vertexList, x, y, z, type, BlockFace.FRONT, chunks);
                        processFace(vertexList, x, y, z, type, BlockFace.BACK, chunks);
                        processFace(vertexList, x, y, z, type, BlockFace.LEFT, chunks);
                        processFace(vertexList, x, y, z, type, BlockFace.RIGHT, chunks);
                    }
                }
            }
        }
        // 最终一次性转换（避免多次数组扩容）
        this.vertices = new float[vertexList.size()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = vertexList.get(i);
        }
        System.out.printf("处理 %d 个方块耗时：%.2fms\n", totalBlocksProcessed, (System.nanoTime()-start)/1e6);
    }

    // 新增面处理专用方法
    private void processFace(List<Float> list, int x, int y, int z, BlockType type, BlockFace face, HashMap<Vector3i, Chunk> chunks) {
        boolean shouldRender = shouldRenderFace(face, new Vector3i(x, y, z), chunks);
        //System.out.println(face + ", shouldRender:" + shouldRender);
        if (shouldRender) {
            //float newX = chunkPos.x * CHUNK_WIDTH + x;
            // 修正坐标计算：交换x和z的乘法项
            float worldX = chunkPos.x * CHUNK_WIDTH + x;
            float worldY = chunkPos.y * CHUNK_HEIGHT + y;
            float worldZ = chunkPos.z * CHUNK_DEPTH + z;
            // 创建单个方块顶点数据和UV数据
            // worldZ 取反以符合 Z 轴的正方向向屏幕内
            float[] faceData = Utility.CreateVerticesAndUVS(worldX, worldY, -worldZ, face, type);
            for (float v : faceData) {
                list.add(v);
            }
        }
    }

    /**
     * 获取方块的面是否有接触
     */
    public boolean shouldRenderFace(BlockFace face, Vector3i blockPos, HashMap<Vector3i, Chunk> chunks){
        switch (face) {
            case TOP:    return checkAdjacentBlock(blockPos, 0, 1, 0, chunks);
            case BOTTOM: return checkAdjacentBlock(blockPos, 0, -1, 0, chunks);
            case FRONT:  return checkAdjacentBlock(blockPos, 0, 0, -1, chunks);
            case BACK:   return checkAdjacentBlock(blockPos, 0, 0, 1, chunks);
            case LEFT:   return checkAdjacentBlock(blockPos, -1, 0, 0, chunks);
            case RIGHT:  return checkAdjacentBlock(blockPos, 1, 0, 0, chunks);
            default:     return true;
        }
    }

    private boolean checkAdjacentBlock(Vector3i blockPos, int dx, int dy, int dz, HashMap<Vector3i, Chunk> chunks) {

        int newX = blockPos.x + dx;
        int newY = blockPos.y + dy;
        int newZ = blockPos.z + dz;

        if (newX < 0 || newX >= CHUNK_WIDTH || newY < 0 || newY >= CHUNK_HEIGHT || newZ < 0 || newZ >= CHUNK_DEPTH) {
            //System.out.println("→ 超出区块边界，渲染该面");
            // 跨区块检测是否是空气
            // 左
            if(dx < 0) {
                if (chunks.containsKey(new Vector3i(chunkPos.x-1, chunkPos.y, chunkPos.z))) {
                    Block block = chunks.get(new Vector3i(chunkPos.x-1, chunkPos.y, chunkPos.z)).getBlock(CHUNK_WIDTH-1, newY, newZ);
                    //System.out.println("→ 左侧区块是空气，渲染该面");
                    return block.type == BlockType.AIR || !block.isSolid;
                }
                // 左边区块不存在
                else{
                    return true;
                }
            }
            // 右
            if(dx > 0) {
                if (chunks.containsKey(new Vector3i(chunkPos.x+1, chunkPos.y, chunkPos.z))) {
                    Block block = chunks.get(new Vector3i(chunkPos.x+1, chunkPos.y, chunkPos.z)).getBlock(0, newY, newZ);
                    //System.out.println("→ 左侧区块是空气，渲染该面");
                    return block.type == BlockType.AIR || !block.isSolid;
                }
                // 右边区块不存在
                else{
                    return true;
                }
            }
            // 前
            if(dz < 0) {
                if (chunks.containsKey(new Vector3i(chunkPos.x, chunkPos.y, chunkPos.z-1))) {
                    Block block = chunks.get(new Vector3i(chunkPos.x, chunkPos.y, chunkPos.z-1)).getBlock(newX, newY, CHUNK_DEPTH-1);
                    //System.out.println("→ 左侧区块是空气，渲染该面");
                    return block.type == BlockType.AIR || !block.isSolid;
                }
                // 右边区块不存在
                else{
                    return true;
                }
            }
            // 后
            if(dz > 0) {
                if (chunks.containsKey(new Vector3i(chunkPos.x, chunkPos.y, chunkPos.z+1))) {
                    Block block = chunks.get(new Vector3i(chunkPos.x, chunkPos.y, chunkPos.z+1)).getBlock(newX, newY, 0);
                    //System.out.println("→ 左侧区块是空气，渲染该面");
                    return block.type == BlockType.AIR || !block.isSolid;
                }
                // 右边区块不存在
                else{
                    return true;
                }
            }
            // 上
            if(dy > 0) {
                if (chunks.containsKey(new Vector3i(chunkPos.x, chunkPos.y+1, chunkPos.z))) {
                    Block block = chunks.get(new Vector3i(chunkPos.x, chunkPos.y+1, chunkPos.z)).getBlock(newX, 0, newZ);
                    //System.out.println("→ 左侧区块是空气，渲染该面");
                    return block.type == BlockType.AIR || !block.isSolid;
                }
                // 右边区块不存在
                else{
                    return true;
                }
            }
            // 下
            if(dy < 0) {
                if (chunks.containsKey(new Vector3i(chunkPos.x, chunkPos.y-1, chunkPos.z))) {
                    Block block = chunks.get(new Vector3i(chunkPos.x, chunkPos.y-1, chunkPos.z)).getBlock(newX, CHUNK_HEIGHT-1, newZ);
                    //System.out.println("→ 左侧区块是空气，渲染该面");
                    return block.type == BlockType.AIR || !block.isSolid;
                }
                // 右边区块不存在
                else{
                    return true;
                }
            }
            return true;
        }

        Block block = getBlock(newX, newY, newZ);
        return block == null || !block.isSolid;
    }

    public Block getBlock(int x, int y, int z) {
        int index = x + y * CHUNK_WIDTH + z * CHUNK_DEPTH * CHUNK_HEIGHT;
        // int index = y * (CHUNK_WIDTH * CHUNK_DEPTH) + z * CHUNK_WIDTH + x;
        if(index >= blocks.length || index < 0) return null;
        // System.out.println("index: " + index + ", x:" + x + ", y:" + y + ", z:" + z + "block:" + blocks[index].pos.x + ", " + blocks[index].pos.y + ", " + blocks[index].pos.z);
        return blocks[
            index
        ];
    }

    public void setBlock(int x, int y, int z, Block block) {
        int index = x + y * CHUNK_WIDTH + z * CHUNK_DEPTH * CHUNK_HEIGHT;
        // int index = y * (CHUNK_WIDTH * CHUNK_DEPTH) + z * CHUNK_WIDTH + x;
        if(index < 0 || index >= blocks.length) return;
        blocks[
            index
        ] = block;
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