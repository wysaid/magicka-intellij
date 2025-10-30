# Magicka - CLion 插件

✨ 为视觉特效开发者打造的 Shader 代码生成工具

---

## 📖 简介

Magicka 是一款为 CLion 开发的插件,旨在简化 Shader 开发工作流。通过集成 Magicka CLI 工具,提供自动化的 Shader 模板生成功能,支持多种文件格式,帮助开发者快速完成 Shader 配置到代码的转换。

### 核心特性

- 🎯 **多文件格式支持** - 处理 `.sl.json`、`.spv.vert`、`.spv.frag` 文件
- ⚡ **快捷键操作** - `Ctrl+Alt+Meta+L` (Mac: `Cmd+Ctrl+Alt+L`) 快速处理文件
- 🔧 **环境自动检测** - 自动检查 Node.js、npm 和 Magicka CLI 环境
- 📦 **版本管理** - 自动检测 CLI 版本,低于 0.37.2 时友好提示升级
- � **国际化支持** - 完整的中英文界面支持
- 🚀 **后台执行** - 非阻塞式命令执行,不影响 IDE 使用

---

## 📥 安装

### 前置要求

1. **CLion 2023.3 或更高版本**
2. **Node.js 和 npm** - [下载安装](https://nodejs.org/)
3. **Magicka CLI 工具** (插件会自动检测并提示安装)

### 安装 Magicka CLI

```bash
npm install -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com
```

### 安装插件

#### 方法一: 从源码构建

```bash
# 克隆仓库
git clone https://git.corp.kuaishou.com/facemagic/magicka-intellij.git
cd magicka-intellij

# 构建插件
./gradlew buildPlugin

# 插件 ZIP 文件位于: build/distributions/
```

#### 方法二: 手动安装

1. 下载插件 ZIP 文件
2. 打开 CLion,前往 `Settings/Preferences → Plugins`
3. 点击齿轮图标 ⚙️ → `Install Plugin from Disk...`
4. 选择下载的 ZIP 文件
5. 重启 CLion

---

## 使用指南

### 处理 Shader 配置文件

#### 1. 处理 `.sl.json` 文件

在项目中右键点击 `.sl.json` 文件,选择 **Magicka → 处理 Shader 配置**,或使用快捷键 `Ctrl+Alt+Meta+L`。

**配置文件格式示例:**

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

插件将调用 `npx magicka generate-starlight-template` 命令生成对应的 Shader 模板代码。

#### 2. 处理 `.spv.vert` / `.spv.frag` 文件

右键点击 SPV 着色器文件,插件会:

1. 自动查找同目录下的所有 `.sl.json` 配置文件
2. 过滤出包含当前文件引用的配置条目
3. 为每个匹配的配置生成临时文件并调用 Magicka CLI 处理
4. 显示处理结果通知

### 环境检查

插件会在首次运行时自动检查:

- ✅ npm 是否已安装 (未安装则提示安装 Node.js)
- ✅ `@ks-facemagic/magicka` 包是否已全局安装
- ✅ Magicka CLI 版本是否 >= 0.37.2 (每次 IDE 启动仅检查一次)

### 查看插件信息

前往 `Tools → Magicka → 关于 Magicka` 查看插件版本和信息。

---

## 🛠️ 开发

### 技术栈

- **语言**: Kotlin 2.1.21
- **构建工具**: Gradle 8.x
- **插件框架**: IntelliJ Platform Plugin SDK (Gradle IntelliJ Plugin 1.17.4)
- **目标平台**: CLion 2023.3+
- **JDK**: 17

### 构建命令

```bash
# 构建插件
./gradlew buildPlugin

# 启动 CLion 测试插件
./gradlew runIde

# 运行测试
./gradlew test

# 清理构建产物
./gradlew clean
```

### 项目结构

```
magicka-clion/
├── src/
│   ├── main/
│   │   ├── kotlin/org/magicka/
│   │   │   ├── MagickaBundle.kt          # 国际化资源加载
│   │   │   ├── MagickaIcons.kt           # 图标资源
│   │   │   ├── HelpAction.kt             # 帮助菜单
│   │   │   └── action/
│   │   │       ├── MagickaOptionsGroup.kt
│   │   │       └── ProcessShaderJsonAction.kt  # 核心处理逻辑
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   ├── plugin.xml            # 插件配置
│   │       │   └── pluginIcon.svg        # 插件图标
│   │       ├── icons/
│   │       │   └── magicka_13x13.svg     # 菜单图标
│   │       └── messages/
│   │           ├── MagickaBundle.properties         # 英文资源
│   │           └── MagickaBundle_zh_CN.properties   # 中文资源
│   └── test/
├── build.gradle.kts
├── settings.gradle.kts
└── .gitlab-ci.yml                        # CI/CD 配置
```

### CI/CD 流程

项目使用 GitLab CI/CD 进行持续集成:

- **build** - 编译和构建插件
- **test** - 执行单元测试
- **package** - 打包插件分发包
- **release** - 发布时自动归档产物

---

## 🔍 工作原理

### 文件处理流程

#### `.sl.json` 文件

```text
右键点击 → 环境检查 → 调用 npx magicka generate-starlight-template → 显示结果
```

#### `.spv.vert` / `.spv.frag` 文件

```text
右键点击 
  → 环境检查 
  → 查找同目录 .sl.json 文件 
  → 解析 JSON 并过滤匹配项 
  → 生成临时 .processing.sl.json 
  → 调用 Magicka CLI 处理 
  → 清理临时文件 
  → 显示统一结果通知
```

### 错误处理

- 所有用户可见的错误通过对话框或通知显示,不会暴露为 IDE 异常
- 日志信息使用英文记录,便于开发调试
- 命令执行超时设置为 60 秒,防止长时间阻塞

---

## 📋 常见问题

### Q: 提示 "Node.js 未安装" 怎么办?

A: 前往 <https://nodejs.org/> 下载安装 Node.js,安装完成后重启 IDE。

### Q: 提示 "Magicka CLI 未安装" 怎么办?

A: 在终端执行: `npm install -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com`

### Q: 提示版本过低怎么办?

A: 在终端执行: `npm update -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com`

### Q: 处理 SPV 文件时没有找到配置怎么办?

A: 确保同目录下存在 `.sl.json` 文件,并且文件中的 `data` 数组包含了对应的 `vsh` 或 `fsh` 字段引用。

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request!

### 贡献流程

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件。

---

## 🔗 相关链接

- [GitLab 仓库](https://git.corp.kuaishou.com/facemagic/magicka-intellij)
- [Issue 追踪](https://git.corp.kuaishou.com/facemagic/magicka-intellij/-/issues)
- [内部 npm 源](https://npm.corp.kuaishou.com)

---

## 👥 作者

### 快手 FaceMagic 团队

- 开发者: wangyang
- 邮箱: <wangyang@kuaishou.com>
- 组织: 快手 FaceMagic 团队

---

**Magicka** - 让 Shader 开发更简单 ✨

Made with ❤️ by Kuaishou FaceMagic Team
