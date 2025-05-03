# Singleton Pattern in Giftcard Generation

## Overview of the Singleton Pattern

The Singleton pattern is a design pattern that ensures a class has only one instance and provides a global point of access to it. 
In this project, we use the Singleton pattern to ensure we have single instances of the `GiftcardGenerationService` and 
configurations that should be shared across the application.

## Why We're Using the Singleton Pattern

In this application, certain components should exist as only one instance throughout the system's lifecycle:

1. **Configuration Management**: The application settings should be loaded once and shared consistently
2. **Service Control**: Services that manage sequential operations like card generation need controlled access

Using the Singleton pattern helps us:
- Ensure data consistency across the application
- Control access to shared resources
- Avoids duplicate instances

## Implementation in the Project

Kotlin offers multiple ways to implement the Singleton pattern, and this project demonstrates a couple approaches.

### 1. Using Kotlin Objects - AppConfig

The simplest implementation of Singleton in Kotlin is using the `object` declaration in the `AppConfig.kt` file:

```kotlin
object AppConfig {
    private val configMap: Map<String, Any>
    val writeToCsvFile: Boolean
    val csvGiftCardFilePath: String
    val csvSequenceFilePath: String
    
    init {
        // Load configuration once during initialization
    }
}
```

This object declaration creates a singleton object that is instantiated lazily when first accessed. 
The configuration values are loaded once in the `init` block and then shared throughout the application.

Using the singleton ensures all components of the application use the same configuration values, making behavior consistent and predictable.

### 2. Using Companion Object - GenerateCardService

For cases where we want a singleton class with instance methods, we implement the pattern using a companion object:

```kotlin
class GenerateCardService private constructor() {
    // Service implementation...
    
    companion object {
        @Volatile private var instance: GenerateCardService? = null
        fun getInstance(): GenerateCardService {
            return instance ?: synchronized(this) {
                instance ?: GenerateCardService().also { instance = it }
            }
        }
    }
}
```

This implementation (in `GenerateCardService.kt`) uses the double-check locking pattern to ensure thread safety. 
The `@Volatile` annotation ensures the value of `instance` is always up-to-date across multiple threads.

Using a singleton for the `GenerateCardService` is important as it manages card sequence generation. 
If multiple instances existed, it might generate cards with duplicate sequence numbers.

### 3. Anonymous Objects - Not Singleton - Within GiftCardCsvHandler

Anonymous objects in Kotlin are not true singletons but are used for single-use implementations. They also use the `object`
keyword:

```kotlin
private val csvReader = object {
    fun readCards(): List<Card> {
        // Implementation
    }
}
```

In `GiftCardCsvHandler.kt` (lines 46-68), these objects organize related functionality within the class. 
While there's only one instance of each anonymous object per handler instance, they aren't globally accessible like true singletons.

## Kotlin Object Types Explained

Kotlin offers several ways to create object instances:

1. **Object Declaration** (`object AppConfig`):
    - Creates a true singleton
    - Lazily initialized on first access
    - Can implement interfaces and inherit from classes
    - Good for static utility classes or configuration management that don't need state
    - Example: `AppConfig` in the project

2. **Companion Object** (`companion object {...}`):
    - A single global instance (singleton behavior)
    - Instance methods that can access and modify the one instance state
    - The ability to initialize the singleton with parameters or complex setup
    - Example: `GenerateCardService` in the project

3. **Anonymous Object** (`val x = object { ... }`):
    - Similar to anonymous classes in Java
    - Creates a one-off instance with custom functionality
    - Not a true singleton (multiple instances can exist)
    - Example: `csvReader` and `csvWriter` in GiftCardCsvHandler

## Without the Singleton Pattern

Without the Singleton pattern, the application would face several issues:

### Configuration Management Without Singleton

Without `AppConfig` as a singleton:

```kotlin
class ConfigLoader {
    fun loadConfig(): Map<String, Any> {
        // Reload YAML file every time
        val inputStream = javaClass.classLoader.getRestheceAsStream("application.yml")
        val yaml = Yaml()
        return yaml.load(inputStream)
    }
}

// Used every place that needs the configs:
val config = ConfigLoader().loadConfig()
val writeToCsv = config["app.writeToCsvFile"] as Boolean
```

Issues this would cause:
- Configuration loaded multiple times, wasting resources
- Potential inconsistencies if the file changed during runtime
- More complex error handling needed at each use point

### Card Generation Without Singleton

Without `GenerateCardService` as a singleton:

```kotlin
// In App.kt
val cardService1 = GenerateCardService()
val cards1 = cardService1.generateCards(5, 100.0, CardType.GIFT)

// In another class of the application that could call generateCards()
val cardService2 = GenerateCardService()
val cards2 = cardService2.generateCards(3, 50.0, CardType.PREPAID)
```

This would cause:
- Multiple services with separate sequence tracking
- Potential duplicate card numbers being generated
- Inconsistent sequence updates in the storage system
- Race conditions when multiple services try to update the sequence

## Benefits of The Singleton Implementation

1. **Thread Safety**: the implementations handle concurrent access properly
2. **Lazy Initialization**: Classes are only created when needed
3. **Global Access**: Important components are easily accessible throughout the application
4. **Controlled Creation**: We prevent accidental creation of multiple instances of critical services

## Conclusion

The Singleton pattern is important in the gift card generation system, particularly for managing configuration settings and controlling card sequence generation. 
Kotlin's language features make implementing singletons straightforward and fairly simple compared to other languages. 
By ensuring single instances of key components, we maintain data consistency and prevent issues like duplicate card numbers that would otherwise compromise the application's reliability.
