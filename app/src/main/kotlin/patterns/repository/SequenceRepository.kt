package patterns.repository

import patterns.model.SequenceRecord

interface SequenceRepository {
    fun getSequence(): Long?
    fun updateSequence(sequence: Long, source: String): SequenceRecord
}