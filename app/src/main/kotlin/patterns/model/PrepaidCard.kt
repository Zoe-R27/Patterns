package patterns.model

import patterns.constants.CardType
import java.time.LocalDateTime

// PrepaidCard data class - gets automatic copy function
data class PrepaidCard(
    override val number: String,
    override val balance: Double,
    override val createdDate: LocalDateTime = LocalDateTime.now(),
    override val createdBy: String,
    override val updatedDate: LocalDateTime = LocalDateTime.now(),
    override val updatedBy: String,
    val expirationDate: LocalDateTime? = null
) : Card(number, CardType.PREPAID, balance, createdDate, createdBy, updatedDate, updatedBy)