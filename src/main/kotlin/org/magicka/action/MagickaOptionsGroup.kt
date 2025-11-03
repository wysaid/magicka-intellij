package org.magicka.action

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.magicka.MagickaBundle

/**
 * Magicka tool menu group
 * Displays Magicka-related options in the Tools menu
 */
class MagickaOptionsGroup : ActionGroup() {
    
    init {
        // Set internationalized menu text
        templatePresentation.text = MagickaBundle.message("menu.magicka.options")
        templatePresentation.description = MagickaBundle.message("menu.magicka.options.description")
    }
    
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        // Return empty array because the help menu is already defined in plugin.xml
        return emptyArray()
    }
}
