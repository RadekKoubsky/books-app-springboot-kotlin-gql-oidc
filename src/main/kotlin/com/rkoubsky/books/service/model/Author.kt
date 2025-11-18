package com.rkoubsky.books.service.model

import java.time.OffsetDateTime
import java.util.*

data class Author(
    val id: UUID,
    val name: String,
    val surname: String,
    val bio: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class CreateAuthorCommand(
    val name: String,
    val surname: String,
    val bio: String?
)

data class UpdateAuthorCommand(
    val name: String?,
    val surname: String?,
    val bio: String?
)

data class AuthorFilter(
    val name: String?,
    val surname: String?
)
