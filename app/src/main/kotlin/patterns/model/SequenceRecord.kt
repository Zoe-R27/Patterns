package patterns.model

import java.time.LocalDateTime

data class SequenceRecord(
    val startSequence: Long,
    val endSequence: Long,
    val sequenceName: String,
    val createdBy: String,
    val createdDate: LocalDateTime,
    val modifiedBy: String,
    val modifiedDate: LocalDateTime
)