package patterns.service

import patterns.model.Card
import patterns.repository.CardRepository

/**
 * Normally the cards would be saved in a database where we can pull the information when we need it
 * In this case I do not want it to go to a database, but to a CSV file to make it easier to see the output of the program.
 * This is the adapter that will be used to save the cards to a CSV file but still make it appear as though the cards
 * are going to the database
**/

class CsvCardRepositoryAdapter(private val giftCardCsvHandler: GiftCardCsvHandler) : CardRepository {
    override fun saveAll(cards: List<Card>): Iterable<Card> {
        return giftCardCsvHandler.writeCardsToFile(cards)
    }

    override fun save(card: Card): Iterable<Card> {
        return giftCardCsvHandler.writeCardsToFile(listOf(card))
    }

    override fun getAll(): List<Card> {
        return giftCardCsvHandler.readCardsFromFile()
    }
}