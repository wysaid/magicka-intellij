package org.magicka.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import org.magicka.MagickaBundle

/**
 * 处理 .sl.json 文件的 Action
 * 当用户右键点击 .sl.json 文件时触发
 */
class ProcessShaderJsonAction : AnAction() {
    private val logger = Logger.getInstance(ProcessShaderJsonAction::class.java)
    
    init {
        // 设置国际化的菜单文本
        templatePresentation.text = MagickaBundle.message("action.process.shader.json")
        templatePresentation.description = MagickaBundle.message("action.process.shader.json.description")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        if (virtualFile != null) {
            val filePath = virtualFile.path
            
            // 在控制台打印文件路径
            println("=".repeat(60))
            println("Magicka - Processing Shader JSON File")
            println("File Path: $filePath")
            println("File Name: ${virtualFile.name}")
            println("=".repeat(60))
            
            // 同时记录到 IDE 日志
            logger.info("Processing shader JSON file: $filePath")
            
            // 显示通知
            try {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("Magicka Plugin Notifications")
                    .createNotification(
                        MagickaBundle.message("notification.process.shader.json.title"),
                        MagickaBundle.message("notification.process.shader.json.content", virtualFile.name),
                        NotificationType.INFORMATION
                    )
                    .notify(project)
            } catch (ex: Exception) {
                logger.warn("Failed to show notification", ex)
            }
        }
    }

    override fun update(e: AnActionEvent) {
        // 只有当选中的是 .sl.json 文件时才显示此 action
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val isVisible = virtualFile != null && virtualFile.name.endsWith(".sl.json")
        e.presentation.isEnabledAndVisible = isVisible
    }
}
