import org.gradle.api.GradleException
import java.net.URL
import com.google.gson.Gson

data class JetBrainsRelease(
    val version: String,
    val build: String,
    val type: String
)

data class JetBrainsProductResponse(
    val CL: List<JetBrainsRelease>
)

object VersionChecker {
    private const val JETBRAINS_API_RELEASE = "https://data.services.jetbrains.com/products/releases?code=CL&latest=true&type=release"
    private const val JETBRAINS_API_EAP = "https://data.services.jetbrains.com/products/releases?code=CL&latest=true&type=eap"
    
    /**
     * Get CLion version info from API
     */
    private fun getVersionInfo(apiUrl: String, versionType: String): Pair<String, String>? {
        return try {
            val json = URL(apiUrl).readText()
            val gson = Gson()
            val response = gson.fromJson(json, JetBrainsProductResponse::class.java)
            
            val latestRelease = response.CL.firstOrNull() ?: return null
            
            // Extract major version number (e.g. extract "252" from "252.100.200")
            val buildNumber = latestRelease.build.split(".").firstOrNull() ?: return null
            
            println("✓ CLion latest $versionType version: ${latestRelease.version} (build: ${latestRelease.build}, major: $buildNumber)")
            
            Pair(buildNumber, latestRelease.version)
        } catch (e: Exception) {
            println("⚠️  Warning: Failed to get CLion $versionType version: ${e.message}")
            null
        }
    }
    
    /**
     * Get CLion latest major version number (e.g. 252)
     * Checks both release and EAP versions, returns the higher one
     */
    fun getLatestClionBuildNumber(): String {
        val releaseInfo = getVersionInfo(JETBRAINS_API_RELEASE, "release")
        val eapInfo = getVersionInfo(JETBRAINS_API_EAP, "eap")
        
        val releaseBuild = releaseInfo?.first?.toIntOrNull() ?: 0
        val eapBuild = eapInfo?.first?.toIntOrNull() ?: 0
        
        return when {
            releaseBuild == 0 && eapBuild == 0 -> {
                throw GradleException("Unable to get CLion version information from JetBrains API")
            }
            eapBuild > releaseBuild -> {
                println("✓ Using EAP version as latest: ${eapInfo?.first}")
                eapInfo!!.first
            }
            else -> {
                println("✓ Using release version as latest: ${releaseInfo?.first}")
                releaseInfo!!.first
            }
        }
    }
    
    /**
     * Extract major version number from untilBuild string (e.g. extract "252" from "252.*")
     */
    fun extractBuildNumber(untilBuild: String): String {
        return untilBuild.replace(".*", "").trim()
    }
    
    /**
     * Check if untilBuild matches latest version
     */
    fun checkVersion(currentUntilBuild: String, throwOnMismatch: Boolean = true): Boolean {
        val latestBuild = getLatestClionBuildNumber()
        val currentBuild = extractBuildNumber(currentUntilBuild)
        
        println("\n=== Version Check ===")
        println("Current untilBuild: $currentUntilBuild (major version: $currentBuild)")
        println("CLion latest available major version: $latestBuild")
        
        val currentBuildNum = currentBuild.toIntOrNull() ?: 0
        val latestBuildNum = latestBuild.toIntOrNull() ?: 0
        
        // Allow currentBuild >= latestBuild (might be targeting newer EAP)
        val isValid = currentBuildNum >= latestBuildNum
        
        if (isValid) {
            if (currentBuildNum > latestBuildNum) {
                println("✓ Version check passed! untilBuild ($currentBuild) is ahead of latest available version ($latestBuild)")
                println("  This is acceptable - you may be targeting a newer EAP version")
            } else {
                println("✓ Version check passed! untilBuild matches the latest available version")
            }
        } else {
            val message = """
                ❌ Version check failed!
                Current untilBuild: $currentUntilBuild
                CLion latest available version: $latestBuild
                
                Your untilBuild is outdated. Please run the following command to update:
                ./gradlew updateUntilBuild
            """.trimIndent()
            
            if (throwOnMismatch) {
                throw GradleException(message)
            } else {
                println(message)
            }
        }
        
        return isValid
    }
}
