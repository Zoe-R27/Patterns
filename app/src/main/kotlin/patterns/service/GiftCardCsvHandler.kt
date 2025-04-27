package patterns.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import patterns.constants.CARD_HEADERS
import patterns.constants.CardType
import patterns.model.Card
import patterns.model.GiftCard
import patterns.model.PrepaidCard
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GiftCardCsvHandler {
    private val logger: Logger = LoggerFactory.getLogger(GiftCardCsvHandler::class.java)
    private val writeToCsv = AppConfig.writeToCsvFile
    private val giftCardFilePath = AppConfig.csvGiftCardFilePath

    fun writeCardsToFile(cards: List<Card>): Iterable<Card> {
        if (!writeToCsv) {
            // This would contain the logic to write to a database
            logger.info("writeToCsv is set to false. Skipping CSV write operation.")
            return cards
        }
        return csvWriter.writeCards(cards)
    }

    fun readCardsFromFile(): List<Card> {
        if (!writeToCsv) {
            // This would contain the logic to read from a database
            logger.info("writeToCsv is set to false. Skipping CSV read operation.")
            return emptyList()
        }
        return csvReader.readCards()
    }

    /**
     * Kotlin also had built in singleton objects. An example of this can be found
     * in the AppConfig file in the config package.
     * More information can be found here: https://kotlinlang.org/docs/object-declarations.html
     * In this case we are using the object to create an anonymous object
     * https://kotlinlang.org/docs/object-declarations.html#object-expressions
     * https://medium.com/@appdevinsights/object-keyword-in-kotlin-568cc7f29fc7
     */
    private val csvReader = object {
        fun readCards(): List<Card> {
            if (!File(giftCardFilePath).exists()) {
                logger.warn("CSV file does not exist. Returning an empty list.")
                return emptyList()
            }

            return try {
                val cards = mutableListOf<Card>()
                val lines = File(giftCardFilePath).readLines()

                if (lines.size > 1) {
                    for (i in 1 until lines.size) {
                        parseCardFromLine(lines[i])?.let { cards.add(it) }
                    }
                }
                logger.info("Successfully read ${cards.size} cards from CSV file.")
                cards
            } catch (e: Exception) {
                logger.error("Error reading from CSV file: ${e.message}", e)
                emptyList()
            }
        }

        private fun parseCardFromLine(line: String): Card? {
            try {
                val parts = line.split(",")
                if (parts.size < 8) return null

                val number = parts[0]
                val type = CardType.valueOf(parts[1])
                val amount = parts[2].toDoubleOrNull() ?: 0.0
                val createdDate = dateTimeUtils.parseDateTime(parts[3])
                val createdBy = parts[4]
                val updatedDate = dateTimeUtils.parseDateTime(parts[5])
                val updatedBy = parts[6]
                val extraInfo = parts[7]

                return createCard.createCardFromData(
                    type, number, amount, createdDate, createdBy,
                    updatedDate, updatedBy, extraInfo
                )
            } catch (e: Exception) {
                logger.error("Error parsing card data: ${e.message}", e)
                return null
            }
        }
    }

    private val csvWriter = object {
        fun writeCards(cards: List<Card>): Iterable<Card> {
            return try {
                val file = File(giftCardFilePath)
                val fileExists = file.exists() && file.length() > 0

                FileWriter(file, fileExists).use { fileWriter ->
                    fileWriter.buffered().use { writer ->
                        if (!fileExists) {
                            writer.write("$CARD_HEADERS\n")
                        }

                        cards.forEach { card ->
                            writer.write("${convertCardToLine(card)}\n")
                        }
                    }
                }
                logger.info("Successfully wrote ${cards.size} cards to CSV file.")
                cards
            } catch (e: Exception) {
                logger.error("Error writing to CSV file: ${e.message}", e)
                throw e
            }
        }

        private fun convertCardToLine(card: Card): String {
            val extraInfo = when (card) {
                is GiftCard -> card.originalPurchaseAmount.toString()
                is PrepaidCard -> card.expirationDate?.toString() ?: ""
            }

            return "${card.number},${card.type},${card.balance},${card.createdDate}," +
                    "${card.createdBy},${card.updatedDate},${card.updatedBy},$extraInfo"
        }
    }

    /**
     * This could be a case for implementing a Factory pattern.
     * https://www.baeldung.com/java-factory-pattern
     */

    private val createCard = object {
        fun createCardFromData(
            type: CardType,
            number: String,
            amount: Double,
            createdDate: LocalDateTime,
            createdBy: String,
            updatedDate: LocalDateTime,
            updatedBy: String,
            extraInfo: String
        ): Card {
            return when (type) {
                CardType.GIFT -> {
                    val originalAmount = extraInfo.toDoubleOrNull() ?: 0.0
                    GiftCard(
                        number = number,
                        balance = amount,
                        createdDate = createdDate,
                        createdBy = createdBy,
                        updatedDate = updatedDate,
                        updatedBy = updatedBy,
                        originalPurchaseAmount = originalAmount
                    )
                }
                CardType.PREPAID -> {
                    val expirationDate = if (extraInfo.isNotBlank())
                        dateTimeUtils.parseDateTime(extraInfo) else null
                    PrepaidCard(
                        number = number,
                        balance = amount,
                        createdDate = createdDate,
                        createdBy = createdBy,
                        updatedDate = updatedDate,
                        updatedBy = updatedBy,
                        expirationDate = expirationDate
                    )
                }
            }
        }
    }

    private val dateTimeUtils = object {
        fun parseDateTime(dateTimeStr: String): LocalDateTime {
            return try {
                val formatter = DateTimeFormatter.ISO_DATE_TIME
                LocalDateTime.parse(dateTimeStr, formatter)
            } catch (e: Exception) {
                logger.warn("Failed to parse date: $dateTimeStr. Using current time as fallback.")
                LocalDateTime.now()
            }
        }
    }
}