package com.rkoubsky.books.gql

import com.rkoubsky.books.service.Book
import com.rkoubsky.books.service.BookFilter
import org.springframework.stereotype.Component

@Component
class BookMapper(private val authorMapper: AuthorMapper) {

    fun toGQL(book: Book): BookGQL {
        return BookGQL(
            id = book.id,
            title = book.title,
            isbn = book.isbn,
            publishedYear = book.publishedYear,
            author = authorMapper.toGQL(book.author),
            createdAt = book.createdAt,
            updatedAt = book.updatedAt
        )
    }

    fun mapFilter(filterGQL: BookFilterGQL?): BookFilter? {
        return filterGQL?.let {
            BookFilter(
                title = it.title,
                isbn = it.isbn,
                publishedYear = it.publishedYear,
                authorId = it.authorId
            )
        }
    }
}
