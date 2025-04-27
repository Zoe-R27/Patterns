package patterns.constants

/**
 * It is good practice to have constants for repeated code or configurable code. This way if someone ever needs to change
 * say the CARD_PREFIX, they don't have to find all the locations where "55" is used. Instead they can just change it here.
 */
const val CARD_PREFIX = "55"
const val UPDATED_BY = "System"
const val SEQUENCE_HEADERS = "SEQUENCE_START,SEQUENCE_END,SEQUENCE_NAME,CREATED_BY,CREATED_DATE,MODIFIED_BY,MODIFIED_DATE"
const val CARD_HEADERS = "Number,Type,Amount,CreatedDate,CreatedBy,UpdatedDate,UpdatedBy,ExtraInfo"