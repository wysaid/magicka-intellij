package org.magicka

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * Magicka plugin icons
 */
object MagickaIcons {
    /**
     * Main icon - used for menus and toolbars
     */
    @JvmField
    val MAGICKA_ICON: Icon = IconLoader.getIcon("/icons/magicka_13x13.svg", MagickaIcons::class.java)
}
