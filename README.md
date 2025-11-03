# Magicka Creator - JetBrains Plugin

✨ Shader Code Generation Tool for Visual Effects Developers

[![Build Status](https://github.com/wysaid/magicka-intellij/actions/workflows/ci.yml/badge.svg)](https://github.com/wysaid/magicka-intellij/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[中文版本](./README_zh_CN.md) | English

---

## 📖 Introduction

Magicka Creator is a plugin developed for JetBrains IDEs, designed to simplify the Shader development workflow. By integrating the Magicka CLI tool, it provides automated Shader template generation, supports multiple file formats, and helps developers quickly complete the conversion from Shader configuration to code.

### Core Features

- 🎯 **Multiple File Format Support** - Process `.sl.json`, `.spv.vert`, `.spv.frag` files
- ⚡ **Keyboard Shortcuts** - `Ctrl+Alt+Meta+L` (Mac: `Cmd+Ctrl+Alt+L`) for quick file processing
- 🔧 **Automatic Environment Detection** - Automatically check Node.js, npm, and Magicka CLI environment
- 📦 **Version Management** - Automatically detect CLI version, friendly prompt for upgrade when lower than 0.37.2
- 🌍 **Internationalization Support** - Complete Chinese and English interface support
- 🚀 **Background Execution** - Non-blocking command execution that doesn't affect IDE usage

---

## 📥 Installation

### Prerequisites

1. **JetBrains IDE 2023.3 or higher** (IntelliJ IDEA, PyCharm, WebStorm, CLion, etc.)
2. **Node.js and npm** - [Download and install](https://nodejs.org/)
3. **Magicka CLI tool** (plugin will automatically detect and prompt installation)

### Installing Magicka CLI

```bash
npm install -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com
```

### Installing the Plugin

#### Method 1: Build from Source

```bash
# Clone the repository
git clone https://github.com/wysaid/magicka-intellij.git
cd magicka-intellij

# Build the plugin
./gradlew buildPlugin

# Plugin ZIP file located at: build/distributions/
```

#### Method 2: Manual Installation

1. Download the plugin ZIP file
2. Open JetBrains IDE, go to `Settings/Preferences → Plugins`
3. Click the gear icon ⚙️ → `Install Plugin from Disk...`
4. Select the downloaded ZIP file
5. Restart IDE

---

## 📖 Usage Guide

### Processing Shader Configuration Files

#### 1. Process `.sl.json` Files

Right-click on a `.sl.json` file in your project, select **Magicka → Process Shader Configuration**, or use the shortcut `Ctrl+Alt+Meta+L`.

**Configuration File Format Example:**

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

The plugin will call the `npx magicka generate-starlight-template` command to generate the corresponding Shader template code.

#### 2. Process `.spv.vert` / `.spv.frag` Files

Right-click on an SPV shader file, the plugin will:

1. Automatically find all `.sl.json` configuration files in the same directory
2. Filter configuration entries that contain references to the current file
3. Generate temporary files for each matched configuration and call Magicka CLI to process
4. Display processing result notifications

### Environment Check

The plugin will automatically check on first run:

- ✅ Whether npm is installed (prompt to install Node.js if not)
- ✅ Whether `@ks-facemagic/magicka` package is globally installed
- ✅ Whether Magicka CLI version >= 0.37.2 (checked once per IDE startup)

### View Plugin Information

In the IDE, go to **Tools → Magicka → About Magicka** to view the plugin version and detailed information.

---

## 🛠️ Development

### Tech Stack

- **Language**: Kotlin 2.1.21
- **Build Tool**: Gradle 8.x
- **Plugin Framework**: IntelliJ Platform Plugin SDK (Gradle IntelliJ Plugin 1.17.4)
- **Target Platform**: JetBrains IDEs 2023.3+ (Test environment: CLion)
- **JDK**: 17

### Build Commands

```bash
# Build the plugin
./gradlew buildPlugin

# Launch test IDE (uses IntelliJ IDEA by default, can be tested in CLion)
./gradlew runIde

# Run tests
./gradlew test

# Clean build artifacts
./gradlew clean
```

### Project Structure

```
magicka-clion/
├── src/
│   ├── main/
│   │   ├── kotlin/org/magicka/
│   │   │   ├── MagickaBundle.kt          # Internationalization resource loader
│   │   │   ├── MagickaIcons.kt           # Icon resources
│   │   │   ├── HelpAction.kt             # Help menu
│   │   │   └── action/
│   │   │       ├── MagickaOptionsGroup.kt
│   │   │       └── ProcessShaderJsonAction.kt  # Core processing logic
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   ├── plugin.xml            # Plugin configuration
│   │       │   └── pluginIcon.svg        # Plugin icon
│   │       ├── icons/
│   │       │   └── magicka_13x13.svg     # Menu icon
│   │       └── messages/
│   │           ├── MagickaBundle.properties         # English resources
│   │           └── MagickaBundle_zh_CN.properties   # Chinese resources
│   └── test/
├── build.gradle.kts
├── settings.gradle.kts
└── .gitlab-ci.yml                        # CI/CD configuration
```

### CI/CD Workflow

The project uses GitLab CI/CD for continuous integration:

- **build** - Compile and build the plugin
- **test** - Execute unit tests
- **package** - Package the plugin distribution
- **release** - Automatically archive artifacts on release

---

## 🔍 How It Works

### File Processing Flow

#### `.sl.json` Files

```text
Right-click → Environment Check → Call npx magicka generate-starlight-template → Display Result
```

#### `.spv.vert` / `.spv.frag` Files

```text
Right-click 
  → Environment Check 
  → Find .sl.json files in same directory 
  → Parse JSON and filter matches 
  → Generate temporary .processing.sl.json 
  → Call Magicka CLI to process 
  → Clean up temporary files 
  → Display unified result notification
```

### Error Handling

- All user-visible errors are displayed via dialogs or notifications, not exposed as IDE exceptions
- Log messages are recorded in English for development debugging
- Command execution timeout is set to 60 seconds to prevent long-term blocking

---

## 📋 FAQ

#### Prompt "Node.js Not Installed"

Go to <https://nodejs.org/> to download and install Node.js, then restart the IDE after installation.

#### Prompt "Magicka CLI Not Installed"

Run in terminal: `npm install -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com`

### Q: What if the version is too low?

A: Execute in terminal: `npm update -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com`

### Q: What if no configuration is found when processing SPV files?

A: Ensure that a `.sl.json` file exists in the same directory, and that the `data` array in the file contains the corresponding `vsh` or `fsh` field references.

---

## 🤝 Contributing

Issues and Pull Requests are welcome!

### Contribution Process

1. Fork this repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Submit a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🔗 Related Links

- [GitHub Repository](https://github.com/wysaid/magicka-intellij)
- [Issue Tracker](https://github.com/wysaid/magicka-intellij/issues)
- [Internal npm Registry](https://npm.corp.kuaishou.com)

---

## 👥 Team & Contributors

This project is maintained by the **Kuaishou FaceMagic Team**.

### Contact

- **Organization**: Kuaishou FaceMagic Team
- **Email**: <wangyang@kuaishou.com>
- **Repository**: [magicka-intellij](https://github.com/wysaid/magicka-intellij)

We welcome contributions from the community! See the [Contributing](#-contributing) section for more details.

---

**Magicka** - Making Shader Development Easier ✨

Made with ❤️ by Kuaishou FaceMagic Team
