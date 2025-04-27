package patterns.service

import patterns.model.SequenceRecord
import patterns.repository.SequenceRepository

class CsvSequenceRepositoryAdapter(private val sequenceCsvHandler: SequenceCsvHandler) : SequenceRepository {
    override fun getSequence(): Long? {
        return sequenceCsvHandler.getStartSequence()
    }

    override fun updateSequence(sequence: Long, s: String): SequenceRecord {
        return sequenceCsvHandler.updateStartSequence(sequence, "System")
    }

}