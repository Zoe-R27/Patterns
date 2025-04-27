package patterns.service

import org.slf4j.LoggerFactory
import patterns.model.SequenceRecord
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import patterns.constants.SEQUENCE_HEADERS

class SequenceCsvHandler {
    private val logger = LoggerFactory.getLogger(SequenceCsvHandler::class.java)
    private val sequenceFilePath = AppConfig.csvSequenceFilePath


    fun getStartSequence(): Long? {
        val record = readSequenceRecord()
        return record?.startSequence
    }

    fun updateStartSequence(newStartSequence: Long, modifiedBy: String): SequenceRecord {
        val record = readSequenceRecord() ?: throw Exception("No sequence record found")

        val updatedRecord = record.copy(
            startSequence = newStartSequence,
            modifiedBy = modifiedBy,
            modifiedDate = LocalDateTime.now()
        )

        return writeSequenceRecord(updatedRecord)
    }

    private fun readSequenceRecord(): SequenceRecord? {
        val lines = getLinesFromFile() ?: return null

        try {
            // Assuming the first line is the header and the second line contains the data
            val dataLine = lines[1]
            val parts = dataLine.split(",").map { it.trim() }

            return SequenceRecord(
                startSequence = parts[0].toLong(),
                endSequence = parts[1].toLong(),
                sequenceName = parts[2],
                createdBy = parts[3],
                createdDate = parseDateTime(parts[4]),
                modifiedBy = parts[5],
                modifiedDate = parseDateTime(parts[6])
            )
        } catch (e: Exception) {
            logger.error("Error parsing sequence data: ${e.message}", e)
            return null
        }
    }

    private fun writeSequenceRecord(record: SequenceRecord): SequenceRecord {
        return try {
            val file = File(sequenceFilePath)
            val fileExists = file.exists()

            val content = StringBuilder()

            if (!fileExists) {
                content.append("$SEQUENCE_HEADERS\n")
            } else {
                // Keep the header from the existing file
                val lines = getLinesFromFile()
                if (lines != null && lines.isNotEmpty()) {
                    content.append("${lines[0]}\n")
                } else {
                    content.append("$SEQUENCE_HEADERS\n")
                }
            }

            // Add the record data
            content.append("${record.startSequence},${record.endSequence},${record.sequenceName}," +
                    "${record.createdBy},${formatDateTime(record.createdDate)}," +
                    "${record.modifiedBy},${formatDateTime(record.modifiedDate)}")

            file.writeText(content.toString())
            logger.info("Successfully updated sequence record")
            record
        } catch (e: Exception) {
            logger.error("Error writing sequence file: ${e.message}", e)
            throw e
        }
    }

    private fun getLinesFromFile(): List<String>? {
        val file = File(sequenceFilePath)
        if (!file.exists()) {
            logger.warn("Sequence file does not exist at path: $sequenceFilePath")
            return null
        }

        val lines = file.readLines()
        if (lines.size < 2) {
            logger.warn("Sequence file is empty or does not contain valid data.")
            return null
        }
        return lines
    }

    fun parseDateTime(dateTimeStr: String): LocalDateTime {
        return try {
            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            logger.warn("Failed to parse date: $dateTimeStr. Using current time as fallback.")
            LocalDateTime.now()
        }
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    }
}