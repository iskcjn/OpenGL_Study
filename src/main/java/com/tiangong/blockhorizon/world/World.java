package com.tiangong.blockhorizon.world;

import com.tiangong.blockhorizon.world.chunk.Chunk;
import org.joml.Vector3i;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class World {
    public List<Chunk> chunks;

    public HashMap<Vector3i, Chunk> chunkMap;

    public World() throws IOException {
        this.chunks = new java.util.ArrayList<>();
        this.chunkMap = new HashMap<>();

        generate();
    }

    public void generate() throws IOException {
        for (int y = 0; y < 16; y++) {
            for (int z = -2; z < 2; z++) {
                for (int x = -2; x < 2; x++) {
                    addChunk(x, y, z);
                }
            }
        }
        upChunkData();
        System.out.println("世界生成完毕");
    }

    public void addChunk(int x, int y, int z) throws IOException {
        Chunk chunk = new Chunk(x, y, z);
        chunkMap.put(new Vector3i(x, y, z), chunk);
        chunks.add(chunk);
    }
    public void upChunkData(){
        for (Chunk chunk : chunks) {
            chunk.generateMesh(chunkMap);
            chunk.render.updateVertices(chunk.vertices);
            chunk.render.initBuffer();
        }
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