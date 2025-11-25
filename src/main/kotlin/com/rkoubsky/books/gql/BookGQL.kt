package com.rkoubsky.books.gql

import java.time.OffsetDateTime
import java.util.*

data class BookGQL(
    val id: UUID,
    val title: String,
    val isbn: String,
    val year: Int,
    val author: AuthorGQL,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class CreateBookInputGQL(
    val title: String,
    val isbn: String,
    val year: Int,
    val authorId: UUID
)

data class UpdateBookInputGQL(
    val title: String?,
    val isbn: String?,
    val year: Int?,
    val authorId: UUID?
)
