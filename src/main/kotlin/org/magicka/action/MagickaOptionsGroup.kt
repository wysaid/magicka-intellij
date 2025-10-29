package org.magicka.action

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.magicka.MagickaBundle

/**
 * Magicka 工具菜单组
 * 在工具菜单中显示 Magicka 相关选项
 */
class MagickaOptionsGroup : ActionGroup() {
    
    init {
        // 设置国际化的菜单文本
        templatePresentation.text = MagickaBundle.message("menu.magicka.options")
        templatePresentation.description = MagickaBundle.message("menu.magicka.options.description")
    }
    
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        // 返回空数组，因为帮助菜单已经在 plugin.xml 中定义
        return emptyArray()
    }
}
