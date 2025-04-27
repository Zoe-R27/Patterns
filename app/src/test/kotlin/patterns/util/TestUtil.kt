package patterns.util

import patterns.constants.CardType
import patterns.constants.CARD_PREFIX
import patterns.constants.UPDATED_BY
import patterns.model.GiftCard
import patterns.model.PrepaidCard
import patterns.model.SequenceRecord
import java.time.LocalDateTime

class TestUtil {
    companion object {

        /**
         * Creates a list of GiftCard objects using some aspects of the Prototype pattern.
         *
         * This functions creates a default/prototype card with default values.
         * This prototype is then copied using Kotlin's copy() function
         * to generate new GiftCards. Each copy gets a different number and updateDate,
         * while still keeping the other fields from the prototype.
         *
         * The difference between a traditional prototype pattern and this implementation
         * is that Kotlin's data classes provide a built-in copy() function that does a shallow
         * copy instead of a deep copy that is usually expected by the prototype pattern.
         *
         * Shallow Copy: A new object is created, but some of the fields (objects) are references to the same instances
         * Deep copy: A new copy of the object is created for each field
         * You can find a more detailed explanation of the difference here:
         * https://dev.to/hkp22/javascript-shallow-copy-vs-deep-copy-examples-and-best-practices-3k0a
         *
         * @param amount The number of GiftCard objects to create.
         * @param cardBalance The balance to assign to each GiftCard.
         * @param createdBy The user who created the GiftCards.
         * @return A list of GiftCard objects with unique numbers and the specified balance.
         */
        fun createGiftCards(amount: Long, cardBalance: Double, createdBy: String): List<GiftCard> {
            val prototypeCard = GiftCard(
                number = "55111000000001",
                balance = cardBalance,
                createdBy = createdBy,
                updatedBy = createdBy,
                originalPurchaseAmount = cardBalance
            )

            return (1..amount).map { index ->
                prototypeCard.copy(
                    number = CARD_PREFIX
                        .plus(String.format("%03d", CardType.GIFT.typePrefix))
                        .plus(String.format("%09d", index)),
                    updatedDate = LocalDateTime.now()
                )
            }
        }

        fun createPrepaidCards(amount: Long, cardBalance: Double, createdBy: String, expirationDate: LocalDateTime?): List<PrepaidCard> {
            val prototypeCard = PrepaidCard(
                number = "55111000000001",
                balance = cardBalance,
                createdBy = createdBy,
                updatedBy = createdBy,
                expirationDate = expirationDate
            )

            return (1..amount).map { index ->
                prototypeCard.copy(
                    number = CARD_PREFIX
                        .plus(String.format("%03d", CardType.GIFT.typePrefix))
                        .plus(String.format("%09d", index)),
                    updatedDate = LocalDateTime.now()
                )
            }
        }

        fun getSequence(sequenceNumber: Long): SequenceRecord {
            return SequenceRecord(
                startSequence = sequenceNumber,
                endSequence = 9999999,
                sequenceName = "Test Sequence",
                createdBy = "TEST",
                createdDate = LocalDateTime.now(),
                modifiedBy = "TEST",
                modifiedDate = LocalDateTime.now()
            )
        }
    }
}