package org.magicka.action

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.magicka.MagickaBundle
import org.magicka.MagickaIcons
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * 处理 .sl.json 文件的 Action
 * 当用户右键点击 .sl.json 文件时触发
 */
class ProcessShaderJsonAction : AnAction() {
    private val logger = Logger.getInstance(ProcessShaderJsonAction::class.java)
    
    companion object {
        private const val MIN_VERSION = "0.37.2"
        @Volatile
        private var versionChecked = false
    }
    
    /**
     * 处理结果数据类
     */
    data class ProcessResult(
        val success: Boolean,
        val message: String,
        val stdout: String = "",
        val stderr: String = ""
    )
    
    init {
        // 设置国际化的菜单文本
        templatePresentation.text = MagickaBundle.message("action.process.shader.json")
        templatePresentation.description = MagickaBundle.message("action.process.shader.json.description")
        // 设置菜单图标
        templatePresentation.icon = MagickaIcons.MAGICKA_ICON
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        
        if (virtualFile != null) {
            val filePath = virtualFile.path
            val fileName = virtualFile.name

            // 调试弹窗, 表示执行到这里了, 显示一下待处理的文件名和路径
            // Messages.showInfoMessage(project, "Debug: Reached here\nFile Name: $fileName\nFile Path: $filePath", "Debug")

            // 后台执行命令，避免阻塞 UI
            object : Task.Backgroundable(project, MagickaBundle.message("action.process.shader.json"), false) {
                override fun run(indicator: ProgressIndicator) {
                    indicator.text = MagickaBundle.message("action.process.shader.json.description")

                    // 统一的 CLI 就绪检查（npm、包安装、版本一次性检查）
                    if (!ensureMagickaCliReady(project)) return

                    // 根据文件后缀分流处理
                    when {
                        fileName.endsWith(".sl.json") -> {
                            val result = processShaderJson(filePath, project, fileName)
                            showProcessResult(result, project, fileName)
                        }
                        fileName.endsWith(".spv.vert") || fileName.endsWith(".spv.frag") -> {
                            processSpvFile(filePath, project, fileName)
                        }
                        else -> {
                            // 非预期类型，忽略
                            logger.info("Magicka action ignored for unsupported file: $fileName")
                        }
                    }
                }
            }.queue()
        }
    }

    override fun update(e: AnActionEvent) {
        // 只有当选中的是 .sl.json 或者 .spv.vert / .spv.frag 文件时才显示此 action
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val name = virtualFile?.name ?: ""
        val isVisible = name.endsWith(".sl.json") || name.endsWith(".spv.vert") || name.endsWith(".spv.frag")
        e.presentation.isEnabledAndVisible = isVisible
    }

    /**
     * 确保 Magicka CLI 环境就绪：包含 npm 检测、包安装检测，以及版本一次性检查。
     */
    private fun ensureMagickaCliReady(project: com.intellij.openapi.project.Project?): Boolean {
        // 检查 npm
        val npm = if (SystemInfo.isWindows) "npm.cmd" else "npm"
        if (!isExecutableAvailable(npm)) {
            showInfo(
                project,
                MagickaBundle.message("env.nodejs.not.installed.title"),
                MagickaBundle.message("env.nodejs.not.installed.message")
            )
            return false
        }

        // 检查 @ks-facemagic/magicka 是否全局安装
        val checkCmd = GeneralCommandLine(npm)
            .withParameters("ls", "-g", "@ks-facemagic/magicka", "--depth=0")
            .withCharset(StandardCharsets.UTF_8)
        val checkOutput = runCommand(checkCmd)
        val installed = checkOutput.exitCode == 0 && !checkOutput.stderr.contains("missing", ignoreCase = true)
        if (!installed) {
            val installCmd = "npm install -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com"
            showInfo(
                project,
                MagickaBundle.message("env.magicka.not.installed.title"),
                MagickaBundle.message("env.magicka.not.installed.message", installCmd)
            )
            return false
        }

        // 版本检查（仅本次 IDE 会话检查一次）
        if (!versionChecked) {
            checkMagickaVersion(project)
            versionChecked = true
        }
        return true
    }

    /**
     * 处理 .sl.json 文件：调用 magicka 生成模板
     * @return ProcessResult 处理结果
     */
    private fun processShaderJson(filePath: String, project: com.intellij.openapi.project.Project?, fileName: String): ProcessResult {
        val npx = if (SystemInfo.isWindows) "npx.cmd" else "npx"
        val cmd = GeneralCommandLine(npx)
            .withParameters("magicka", "generate-starlight-template", filePath)
            .withWorkDirectory(project?.basePath)
            .withCharset(StandardCharsets.UTF_8)

        val output = runCommand(cmd)
        return if (output.exitCode == 0) {
            ProcessResult(
                success = true,
                message = MagickaBundle.message("result.generation.success"),
                stdout = output.stdout,
                stderr = output.stderr
            )
        } else {
            ProcessResult(
                success = false,
                message = MagickaBundle.message("result.generation.failed"),
                stdout = output.stdout,
                stderr = output.stderr
            )
        }
    }

