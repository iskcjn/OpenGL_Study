package com.tiangong.blockhorizon.world;

import com.tiangong.blockhorizon.world.chunk.Chunk;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class World {
    public List<Chunk> _chunkList;

    public World() {
        _chunkList = new ArrayList<>();
        addChunk();
    }

    public void addChunk() {
        for (int y = 0; y < 1; y++) {
            for (int z = 0; z < 3; z++) {
                for (int x = 0; x < 3; x++) {
                    Chunk chunk = new Chunk(new Vector3i(x, y, z));
                    chunk.addBlock();
                    chunk.mesh.updateIndices();
                    chunk.mesh.bind();
                    _chunkList.add(chunk);
                }
            }
        }
    }
}
