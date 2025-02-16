package com.tiangong.blockhorizon.utility;

import java.io.*;

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
     * 合并两个数组
     * @param array1 第一个数组
     * @param array2 第二个数组
     * @return 合并后的数组
     * */
    public static int[] concatenateArrays(int[] array1, int[] array2){
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
}