    /**
     * 处理 .spv.vert / .spv.frag 文件：查找关联的 .sl.json 并处理
     */
    private fun processSpvFile(filePath: String, project: com.intellij.openapi.project.Project?, fileName: String) {
        logger.info("Magicka - Processing SPV shader file: name=$fileName, path=$filePath")
        
        try {
            // 1. 获取当前文件的父目录，查找所有 .sl.json 文件
            val currentFile = File(filePath)
            val parentDir = currentFile.parentFile
            if (parentDir == null || !parentDir.exists()) {
                logger.warn("Parent directory not found for: $filePath")
                showError(
                    project,
                    MagickaBundle.message("spv.parent.dir.not.found.title"),
                    MagickaBundle.message("spv.parent.dir.not.found.message", fileName)
                )
                return
            }
            
            val slJsonFiles = parentDir.listFiles { _, name -> name.endsWith(".sl.json") }
            if (slJsonFiles == null || slJsonFiles.isEmpty()) {
                logger.info("No .sl.json files found in directory: ${parentDir.absolutePath}")
                showInfo(
                    project,
                    MagickaBundle.message("spv.no.config.found.title"),
                    MagickaBundle.message("spv.no.config.found.message", fileName)
                )
                return
            }
            
            logger.info("Found ${slJsonFiles.size} .sl.json file(s) in directory: ${parentDir.absolutePath}")
            
            // 统计匹配的配置文件数量
            var matchedConfigCount = 0
            val processedConfigs = mutableListOf<String>()
            
            // 2. 遍历每个 .sl.json 文件
            val gson = Gson()
            for (slJsonFile in slJsonFiles) {
                
                // 读取并解析 JSON
                val jsonContent = slJsonFile.readText(Charsets.UTF_8)
                val jsonObject = JsonParser.parseString(jsonContent).asJsonObject
                
                // 获取 data 数组
                if (!jsonObject.has("data")) {
                    logger.warn("No 'data' field in ${slJsonFile.name}, skipping")
                    continue
                }
                
                val dataArray = jsonObject.getAsJsonArray("data")
                if (dataArray == null || dataArray.size() == 0) {
                    logger.info("Empty 'data' array in ${slJsonFile.name}, skipping")
                    continue
                }
                
                // 3. 过滤：只保留 vsh 或 fsh 匹配当前文件名的对象
                val filteredArray = dataArray.filter { element ->
                    if (!element.isJsonObject) return@filter false
                    val obj = element.asJsonObject
                    val vsh = obj.get("vsh")?.asString
                    val fsh = obj.get("fsh")?.asString
                    vsh == fileName || fsh == fileName
                }
                
                if (filteredArray.isEmpty()) {
                    logger.info("No matching entries for $fileName in ${slJsonFile.name}, skipping")
                    continue
                }
                
                logger.info("Found ${filteredArray.size} matching entry(ies) for $fileName in ${slJsonFile.name}")
                
                // 找到了匹配项，记录成功
                matchedConfigCount++
                processedConfigs.add(slJsonFile.name)
                
                // 4. 构建结果 JSON 对象
                val resultJson = JsonObject()
                val resultArray = com.google.gson.JsonArray()
                filteredArray.forEach { resultArray.add(it) }
                resultJson.add("data", resultArray)
                
                // 5. 保存为 .processing.sl.json 临时文件
                val processingFile = File(parentDir, ".processing.sl.json")
                processingFile.writeText(gson.toJson(resultJson), Charsets.UTF_8)
                logger.info("Created temporary processing file: ${processingFile.absolutePath}")
                
                // 6. 调用 processShaderJson 处理临时文件
                val result = processShaderJson(processingFile.absolutePath, project, processingFile.name)
                
                // 清理临时文件
                try {
                    if (processingFile.exists()) {
                        processingFile.delete()
                        logger.info("Cleaned up temporary file: ${processingFile.name}")
                    }
                } catch (e: Exception) {
                    logger.warn("Failed to delete temporary file: ${processingFile.absolutePath}", e)
                }
                
                // 检查处理结果，失败则立即停止并显示错误
                if (!result.success) {
                    logger.warn("Processing failed for ${slJsonFile.name} with SPV file $fileName")
                    val errorDetail = result.stderr.ifBlank { result.stdout }
                    showError(
                        project,
                        MagickaBundle.message("result.generation.failed"),
                        "${MagickaBundle.message("result.processing.failed.message", slJsonFile.name)}\n\n${trimToLines(errorDetail, 15)}"
                    )
                    return
                }
                
                // 记录成功处理
                logger.info("Successfully processed ${slJsonFile.name} for $fileName")
            }
            
            // 7. 显示最终统一的处理结果（只要找到匹配的配置文件就算成功）
            if (matchedConfigCount > 0) {
                // 找到了匹配的配置文件，算作成功
                val configList = processedConfigs.joinToString(", ")
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("Magicka Plugin Notifications")
                    .createNotification(
                        MagickaBundle.message("notification.process.shader.json.title"),
                        MagickaBundle.message("result.processing.complete", fileName, matchedConfigCount, configList),
                        NotificationType.INFORMATION
                    )
                    .notify(project)
            } else {
                // 一个都没找到，算作失败
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("Magicka Plugin Notifications")
                    .createNotification(
                        MagickaBundle.message("result.no.match.title", fileName),
                        MagickaBundle.message("result.no.match.message", fileName),
                        NotificationType.WARNING
                    )
                    .notify(project)
            }
            
        } catch (e: Exception) {
            logger.warn("Failed to process SPV file: $filePath", e)
            showError(
                project,
                MagickaBundle.message("result.processing.failed.title"),
                MagickaBundle.message("result.processing.failed.message", e.message ?: "Unknown error")
            )
        }
    }

