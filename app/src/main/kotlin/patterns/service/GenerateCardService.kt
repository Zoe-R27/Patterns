package patterns.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import patterns.constants.CardType
import patterns.constants.CARD_PREFIX
import patterns.constants.UPDATED_BY
import patterns.model.Card
import patterns.model.GiftCard
import patterns.model.PrepaidCard
import patterns.model.SequenceRecord
import java.time.LocalDateTime

/**
 *  The Companion Object at the bottom is used to make this class a Singleton
 *  This allows me to easily use the same instance of this class across the application
 *  which helps me control card generation in sequence.
 *  Examples of using object declarations in Kotlin can be found in the AppConfig and GiftCardCsvHandler
 */
class GenerateCardService private constructor() {

    private val logger: Logger = LoggerFactory.getLogger(GenerateCardService::class.java)

    // Creating the adapter used in the adapter pattern to save cards to the csv file instead of a database
    private val giftCardCsvHandler = GiftCardCsvHandler()
    private val cardRepository = CsvCardRepositoryAdapter(giftCardCsvHandler)

    private val sequenceCsvHandler = SequenceCsvHandler()
    private val sequenceRepository = CsvSequenceRepositoryAdapter(sequenceCsvHandler)

    /**
     * Generates a specified number of cards with the given amount and type.
     * Each card receives a unique sequence number and is persisted to storage.
     *
     * @param giftCardAmount The number of gift cards to generate
     * @param giftCardBalance The balance to assign to each card in the batch
     * @param giftCardType The type of gift card to create
     * @return List of generated Card objects
     */
    fun generateCards(
        cardAmount: Long,
        cardBalance: Double,
        cardType: CardType,
        expirationDate: LocalDateTime? = null
    ): List<Card> {
        val generatedListOfCards = mutableListOf<Card>()

        // Wrap in a try-catch statement to handle any exceptions that may occur
        // Kotlin also has a built-in runCatching function that can be used to handle exceptions amongst other things
        try {
            // Call to get the start sequence
            val startSequence = getSequence()
            // Calculate the end sequence
            val endSequence = startSequence + cardAmount - 1

            // Generate the cards in the range of startSequence to endSequence
            for (sequenceValue in startSequence..endSequence) {
                val cardNumber = generateCardNumber(sequenceValue, cardType.typePrefix)
                val card = when (cardType) {
                    CardType.GIFT -> GiftCard(
                        number = cardNumber,
                        balance = cardBalance,
                        createdDate = LocalDateTime.now(),
                        createdBy = UPDATED_BY,
                        updatedDate = LocalDateTime.now(),
                        updatedBy = UPDATED_BY,
                        originalPurchaseAmount = cardBalance
                    )
                    CardType.PREPAID -> PrepaidCard(
                        number = cardNumber,
                        balance = cardBalance,
                        createdDate = LocalDateTime.now(),
                        createdBy = UPDATED_BY,
                        updatedDate = LocalDateTime.now(),
                        updatedBy = UPDATED_BY,
                        expirationDate = expirationDate
                    )
                }
                generatedListOfCards.add(card)
            }
            logger.info("Generating $cardAmount ${cardType.name.lowercase()} cards.")

            // Automatically save the cards to the Database and Update the Sequence in the Database
            saveCards(generatedListOfCards)
            updateSequenceAfterGeneration(endSequence)
            return generatedListOfCards
        } catch (ex: Exception) {
            logger.error("Error generating cards: ${ex.message}", ex)
            return emptyList()
        }
    }

    /**
     * Creates a standardized card number using the sequence and card type.
     * Format: [CARD_PREFIX][3-digit type prefix][9-digit sequence]
     *
     * @param sequence The unique sequence for the card.
     * @param cardTypePrefix The 3 digit long code indicating card type
     * @return String representation of the formatted card number
     */
    private fun generateCardNumber(sequence: Long, cardTypePrefix: Int): String {
        val cardNumber = CARD_PREFIX
            .plus(String.format("%03d", cardTypePrefix))
            .plus(String.format("%09d", sequence))

        logger.debug("Generated card number: $cardNumber"
        )
        return cardNumber
    }

    /**
     * Retrieves the next available sequence number from the sequence repository.
     * Throws an exception if no sequence is available.
     *
     * @return Long value of the next available sequence
     * @throws IllegalStateException if no sequence is found
     * @throws Exception if there's an error retrieving the sequence
     */
    private fun getSequence(): Long {
        return try {
            val sequence = sequenceRepository.getSequence()
            if(sequence == null) {
                logger.error("No sequence found in the sequence file.")
                throw IllegalStateException("No sequence found in the sequence file.")
            }
            logger.info("Retrieved sequence: $sequence")
            sequence
        } catch (ex: Exception) {
            logger.error("Error retrieving sequence: ${ex.message}", ex)
            throw ex
        }
    }

    /**
     * Persists a list of cards to the underlying storage via the card repository.
     *
     * @param cards List of Card objects to save
     */
    private fun saveCards(cards: List<Card>) {
        // runCatching is like Java's try-catch but more idiomatic in Kotlin
        runCatching {
            cardRepository.saveAll(cards)
        }.onFailure {
            logger.error("Error saving cards: ${it.message}", it)
            throw it
        }
    }

    /**
     * Updates the start sequence in the sequence file to the end sequence + 1
     * to prepare for the next batch of card generation
     *
     * @param endSequence The last sequence number used in card generation
     * @return Boolean indicating success or failure
     */
    private fun updateSequenceAfterGeneration(endSequence: Long): SequenceRecord {
        try {
            // Add 1 to start from the next number after the last used sequence
            val nextStartSequence = endSequence + 1

            return sequenceRepository.updateSequence(nextStartSequence, UPDATED_BY)

        } catch (ex: Exception) {
            logger.error("Error updating sequence: ${ex.message}", ex)
            throw ex
        }
    }

    /**
     * In this case I use the companion object to implement the Singleton pattern
     * The Companion Object is a singleton object that is associated with the class
     * It is used to hold static members and methods
     * More can be found here:
     * https://medium.com/@riztech.dev/understanding-companion-objects-in-kotlin-a93f1a5880a7
     * https://kotlinlang.org/docs/object-declarations.html#companion-objects
     */
    companion object {

        @Volatile private var instance: GenerateCardService? = null
        fun getInstance(): GenerateCardService {
            return instance ?: synchronized(this) {
                instance ?: GenerateCardService().also { instance = it }
            }
        }
    }
}