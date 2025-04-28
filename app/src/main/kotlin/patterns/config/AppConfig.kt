package patterns.service

import org.yaml.snakeyaml.Yaml
import java.io.InputStream

/**
 * AppConfig is a singleton object that loads configuration from a YAML file. The singleton object ensure we have one
 * copy of the configs in the entire application.
 * It provides access to application-specific settings.
 * More information can be found here: https://kotlinlang.org/docs/object-declarations.html
 */
object AppConfig {
    private val configMap: Map<String, Any>

    val writeToCsvFile: Boolean
    val csvGiftCardFilePath: String
    val csvSequenceFilePath: String

    init {
        // Load YAML file
        val inputStream: InputStream = javaClass.classLoader.getResourceAsStream("application.yml")
            ?: throw RuntimeException("Could not find application.yml")

        val yaml = Yaml()
        @Suppress("UNCHECKED_CAST")
        configMap = yaml.load(inputStream) as Map<String, Any>

        // Extract app config
        @Suppress("UNCHECKED_CAST")
        val appConfig = configMap["app"] as? Map<String, Any>
            ?: throw RuntimeException("Missing 'app' configuration")

        // Initialize properties
        writeToCsvFile = appConfig["writeToCsvFile"] as? Boolean ?: false
        csvGiftCardFilePath = appConfig["csvGiftCardFilePath"] as? String ?: ""
        csvSequenceFilePath = appConfig["csvSequenceFilePath"] as? String ?: ""
    }
}