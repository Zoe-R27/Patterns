package patterns.model

import patterns.constants.CardType
import java.time.LocalDateTime

// GiftCard data class - note the "copy" function is automatically provided
data class GiftCard(
    override val number: String,
    override val balance: Double,
    override val createdDate: LocalDateTime = LocalDateTime.now(),
    override val createdBy: String,
    override val updatedDate: LocalDateTime = LocalDateTime.now(),
    override val updatedBy: String,
    val originalPurchaseAmount: Double
) : Card(number, CardType.GIFT, balance, createdDate, createdBy, updatedDate, updatedBy)