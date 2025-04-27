package patterns.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import patterns.constants.CARD_PREFIX
import patterns.constants.CardType
import patterns.constants.UPDATED_BY
import patterns.util.TestUtil
import java.time.LocalDateTime

class GenerateCardServiceTest : StringSpec({

    val mockCardRepository = mockk<CsvCardRepositoryAdapter>()
    val mockSequenceRepository = mockk<CsvSequenceRepositoryAdapter>()

    // Get a singleton instance of the GenerateCardService and set a way to get to its method
    val service = spyk(GenerateCardService.getInstance())

    beforeTest {
        // Clear all mocks before each test to reset their state
        clearAllMocks()
        // Inject mocks into the service
        service.apply {
            this::class.java.getDeclaredField("cardRepository").apply {
                isAccessible = true
                set(service, mockCardRepository)
            }
            this::class.java.getDeclaredField("sequenceRepository").apply {
                isAccessible = true
                set(service, mockSequenceRepository)
            }
        }
    }

    "generateCards should generate giftcards successfully" {
        // Arrange
        val startSequence = 1000L
        val cardAmount = 5L
        val cardBalance = 100.0
        val cardType = CardType.GIFT
        val generatedCards = TestUtil.createGiftCards(cardAmount, cardBalance, "TEST")

        every { mockSequenceRepository.getSequence() } returns startSequence
        every { mockCardRepository.saveAll(any()) } returns generatedCards
        every { mockSequenceRepository.updateSequence(any(), any()) } returns TestUtil.getSequence(1005L)

        // Act
        val result = service.generateCards(cardAmount, cardBalance, cardType)

        // Assert
        result shouldHaveSize cardAmount.toInt()
        result.forEachIndexed { index, card ->
            card.number shouldBe "${CARD_PREFIX}${String.format("%03d", cardType.typePrefix)}${
                String.format(
                    "%09d",
                    startSequence + index
                )
            }"
            card.balance shouldBe cardBalance
        }
        verify { mockSequenceRepository.getSequence() }
        verify { mockCardRepository.saveAll(result) }
        verify { mockSequenceRepository.updateSequence(startSequence + cardAmount, UPDATED_BY) }
    }

    "generateCards should generate prepaid cards successfully" {
        // Arrange
        val startSequence = 2000L
        val cardAmount = 6L
        val cardBalance = 400.0
        val cardType = CardType.GIFT
        val expirationDate = LocalDateTime.of(2025, 4, 11, 11, 0)
        val generatedCards = TestUtil.createPrepaidCards(cardAmount, cardBalance, "TEST", expirationDate)

        every { mockSequenceRepository.getSequence() } returns startSequence
        every { mockCardRepository.saveAll(any()) } returns generatedCards
        every { mockSequenceRepository.updateSequence(any(), any()) } returns TestUtil.getSequence(2006L)

        // Act
        val result = service.generateCards(cardAmount, cardBalance, cardType)

        // Assert
        result shouldHaveSize cardAmount.toInt()
        result.forEachIndexed { index, card ->
            card.number shouldBe "${CARD_PREFIX}${String.format("%03d", cardType.typePrefix)}${
                String.format(
                    "%09d",
                    startSequence + index
                )
            }"
            card.balance shouldBe cardBalance
        }
        verify { mockSequenceRepository.getSequence() }
        verify { mockCardRepository.saveAll(result) }
        verify { mockSequenceRepository.updateSequence(startSequence + cardAmount, UPDATED_BY) }
    }

    /**
     * Currently this program doesn't do anything other than log errors if we can't save the cards.
     * In an actual service that is deployed for clients to use with the functionality to generate cards as an api or kafka service,
     * we should have alerting and logging in place to notify us of this failure in the database.
     * The team will then have to decide how to handle these types of errors and have a plan in place to on what to do.
     */
    "generateCards should handle failure to save cards in the database" {
        // Arrange
        val startSequence = 2000L
        val cardAmount = 3L
        val cardBalance = 50.0
        val cardType = CardType.PREPAID

        every { mockSequenceRepository.getSequence() } returns startSequence
        every { mockCardRepository.saveAll(any()) } throws Exception("Save failed")

        // Act
        val result = service.generateCards(cardAmount, cardBalance, cardType)

        // Assert
        result shouldHaveSize 0
        verify { mockCardRepository.saveAll(any()) }
        verify(exactly = 0) { mockSequenceRepository.updateSequence(any(), any()) }
    }

    "generateCards should handle failure to get the sequence from the database" {
        // Arrange
        val cardAmount = 3L
        val cardBalance = 50.0
        val cardType = CardType.PREPAID

        every { mockSequenceRepository.getSequence() } throws Exception("Get sequence failed")

        // Act
        val result = service.generateCards(cardAmount, cardBalance, cardType)

        // Assert
        result shouldHaveSize 0
        verify(exactly = 0) { mockCardRepository.saveAll(any()) }
        verify(exactly = 0) { mockSequenceRepository.updateSequence(any(), any()) }
    }
})