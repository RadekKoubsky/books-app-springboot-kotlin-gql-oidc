package com.rkoubsky.books.gql

import com.rkoubsky.books.service.Author
import org.springframework.stereotype.Component

@Component
class AuthorMapper {

    fun toGQL(author: Author): AuthorGQL {
        return AuthorGQL(
            id = author.id,
            name = author.name,
            surname = author.surname,
            bio = author.bio,
            createdAt = author.createdAt,
            updatedAt = author.updatedAt
        )
    }
}
