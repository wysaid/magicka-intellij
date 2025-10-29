package org.magicka

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType

class HelpAction : AnAction() {
    private val logger = Logger.getInstance(HelpAction::class.java)
    
    init {
        // 设置国际化的菜单文本
        templatePresentation.text = MagickaBundle.message("menu.about")
        templatePresentation.description = MagickaBundle.message("menu.about.description")
        // 设置菜单图标
        templatePresentation.icon = MagickaIcons.MAGICKA_ICON
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project

        // 获取插件真实版本号（找不到时使用默认值）
        val pluginVersion = PluginManagerCore.getPlugin(PluginId.getId("com.kwai.magicka"))?.version ?: "1.0.0"

        // 显示插件信息对话框
        Messages.showInfoMessage(
            project,
            MagickaBundle.message("help.dialog.message", pluginVersion),
            MagickaBundle.message("help.dialog.title")
        )
        
        // 在 IDE 日志中输出
        logger.info("Magicka Plugin information displayed")
    }

    private fun showNotification(project: Project?, title: String, content: String) {
        try {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Magicka Plugin Notifications")
                .createNotification(title, content, NotificationType.INFORMATION)
                .notify(project)
        } catch (e: Exception) {
            // 如果通知组不存在，使用默认方式
            logger.info("Notification: $title - $content")
        }
    }
}
