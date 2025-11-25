package com.rkoubsky.books.service

import java.time.OffsetDateTime
import java.util.*

data class Book(
    val id: UUID,
    val title: String,
    val isbn: String,
    val year: Int,
    val author: Author,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class CreateBookCommand(
    val title: String,
    val isbn: String,
    val year: Int,
    val authorId: UUID
)

data class UpdateBookCommand(
    val title: String?,
    val isbn: String?,
    val year: Int?,
    val authorId: UUID?
)
