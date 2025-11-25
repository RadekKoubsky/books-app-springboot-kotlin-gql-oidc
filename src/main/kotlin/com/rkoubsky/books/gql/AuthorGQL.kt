package com.rkoubsky.books.gql

import java.time.OffsetDateTime
import java.util.*

data class AuthorGQL(
    val id: UUID,
    val name: String,
    val surname: String,
    val bio: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class CreateAuthorInputGQL(
    val name: String,
    val surname: String,
    val bio: String?
)

data class UpdateAuthorInputGQL(
    val name: String?,
    val surname: String?,
    val bio: String?
)

data class AuthorFilterGQL(
    val name: String?,
    val surname: String?
)
