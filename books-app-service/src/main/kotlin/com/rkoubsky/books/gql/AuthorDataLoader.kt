package com.rkoubsky.books.gql

import com.rkoubsky.books.service.AuthorService
import com.rkoubsky.books.service.model.Author
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuthorDataLoader(
    private val authorService: AuthorService,
) {
    fun load(authorIds: Set<UUID>): Map<UUID, Author> {
        return authorService.findByIds(authorIds).associateBy { it.id }
    }
}