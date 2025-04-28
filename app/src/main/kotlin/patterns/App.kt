package patterns

import patterns.constants.CardType
import patterns.service.GenerateCardService
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Scanner
import javax.swing.text.DateFormatter

fun main() {
    // Print current working directory
    val currentDir = Paths.get("").toAbsolutePath().toString()
    println("Current working directory: $currentDir")

    val scanner = Scanner(System.`in`)

    // Calling to get the once instance of the Singleton
    val cardService = GenerateCardService.getInstance()

    println("Gift Card Generator")
    println("-----------------")

    // Get number of cards to generate
    print("How many gift cards do you want to generate? ")
    val cardCount = readLongInput(scanner)

    // Get card balance
    print("Enter the balance for each card: ")
    val cardBalance = readDoubleInput(scanner)

    // Select card type
    println("Select card type:")
    CardType.entries.forEachIndexed { index, type ->
        println("${index + 1}. $type")
    }
    print("Enter your choice (1-${CardType.entries.size}): ")
    val cardTypeIndex = readIntInput(scanner, 1, CardType.entries.size) - 1
    val selectedCardType = CardType.entries[cardTypeIndex]

    var expirationDate: LocalDateTime? = null
    if (selectedCardType == CardType.PREPAID) {
        print("\nEnter the expiration date (yyyy-MM-dd HH:mm:ss): ")
        val expirationDateInput = scanner.nextLine()
        expirationDate = try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            LocalDateTime.parse(expirationDateInput, formatter)

        } catch (e: Exception) {
            println("Invalid date format. Using default expiration date.")
            println(e)
            null
        }
    }

    println("\nGenerating $cardCount gift cards with $cardBalance balance each...")

    // Generate the cards
    val generatedCards = cardService.generateCards(cardCount, cardBalance, selectedCardType, expirationDate)

    if (generatedCards.isNotEmpty()) {
        println("Successfully generated ${generatedCards.size} cards!")
        println("Card number range: ${generatedCards.first().number} - ${generatedCards.last().number}")
    } else {
        println("No cards were generated.")
    }
}

private fun readLongInput(scanner: Scanner): Long {
    while (true) {
        try {
            return scanner.nextLine().toLong().also {
                if (it <= 0) throw NumberFormatException("Must be positive")
            }
        } catch (e: NumberFormatException) {
            print("Please enter a valid positive number: ")
        }
    }
}

private fun readDoubleInput(scanner: Scanner): Double {
    while (true) {
        try {
            return scanner.nextLine().toDouble().also {
                if (it <= 0) throw NumberFormatException("Must be positive")
            }
        } catch (e: NumberFormatException) {
            print("Please enter a valid positive number: ")
        }
    }
}

private fun readIntInput(scanner: Scanner, min: Int, max: Int): Int {
    while (true) {
        try {
            return scanner.nextLine().toInt().also {
                if (it < min || it > max) throw NumberFormatException("Out of range")
            }
        } catch (e: NumberFormatException) {
            print("Please enter a number between $min and $max: ")
        }
    }
}
