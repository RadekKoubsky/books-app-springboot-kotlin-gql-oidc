package com.rkoubsky.books.service.model

import java.time.OffsetDateTime
import java.util.*

data class Book(
    val id: UUID,
    val title: String,
    val isbn: String,
    val publishedYear: Int,
    val author: Author,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class CreateBookCommand(
    val title: String,
    val isbn: String,
    val publishedYear: Int,
    val authorId: UUID
)

data class UpdateBookCommand(
    val title: String?,
    val isbn: String?,
    val publishedYear: Int?,
    val authorId: UUID?
)

data class BookFilter(
    val title: String? = null,
    val isbn: String? = null,
    val publishedYear: Int? = null,
    val authorId: UUID? = null,
)

data class BookList(
    val books: List<Book>,
    val pageInfo: PageInfo
)

data class PageInfo(
    val hasNextPage: Boolean,
    val endCursor: String?
)
