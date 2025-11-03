package org.magicka

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey
import java.util.*

private const val BUNDLE = "messages.MagickaBundle"

/**
 * Internationalization resource manager
 * 
 * Used to load and retrieve internationalized text resources
 * Automatically selects appropriate language based on system locale
 * 
 * Debug options:
 * Modify DEBUG_LOCALE to test different languages
 * - null: Use system language (default)
 * - Locale.ENGLISH: Force English
 * - Locale.SIMPLIFIED_CHINESE: Force Simplified Chinese
 */
object MagickaBundle : DynamicBundle(BUNDLE) {
    /**
     * Debug language setting
     * 
     * Usage:
     * 1. Test English: Set to Locale.ENGLISH
     * 2. Test Chinese: Set to Locale.SIMPLIFIED_CHINESE
     * 3. Use system language: Set to null
     * 
     * ⚠️ Note: IDE restart required after modification
     */
    private val DEBUG_LOCALE: Locale? = null  // 👈 Modify here to switch language
    // private val DEBUG_LOCALE: Locale? = Locale.ENGLISH  // Force English
    // private val DEBUG_LOCALE: Locale? = Locale.SIMPLIFIED_CHINESE  // Force Chinese
    
    /**
     * Get internationalized message
     * @param key Resource key
     * @param params Optional parameters for replacing placeholders in message
     */
    @JvmStatic
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
        return if (DEBUG_LOCALE != null) {
            // Debug mode: Use specified language
            val bundle = ResourceBundle.getBundle(BUNDLE, DEBUG_LOCALE)
            val pattern = bundle.getString(key)
            if (params.isEmpty()) {
                pattern
            } else {
                java.text.MessageFormat.format(pattern, *params)
            }
        } else {
            // Normal mode: Use system language
            getMessage(key, *params)
        }
    }
}
