# BlockHorizon

## 项目概述
这是一个使用 OpenGL 相关库进行开发的项目，项目主要依赖于 LWJGL 和 JOML 库。内容为学习OpenGL构建体素世界

## 项目配置

### 编译配置
- Java 版本：17
- 编码格式：UTF-8

### 依赖管理
项目使用 Maven 进行依赖管理，主要依赖的库及其版本信息如下：

| 库名称 | 版本 |
| ---- | ---- |
| lwjgl | 3.3.6 |
| joml | 1.10.7 |

### 主要依赖库
项目引入了多个 LWJGL 相关的库，包括但不限于：
- `lwjgl-openal`
- `lwjgl-opengl`
- `lwjgl-opengles`
- 等等

## 项目结构
项目结构包含多个模块，主要的文件和目录功能如下：
- `src/main/java/com/tiangong/blockhorizon/world/chunk`：包含与世界块相关的代码，如 `Mesh.java` 文件，其中定义了 `Draw`、`CleanUp` 等方法用于图形绘制和资源清理。
- `src/main/java/com/tiangong/blockhorizon/utility`：包含工具类，如 `Utility.java` 中的 `readGLSLFile` 方法用于读取 GLSL 文件。

## 使用方法
1. 克隆项目到本地：
   ```sh
   git clone https://github.com/iskcjn/OpenGL_Study.git
