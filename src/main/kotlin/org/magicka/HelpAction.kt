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
        // Set internationalized menu text
        templatePresentation.text = MagickaBundle.message("menu.about")
        templatePresentation.description = MagickaBundle.message("menu.about.description")
        // Set menu icon
        templatePresentation.icon = MagickaIcons.MAGICKA_ICON
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project

        // Get plugin real version number (use default value if not found)
        val pluginVersion = PluginManagerCore.getPlugin(PluginId.getId("com.kwai.magicka"))?.version ?: "1.0.0"

        // Display plugin information dialog
        Messages.showInfoMessage(
            project,
            MagickaBundle.message("help.dialog.message", pluginVersion),
            MagickaBundle.message("help.dialog.title")
        )
        
        // Output to IDE log
        logger.info("Magicka Plugin information displayed")
    }

    private fun showNotification(project: Project?, title: String, content: String) {
        try {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Magicka Plugin Notifications")
                .createNotification(title, content, NotificationType.INFORMATION)
                .notify(project)
        } catch (e: Exception) {
            // If notification group doesn't exist, use default method
            logger.info("Notification: $title - $content")
        }
    }
}
