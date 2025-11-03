# Getting Started with Magicka Creator

## 快速开始指南

Magicka Creator 是一款为 Shader 开发者设计的 CLion 插件，通过集成 Magicka CLI 工具，自动化生成 Shader 模板代码，大幅提升开发效率。

---

## 📋 前置准备

### 1. 安装 Node.js

Magicka Creator 需要 Node.js 和 npm 环境支持。

- 访问 <https://nodejs.org/> 下载并安装 Node.js（建议 LTS 版本）
- 安装完成后，在终端验证：

```bash
node --version
npm --version
```

### 2. 安装 Magicka CLI 工具

在终端执行以下命令安装 Magicka CLI：

```bash
npm install -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com
```

安装完成后验证：

```bash
npx magicka --version
```

**注意：** 插件要求 Magicka CLI 版本 >= 0.37.2

---

## 🎯 主要功能

### 1. 处理 Shader 配置文件（.sl.json）

#### 使用方法

1. 在项目中创建或打开 `.sl.json` 文件
2. 右键点击文件，选择 **Magicka → 处理 Shader 配置**
3. 或使用快捷键：
   - **macOS**: `Cmd + Ctrl + Alt + L`
   - **Windows/Linux**: `Ctrl + Win + Alt + L`

#### 配置文件格式

```json
{
  "data": [
    {
      "vsh": "shader.spv.vert",
      "fsh": "shader.spv.frag"
    }
  ]
}
```

插件将自动调用 `npx magicka generate-starlight-template` 生成对应的 Shader 模板代码。

### 2. 处理 SPV 着色器文件（.spv.vert / .spv.frag）

#### 使用方法

1. 右键点击 `.spv.vert` 或 `.spv.frag` 文件
2. 选择 **Magicka → 处理 Shader 配置** 或使用快捷键

#### 工作流程

插件会自动：
- 查找同目录下的所有 `.sl.json` 配置文件
- 解析配置并过滤出引用当前文件的条目
- 为每个匹配的配置生成 Shader 模板
- 显示处理结果通知

---

## ⚙️ 首次使用

### 环境自动检测

插件首次运行时会自动检查：

1. ✅ **npm 环境** - 检测 npm 是否可用
2. ✅ **Magicka CLI** - 检测是否已全局安装
3. ✅ **版本检查** - 验证 CLI 版本是否满足要求（每次 IDE 启动仅检查一次）

### 环境问题处理

#### 提示 "Node.js 未安装"

前往 <https://nodejs.org/> 下载安装 Node.js，安装完成后重启 CLion。

#### 提示 "Magicka CLI 未安装"

在终端执行安装命令：

```bash
npm install -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com
```

#### 提示 "版本过低"

在终端执行升级命令：

```bash
npm update -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com
```

---

## 💡 使用技巧

### 快捷键操作

为了提高效率，建议熟记快捷键：
- **macOS**: `Cmd + Ctrl + Alt + L`
- **Windows/Linux**: `Ctrl + Win + Alt + L`

### 批量处理

当有多个 SPV 文件需要处理时：
1. 确保所有文件在同一目录
2. 创建统一的 `.sl.json` 配置文件
3. 依次处理每个 SPV 文件，插件会自动匹配配置

### 查看处理结果

- 成功时会显示通知，包含生成的代码片段（前 10 行）
- 失败时会显示详细错误信息（前 20 行），便于排查问题

---

## 🔍 常见场景

### 场景 1: 新建 Shader 项目

1. 创建项目目录结构
2. 创建 `.sl.json` 配置文件，定义 Shader 对
3. 创建对应的 `.spv.vert` 和 `.spv.frag` 文件
4. 使用插件生成模板代码
5. 在生成的模板基础上编写具体逻辑

### 场景 2: 修改现有 Shader

1. 修改 `.spv.vert` 或 `.spv.frag` 文件
2. 使用插件重新生成模板（快捷键）
3. 插件会根据配置自动更新相关代码

### 场景 3: 多配置管理

1. 在同一目录创建多个 `.sl.json` 文件
2. 每个配置文件管理不同的 Shader 组合
3. 处理 SPV 文件时，插件会自动匹配所有相关配置

---

## 📚 更多信息

### 查看插件信息

在 CLion 中，前往 **Tools → Magicka → 关于 Magicka** 查看插件版本和详细信息。

### 语言支持

插件完整支持中英文界面，会根据系统语言自动切换。

### 获取帮助

- 项目地址: <https://github.com/wysaid/magicka-intellij>
- 问题反馈: <https://github.com/wysaid/magicka-intellij/issues>

---

## 🎉 开始使用

现在您已经了解了 Magicka Creator 的基本使用方法，可以开始体验自动化 Shader 开发的便利了！

记得使用快捷键 `Cmd/Ctrl + Ctrl + Alt + L` 快速处理文件，让开发更高效！✨
