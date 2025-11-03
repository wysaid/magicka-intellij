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
    private const val JETBRAINS_API = "https://data.services.jetbrains.com/products/releases?code=CL&latest=true&type=release"
    
    /**
     * Get CLion latest major version number (e.g. 252)
     */
    fun getLatestClionBuildNumber(): String {
        return try {
            val json = URL(JETBRAINS_API).readText()
            val gson = Gson()
            val response = gson.fromJson(json, JetBrainsProductResponse::class.java)
            
            val latestRelease = response.CL.firstOrNull()
                ?: throw GradleException("Unable to get CLion version information from JetBrains API")
            
            // Extract major version number (e.g. extract "252" from "252.100.200")
            val buildNumber = latestRelease.build.split(".").firstOrNull()
                ?: throw GradleException("Unable to parse CLion build number: ${latestRelease.build}")
            
            println("✓ CLion latest version: ${latestRelease.version} (build: ${latestRelease.build})")
            println("✓ Major version number: $buildNumber")
            
            buildNumber
        } catch (e: Exception) {
            throw GradleException("Failed to get CLion latest version: ${e.message}", e)
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
        println("CLion latest major version: $latestBuild")
        
        val isUpToDate = currentBuild == latestBuild
        
        if (isUpToDate) {
            println("✓ Version check passed! untilBuild is already the latest version")
        } else {
            val message = """
                ❌ Version check failed!
                Current untilBuild: $currentUntilBuild
                CLion latest version: $latestBuild
                
                Please run the following command to update version:
                ./gradlew updateUntilBuild
            """.trimIndent()
            
            if (throwOnMismatch) {
                throw GradleException(message)
            } else {
                println(message)
            }
        }
        
        return isUpToDate
    }
}
