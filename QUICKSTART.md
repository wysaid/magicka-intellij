# Magicka 快速开始指南

## 🚀 快速测试插件

### 1. 运行开发版本

在项目根目录执行：

```bash
./gradlew runIde
```

这会启动一个带有 Magicka 插件的 CLion 实例。

### 2. 测试功能

#### 测试 .sl.json 文件处理

1. 在打开的 CLion 中，打开项目根目录的 `example.sl.json` 文件
2. 右键点击文件（在编辑器或项目视图中）
3. 选择 **"Process Shader Configuration"** 菜单项
4. 查看控制台输出，应该看到类似：

```
============================================================
Magicka - Processing Shader JSON File
File Path: /path/to/magicka-clion/example.sl.json
File Name: example.sl.json
============================================================
```

5. 同时会收到一个通知消息

#### 测试帮助菜单

1. 在菜单栏点击 `Tools → Magicka → About Magicka`
2. 会弹出插件信息对话框，显示：
   - 版本号
   - 项目描述
   - 功能说明

### 3. 安装到本地 CLion

如果想在你的日常使用的 CLion 中测试：

```bash
# 构建插件
./gradlew buildPlugin

# 生成的插件位于
# build/distributions/Magicka IntelliJ Plugin-1.0.2.zip
```

然后在 CLion 中：
1. `Settings/Preferences → Plugins`
2. 点击齿轮 ⚙️ → `Install Plugin from Disk...`
3. 选择生成的 ZIP 文件
4. 重启 CLion

## 🎯 创建自己的 .sl.json 文件

在项目中创建一个 `.sl.json` 文件，例如 `test.sl.json`：

```json
{
  "shader": {
    "name": "MyShader",
    "type": "fragment",
    "version": "1.0"
  }
}
```

右键点击即可使用 Magicka 处理。

## 🛠️ 开发构建命令

```bash
# 清理构建
./gradlew clean

# 构建插件
./gradlew buildPlugin

# 运行测试
./gradlew test

# 运行 IDE（开发测试）
./gradlew runIde

# 检查 CLion 版本兼容性
./gradlew checkClionVersion
```

## 📝 日志查看

在开发版 CLion 中，可以查看日志：

1. `Help → Show Log in Finder` (macOS)
2. 或查看控制台输出

## 🎨 修改图标

如果需要修改插件图标，编辑：
```
src/main/resources/META-INF/pluginIcon.svg
```

## 🌐 修改国际化文本

修改菜单文字或提示信息：

- 英文: `src/main/resources/messages/MagickaBundle.properties`
- 中文: `src/main/resources/messages/MagickaBundle_zh_CN.properties`

## ✅ 验证重构

确认以下内容已完全移除：
- [x] 不包含 `xege` 或 `ege` 字样（代码中）
- [x] 不包含 EGE 图形库相关功能
- [x] 新的 Magicka 品牌和图标
- [x] .sl.json 文件处理功能正常

## 🐛 故障排除

### 构建失败

```bash
# 清理并重新构建
./gradlew clean build
```

### 插件无法加载

检查 `plugin.xml` 中的配置是否正确：
- 插件 ID: `org.magicka.creator`
- 资源包路径: `messages.MagickaBundle`

### 国际化不生效

确保资源文件名正确：
- `MagickaBundle.properties`
- `MagickaBundle_zh_CN.properties`

## 📦 发布准备

当准备发布时：

1. 更新版本号：`gradle.properties`
```properties
pluginVersion=1.0.3
```

2. 构建最终版本：
```bash
./gradlew clean buildPlugin
```

3. 测试构建的插件：
```bash
./gradlew runIde
```

4. 插件包位于：`build/distributions/`

---

**提示**: 所有功能都已就绪，你可以开始添加具体的 Shader 代码生成逻辑了！