    private fun isExecutableAvailable(exe: String): Boolean {
        return try {
            val cmd = GeneralCommandLine(exe).withParameters("--version").withCharset(StandardCharsets.UTF_8)
            val out = runCommand(cmd)
            out.exitCode == 0
        } catch (t: Throwable) {
            false
        }
    }

    private fun checkMagickaVersion(project: com.intellij.openapi.project.Project?) {
        try {
            val npx = if (SystemInfo.isWindows) "npx.cmd" else "npx"
            val cmd = GeneralCommandLine(npx)
                .withParameters("magicka", "--version")
                .withCharset(StandardCharsets.UTF_8)
            
            val output = runCommand(cmd)
            if (output.exitCode == 0) {
                val versionStr = output.stdout.trim()
                logger.info("Detected magicka version: $versionStr")
                
                if (compareVersions(versionStr, MIN_VERSION) < 0) {
                    // 版本低于最低要求，显示警告
                    val upgradeCmd = "npm update -g @ks-facemagic/magicka --registry https://npm.corp.kuaishou.com"
                    Messages.showWarningDialog(
                        project,
                        MagickaBundle.message("env.magicka.version.low.message", versionStr, MIN_VERSION, upgradeCmd),
                        MagickaBundle.message("env.magicka.version.low.title")
                    )
                }
            }
        } catch (t: Throwable) {
            logger.warn("Failed to check magicka version", t)
        }
    }

    /**
     * 比较两个语义版本号
     * @return 负数表示 v1 < v2，0 表示相等，正数表示 v1 > v2
     */
    private fun compareVersions(v1: String, v2: String): Int {
        try {
            val parts1 = v1.split(".").map { it.toIntOrNull() ?: 0 }
            val parts2 = v2.split(".").map { it.toIntOrNull() ?: 0 }
            
            val maxLen = maxOf(parts1.size, parts2.size)
            for (i in 0 until maxLen) {
                val p1 = parts1.getOrNull(i) ?: 0
                val p2 = parts2.getOrNull(i) ?: 0
                val cmp = p1.compareTo(p2)
                if (cmp != 0) return cmp
            }
            return 0
        } catch (e: Exception) {
            logger.warn("Failed to compare versions: $v1 vs $v2", e)
            return 0 // 无法比较时认为版本相同
        }
    }

    private fun runCommand(cmd: GeneralCommandLine): ProcessOutput {
        logger.info("Running: ${cmd.commandLineString}")
        return try {
            val handler = CapturingProcessHandler(cmd)
            handler.runProcess(60_000, true) // 60s 超时
        } catch (t: Throwable) {
            val po = ProcessOutput()
            po.appendStderr(t.message ?: t.toString())
            po.setExitCode(1)
            po
        }
    }

    private fun trimToLines(text: String, maxLines: Int): String {
        val lines = text.lines().filter { it.isNotBlank() }
        return if (lines.size <= maxLines) text.trim() else (lines.take(maxLines).joinToString("\n") + "\n...")
    }

    private fun showInfo(project: com.intellij.openapi.project.Project?, title: String, message: String) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showInfoMessage(project, message, title)
        }
    }
    
    private fun showError(project: com.intellij.openapi.project.Project?, title: String, message: String) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showErrorDialog(project, message, title)
        }
    }
    
    /**
     * 显示处理结果通知
     */
    private fun showProcessResult(result: ProcessResult, project: com.intellij.openapi.project.Project?, fileName: String) {
        if (result.success) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Magicka Plugin Notifications")
                .createNotification(
                    MagickaBundle.message("notification.process.shader.json.title"),
                    "${result.message}：$fileName\n${trimToLines(result.stdout, 10)}",
                    NotificationType.INFORMATION
                )
                .notify(project)
        } else {
            val err = result.stderr.ifBlank { result.stdout }
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Magicka Plugin Notifications")
                .createNotification(
                    "${result.message}：$fileName",
                    trimToLines(err, 20),
                    NotificationType.ERROR
                )
                .notify(project)
        }
    }
}
