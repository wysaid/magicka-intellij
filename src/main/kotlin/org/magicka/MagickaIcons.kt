package org.magicka

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * Magicka 插件图标
 */
object MagickaIcons {
    /**
     * 主图标 - 用于菜单和工具栏
     */
    @JvmField
    val MAGICKA_ICON: Icon = IconLoader.getIcon("/icons/magicka_13x13.svg", MagickaIcons::class.java)
}
