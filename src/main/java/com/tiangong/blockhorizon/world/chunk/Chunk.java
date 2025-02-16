package com.tiangong.blockhorizon.world.chunk;

import com.tiangong.blockhorizon.utility.BlockType;
import com.tiangong.blockhorizon.world.chunk.block.Block;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class Chunk {
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 16;
    public static final int CHUNK_DEPTH = 16;

    public Mesh mesh;

    public Vector3i _chunkPos;

    Map<Vector3i, int[]> _chunkMap;

    int[] _blocks;

    public Chunk(Vector3i chunkPos) {
        this._chunkPos = chunkPos;
        _blocks = new int[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_DEPTH];
        _chunkMap = new HashMap<>();
    }

    Map<Vector3i, Integer> cubeMap = new HashMap<>();
    public void addBlock() {
        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            for (int z = 0; z < CHUNK_DEPTH; z++) {
                for (int x = 0; x < CHUNK_WIDTH; x++) {
                    _blocks[x + y * CHUNK_WIDTH + z * CHUNK_DEPTH * CHUNK_HEIGHT] = BlockType.GRASS.ordinal();
                    cubeMap.put(new Vector3i(x, y, z), BlockType.GRASS.ordinal());
                }
            }
        }
        // 遍历方块
        for (int i = 0; i < _blocks.length; i++) {
            int index = i;
            // 反求 x, y, z 坐标
            int z = index / (CHUNK_DEPTH * CHUNK_HEIGHT);
            index %= (CHUNK_DEPTH * CHUNK_HEIGHT);
            int y = index / CHUNK_WIDTH;
            int x = index % CHUNK_WIDTH;

            int[] indeces = Block.GetBlockIndices2(BlockType.GRASS, checkCubeFaces(new Vector3i(x, y, z)));
            if (indeces.length != 0)
                _chunkMap.put(new Vector3i(x, y, z), indeces);
        }
        //System.out.println("此块共有:" + FaceairCount + "面接触空气");
        mesh = new Mesh(_chunkPos, _chunkMap);
    }
    // 检查立方体的六个面是否接触其他立方体
    public boolean[] checkCubeFaces(Vector3i cube) {
        boolean[] result = new boolean[6];
        // 前
        Vector3i front = new Vector3i(cube.x, cube.y, cube.z + 1);
        result[0] = cubeMap.containsKey(front);
        // 后
        Vector3i back = new Vector3i(cube.x, cube.y, cube.z - 1);
        result[1] = cubeMap.containsKey(back);
        // 左
        Vector3i left = new Vector3i(cube.x - 1, cube.y, cube.z);
        result[2] = cubeMap.containsKey(left);
        // 右
        Vector3i right = new Vector3i(cube.x + 1, cube.y, cube.z);
        result[3] = cubeMap.containsKey(right);
        // 上
        Vector3i top = new Vector3i(cube.x, cube.y + 1, cube.z);
        result[4] = cubeMap.containsKey(top);
        // 下
        Vector3i bottom = new Vector3i(cube.x, cube.y - 1, cube.z);
        result[5] = cubeMap.containsKey(bottom);
        return result;
    }
}
