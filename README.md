# Magicka - Shader Code Generator

A powerful CLion plugin for visual effects development, providing shader code generation and text processing tools.

---

## 📥 Installation

### Manual Installation

1. Download the latest plugin ZIP file from the `build/distributions/` directory after building
2. Open CLion, go to `Settings/Preferences → Plugins`
3. Click the gear icon ⚙️ → `Install Plugin from Disk...`
4. Select the downloaded ZIP file
5. Restart CLion

---

## 🎯 Features

### ✨ Shader Configuration Processing

- Right-click on `.sl.json` files to access Magicka tools
- Automatic shader code generation from configuration files
- Real-time processing with console output

### 🔧 Visual Effects Development

- **Shader Code Generation**: Generate shader code from JSON configurations
- **Text Processing**: Advanced text processing utilities for code generation
- **CLion Integration**: Seamless integration with CLion IDE

### 🌍 Cross-Platform Support

- Supports Windows, macOS, and Linux
- Compatible with CLion 2023.3 and above

### 🌐 Internationalization

- Supports Chinese and English interface
- Automatically switches based on system language

---

## 🚀 Usage

### Processing Shader Configuration Files

1. Create or open a `.sl.json` shader configuration file in your project
2. Right-click on the file in the editor or project view
3. Select **Magicka → Process Shader Configuration**
4. The file path will be printed to the console
5. (More features coming soon!)

### Accessing Magicka Tools

- Go to `Tools → Magicka` in the menu bar
- Access **About Magicka** to view plugin information

---

## 📝 Shader Configuration Format

Shader configuration files use the `.sl.json` extension. Example:

```json
{
  "shader": {
    "name": "MyEffect",
    "type": "fragment",
    "version": "1.0"
  }
}
```

(More documentation coming soon!)

---

## 🛠️ Development

### Building from Source

```bash
# Build the plugin
./gradlew buildPlugin

# Run CLion with the plugin for testing
./gradlew runIde

# Run tests
./gradlew test
```

### CI/CD

This project uses GitLab CI/CD for continuous integration:

- **Build**: Runs on every commit to build and test the plugin
- **Test**: Executes all unit tests automatically
- **Package**: Creates distributable plugin package on master branch
- **Release**: Automatically builds and archives plugin when a tag is pushed

### Project Structure

```
magicka-clion/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── org/magicka/
│   │   │       ├── MagickaBundle.kt
│   │   │       ├── HelpAction.kt
│   │   │       └── action/
│   │   │           ├── MagickaOptionsGroup.kt
│   │   │           └── ProcessShaderJsonAction.kt
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   ├── plugin.xml
│   │       │   └── pluginIcon.svg
│   │       └── messages/
│   │           ├── MagickaBundle.properties
│   │           └── MagickaBundle_zh_CN.properties
│   └── test/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

---

## 📄 License

This project is licensed under the MIT License.

---

## 🔗 Links

- [GitLab Repository](https://git.corp.kuaishou.com/facemagic/magicka-intellij)
- [Issue Tracker](https://git.corp.kuaishou.com/facemagic/magicka-intellij/-/issues)

---

## 👤 Author

- **Name**: wangyang
- **Email**: wangyang@kuaishou.com
- **Organization**: Kuaishou FaceMagic Team

---

**Magicka** - Empowering visual effects development with magical code generation ✨
