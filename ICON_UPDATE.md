# 添加菜单图标说明

## 更新内容

为 Magicka 插件的右键菜单和工具菜单添加了图标支持。

### 新增文件

1. **MagickaIcons.kt** - 图标常量类
   - 路径: `src/main/kotlin/org/magicka/MagickaIcons.kt`
   - 功能: 统一管理插件图标资源

2. **magicka_13x13.svg** - 小尺寸图标
   - 路径: `src/main/resources/icons/magicka_13x13.svg`
   - 尺寸: 13x13 像素
   - 用途: 菜单图标（右键菜单、工具菜单等）

### 修改文件

1. **ProcessShaderJsonAction.kt**
   - 添加图标导入: `import org.magicka.MagickaIcons`
   - 在 `init` 块中设置图标: `templatePresentation.icon = MagickaIcons.MAGICKA_ICON`

2. **HelpAction.kt**
   - 添加图标导入: `import org.magicka.MagickaIcons`
   - 在 `init` 块中设置图标: `templatePresentation.icon = MagickaIcons.MAGICKA_ICON`

### 效果

现在当你在 CLion 中：

1. **右键点击 .sl.json 文件**时，会看到 "Magicka: 执行 Shader 转换" 菜单项左侧有紫色魔法棒图标
2. **Tools → Magicka** 菜单中的 "关于 Magicka" 菜单项也会显示图标

### 图标设计

13x13 小图标采用了简化设计：
- 紫色渐变背景（魔法主题色）
- 简化的魔法棒和星星图案
- 适合小尺寸显示，清晰可辨

### 测试方法

1. 运行插件：
```bash
./gradlew runIde
```

2. 在打开的 CLion 中：
   - 创建或打开一个 `.sl.json` 文件
   - 右键点击文件，查看菜单项是否有图标
   - 打开 `Tools → Magicka` 菜单，查看图标

### 技术说明

- 使用 `IconLoader.getIcon()` 加载 SVG 图标
- IntelliJ Platform 会自动处理图标的缩放和渲染
- 图标资源放在 `resources/icons/` 目录下，遵循 IntelliJ 插件开发的最佳实践
