# Prototype Pattern in Giftcard Generation

## Overview of the Prototype Pattern

The Prototype pattern is a design pattern that allows copying existing objects without making code dependent on their classes. 
In this project, the prototype pattern is used to simplify the creation of test objects by creating a base object (prototype) and then making copies with modifications as needed.
It is not necessary for this test setup, but it is an example of how the pattern can be used in Kotlin.

## Why the Prototype Pattern is Being Used

In the test suite, multiple similar card objects that differ only in a few properties are needed. The prototype pattern helps:

1. **Reduce Repetitive Code**: Create multiple similar objects without duplicate initialization code
2. **Simplify Test Setup**: Focus on just the properties that change between test objects
3. **Maintain Consistency**: Ensure common properties remain consistent across test objects

## Implementation in the Project

The prototype pattern is implemented in the `TestUtil.kt` file to generate test cards:

### Creating Test Cards with the Prototype Pattern

```kotlin
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
```

In this implementation `TestUtil.kt`:

1. A prototype `GiftCard` is created with default values
2. Kotlin's `copy()` function is used to create variations of this card
3. Each copy modifies only the card number and update date
4. A list of these cloned objects is returned

A similar method exists for creating `PrepaidCard` objects.

## Shallow Copy vs. Deep Copy

### Key Differences

- **Shallow Copy**: Copies the object's primitive values and references. If the original contains references to other objects, the copy will reference the same objects.
- **Deep Copy**: Creates new instances of all referenced objects recursively.

### Impact on Prototype Pattern

Kotlin's `.copy()` method creates a shallow copy. This is sufficient when:
- Objects contain only primitives and immutable objects
- No referenced objects need independent modification

However, if a `GiftCard` contained mutable objects (like a list of transactions), modifying these in one card would affect all cards sharing that reference.

In a traditional prototype pattern, deep copying is often needed to ensure complete independence of the cloned objects.
However, for this testing scenario, shallow copying is sufficient because:

1. The card objects primarily contain primitive types and immutable objects
2. Nested objects aren't modified after copying
3. Using `copy()` is more concise and performant

## Without the Prototype Pattern

Without the prototype pattern, the test code would look like this where we would create individual card objects:

```kotlin
fun createGiftCardsWithoutPrototype(amount: Long, cardBalance: Double, createdBy: String): List<GiftCard> {
    val cards = mutableListOf<GiftCard>()

    for (index in 1..amount) {
        val cardNumber = CARD_PREFIX + String.format("%03d", CardType.GIFT.typePrefix) + String.format("%09d", index)

        cards.add(GiftCard(
            number = cardNumber,
            balance = cardBalance,
            createdBy = createdBy,
            updatedBy = createdBy,
            originalPurchaseAmount = cardBalance,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        ))
    }

    return cards
}
```
Or if I wanted even more control and visibility over the card properties, I would have to create a new object for each card:

```kotlin
val card1 = PrepaidCard("1234567890123456",200.0, "Prepaid", "12/25"...)
val card2 = PrepaidCard("1234567890123457",400.0, "Prepaid", "12/25"...)
val card3 = GiftCard("1234567890123458",500.0, "GiftCard", "12/25"...)
val card4 = GiftCard("1234567890123459",600.0, "GiftCard", "12/25"...)
....
```

This could be tedious and error-prone, especially with many similar objects. The prototype pattern simplifies this by allowing us to create a base object and clone it with modifications.

## When the Prototype Pattern is Really Needed

While this implementation is convenient, it's not strictly necessary. The true prototype pattern becomes truly essential when:

1. **Complex Object Creation**: When objects require extensive setup, database access, or other resource-intensive operations
2. **Hidden Implementation**: When copying objects is needed without knowing their concrete classes
3. **Deep Object Hierarchies**: When dealing with complex nested object structures that need independent copies

### Real-World Example:

Consider a scenario with a complex card hierarchy with extensive validation rules, attached accounts, transaction history, and security features:

```kotlin
class EnterpriseCard(
    number: String,
    balance: Double,
    val securityFeatures: MutableList<SecurityFeature>,
    val transactionHistory: MutableList<Transaction>,
    val linkedAccounts: MutableList<Account>,
    val permissions: MutableMap<String, AccessLevel>,
    // Many more properties...
) {
    // Deep cloning would be necessary here
    fun clone(): EnterpriseCard {
        // Complex deep-copy logic required
    }
}
```

In this case, the prototype pattern with proper deep copying would be essential to avoid shared mutable state between clones.

## Benefits of Using copy() in Testing

For testing needs, Kotlin's `copy()` function provides several advantages:

1. **Simplicity**: No custom clone implementation needed
2. **Performance**: Shallow copies are faster to create and use less memory
3. **Expressiveness**: Makes test setup code clear and concise
4. **Type Safety**: The compiler ensures only existing properties are modified
5. **Immutability**: Works well with Kotlin's immutable data classes

## Conclusion

The prototype pattern as implemented in the test utilities demonstrates a pragmatic use of Kotlin's features to simplify test setup. 
While not necessary for relatively simple objects, it shows how the pattern can reduce code duplication and improve maintainability.

The pattern becomes more valuable as object complexity increases. For the current needs, Kotlin's built-in `copy()` 
function provides an simple way to implement the pattern without the complexity of deep cloning logic that would be required in more complex scenarios.
