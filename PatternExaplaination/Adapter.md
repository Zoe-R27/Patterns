# Adapter Pattern in Giftcard Generation

## Overview of the Adapter Pattern

The Adapter pattern is a design pattern that allows classes with incompatible interfaces work with each other. 
In this project, we use it to adapt CSV file operations to behave like a database repository. This way the client, `GenerateCardService`, 
can interact with the CSV file as if it were a database without needing to know the details of how the CSV file is handled.

## Why We're Using the Adapter Pattern

For this project, we are using CSV files for simplicity and easy visualization, but we want the code still act and work as though
it is using a real database implementation.
This is because for a demo project such as this, CSV files are easier to view and spin up compared using a container such as Docker. 
In this way it is possible to just view the CSV file and see the cards generated.
If you want to learn more about Docker you can [read more here](https://www.geeksforgeeks.org/containerization-using-docker/).

The Adapter pattern lets us:
1. Keep the service layer decoupled from CSV implementation details
2. Make the code behave as if it were using a database repository
3. Could allow for easier switching to using a database in the future if needed

## Implementation in the Project

### The Repository Interfaces

The service layer expects these repository interfaces located in the `app/src/main/kotlin/patterns/repository` package:

```kotlin
interface CardRepository {
    fun saveAll(cards: List<Card>): Iterable<Card>
    fun save(card: Card): Iterable<Card>
    fun getAll(): List<Card>
}
```

```kotlin
interface SequenceRepository {
    fun getSequence(): Long?
    fun updateSequence(sequence: Long, sthece: String): SequenceRecord
}
```

These are repository interfaces that define the methods we expect to use in the service layer if we were using a database. 
The goal is to have the client act as though it is using a database repository even as we have adapted the project to use CSV files.

### The CSV Handlers (Adaptees)

The CSV handlers contain the actual file I/O operations:

- `GiftCardCsvHandler` - Manages reading from and writing cards to the `GiftCard.csv` file in the `.data` directory
- `SequenceCsvHandler` - Manages reading from and writing sequences to the `Sequence.csv` file in the `.data` directory

These classes have methods like `writeCardsToFile()`, `readCardsFromFile()`, etc. which contain all the file management logic
that wouldn't be present if we were using a database. We want to keep this logic away from the logic in the service layer
as it is not relevant to the business logic of generating gift cards.

### The Adapters

The adapters bridge the repository interfaces and the CSV handlers:

```kotlin
class CsvCardRepositoryAdapter(private val giftCardCsvHandler: GiftCardCsvHandler) : CardRepository {
    override fun saveAll(cards: List<Card>): Iterable<Card> {
        return giftCardCsvHandler.writeCardsToFile(cards)
    }
    // Other methods...
}
```      

```kotlin
class CsvSequenceRepositoryAdapter(private val sequenceCsvHandler: SequenceCsvHandler) : SequenceRepository {
    // Implementation details
}
```

Each adapter implements a repository interface but delegates the actual work to a CSV handler. 
For example, in `CsvCardRepositoryAdapter`, the `saveAll()` method routes to the handler's `writeCardsToFile()` method
instead of directly writing to a database.

### Using the Adapters in the Service Layer

In `GenerateCardService` (lines 24-30), we create and use these adapters:

```kotlin
// Creating the adapter used in the adapter pattern
private val giftCardCsvHandler = GiftCardCsvHandler()
private val cardRepository = CsvCardRepositoryAdapter(giftCardCsvHandler)

private val sequenceCsvHandler = SequenceCsvHandler()
private val sequenceRepository = CsvSequenceRepositoryAdapter(sequenceRepository)
```

The service then interacts with these repositories using standard methods like `cardRepository.saveAll()` (line 153) 
and `sequenceRepository.getSequence()` (line 118).

## Without the Adapter Pattern

Without this pattern, the `GenerateCardService.kt` would have direct knowledge of CSV file handling

For example, instead of the clean code in `GenerateGiftCardSerice.saveCards()` (lines 139-147) that simply calls the repository using `.saveAll()`,
the client would have direct knowledge of what CSV file operations were being performed.

```kotlin
private fun saveCards(cards: List<Card>) {
    try {
        val file = File(giftCardFilePath)
        val fileExists = file.exists() && file.length() > 0
        
        FileWriter(file, fileExists).use { fileWriter ->
            // CSV writing logic...
            // Header handling...
            // Conversion of card data to CSV format...
        }
    } catch (e: Exception) {
        logger.error("Error writing to CSV file: ${e.message}", e)
        throw e
    }
}
```

Even if we abstracted out this logic into a different class as we do right now with the Handler, 
the service layer would still have to know that the CSV file handling exists to call it.
This would make the service layer tightly coupled to the CSV implementation, making it harder to switch to a real database in the future.

```kotlin
private fun saveCards(cards: List<Card>) {
    val csvHandler = CsvHandler() // Knows about the CSV handling
    csvHandler.writeCardsToFile(cards)
}
```


## Benefits of The Adapter Implementation

1. **Separation of Concerns**: The service layer focuses on business logic while the adapters and handlers deal with the data storage details.
2. **Maintainability**: Changes to storage implementation won't affect the service layer.
3. **Testability**: We can easily mock the repositories for testing the service layer.
4. **Flexibility**: We can switch to a real database implementation without changing the service layer.

## Conclusion
This approach lets us demonstrate the application's functionality with simple CSV files while maintaining a structure that would scale well to a real-world database implementation.
