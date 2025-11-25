package com.rkoubsky.books.gql

import java.time.OffsetDateTime
import java.util.*

data class BookGQL(
    val id: UUID,
    val title: String,
    val isbn: String,
    val publishedYear: Int,
    val authorId: UUID,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class CreateBookInputGQL(
    val title: String,
    val isbn: String,
    val publishedYear: Int,
    val authorId: UUID
)

data class UpdateBookInputGQL(
    val title: String?,
    val isbn: String?,
    val publishedYear: Int?,
    val authorId: UUID?
)

data class BookFilterGQL(
    val title: String?,
    val isbn: String?,
    val publishedYear: Int?,
    val authorId: UUID?
)

data class BookListGQL(
    val books: List<BookGQL>,
    val pageInfo: PageInfoGQL
)

data class PageInfoGQL(
    val hasNextPage: Boolean,
    val endCursor: String?
)
