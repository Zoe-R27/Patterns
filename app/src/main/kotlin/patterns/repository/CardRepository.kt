package patterns.repository

import patterns.model.Card

/**
 * Repository interface for Card operations.
 * If we are using the Micronaut Framework, we would include the @JdbcRepository or @Repository annotation
 * and extends the CrudRepository interface which provides already implemented basic CRUD operations.
 */
interface CardRepository {
    fun saveAll(cards: List<Card>): Iterable<Card>
    fun save(card: Card): Iterable<Card>
    fun getAll(): List<Card>
}