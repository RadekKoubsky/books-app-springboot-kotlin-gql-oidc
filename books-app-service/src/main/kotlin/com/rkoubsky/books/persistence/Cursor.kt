package com.rkoubsky.books.persistence

import java.time.OffsetDateTime
import java.util.*

data class Cursor(
    val createdAt: OffsetDateTime
) {
    fun encode(): String = Base64.getEncoder().encodeToString(createdAt.toString().toByteArray())

    companion object {
        fun decode(cursor: String): OffsetDateTime = OffsetDateTime.parse(String(Base64.getDecoder().decode(cursor)))
    }
}
