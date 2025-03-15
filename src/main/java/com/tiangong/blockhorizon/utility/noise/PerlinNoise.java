package com.tiangong.blockhorizon.utility.noise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PerlinNoise {
    private static final int GRADIENT_TABLE_SIZE = 256;
    private static final Random RANDOM = new Random(123456789);
    private static final double[] GRADIENT_TABLE = new double[GRADIENT_TABLE_SIZE];

    static {
        // Initialize gradient table with random gradients
        for (int i = 0; i < GRADIENT_TABLE_SIZE; i++) {
            GRADIENT_TABLE[i] = RANDOM.nextDouble() * 2 - 1; // Random value between -1 and 1
        }
    }

    public static double noise(double x, double z) {
        // Determine grid cell
        int xi = (int) Math.floor(x);
        int zi = (int) Math.floor(z);

        // Compute fade curves
        double sx = fade(x - xi);
        double sz = fade(z - zi);

        // Compute the 4 corner gradients
        double n00 = gradient(xi, zi, x, z);
        double n10 = gradient(xi + 1, zi, x, z);
        double n01 = gradient(xi, zi + 1, x, z);
        double n11 = gradient(xi + 1, zi + 1, x, z);

        // Interpolate
        double n0 = lerp(n00, n10, sx);
        double n1 = lerp(n01, n11, sx);
        return lerp(n0, n1, sz);
    }

    private static double gradient(int xi, int zi, double x, double z) {
        // Hash the coordinates to get a gradient value
        int hash = (xi * 374761393 + zi * 668265263) % GRADIENT_TABLE_SIZE;
        if (hash < 0) {
            hash += GRADIENT_TABLE_SIZE; // Avoid negative indices
        }
        double gradient = GRADIENT_TABLE[hash];

        // Compute the dot product between the gradient and the distance vector
        return gradient * (x - xi) + gradient * (z - zi);
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10); // 6t^5 - 15t^4 + 10t^3
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    public static double calculateHeight(double x, double z, double scale) {
        // Sample noise and scale it to control height variation
        double noiseValue = noise(x * scale, z * scale);
        return (noiseValue + 1) * 20;
        // return (noiseValue + 1) * 20; // Scale to 0-40
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