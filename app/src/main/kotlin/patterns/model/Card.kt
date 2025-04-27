package patterns.model

import patterns.constants.CardType
import java.time.LocalDateTime

// Base class using a sealed class hierarchy
sealed class Card(
    open val number: String,
    open val type: CardType,
    open val balance: Double,
    open val createdDate: LocalDateTime,
    open val createdBy: String,
    open val updatedDate: LocalDateTime,
    open val updatedBy: String
) {
    // Common methods can go here
    override fun toString(): String {
        return "Card(number=$number, type=$type, balance=$balance, createdDate=$createdDate, " +
                "createdBy=$createdBy, updatedDate=$updatedDate, updatedBy=$updatedBy)"
    }
}