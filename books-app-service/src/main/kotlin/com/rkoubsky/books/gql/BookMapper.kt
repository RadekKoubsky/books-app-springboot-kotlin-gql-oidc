package com.rkoubsky.books.gql

import com.rkoubsky.books.service.model.Book
import com.rkoubsky.books.service.model.BookFilter
import com.rkoubsky.books.service.model.BookList
import org.springframework.stereotype.Component

@Component
class BookMapper {

    fun toGQL(book: Book): BookGQL {
        return BookGQL(
            id = book.id,
            title = book.title,
            isbn = book.isbn,
            publishedYear = book.publishedYear,
            authorId = book.authorId,
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

    fun toBookListGQL(bookList: BookList): BookListGQL {
        return BookListGQL(
            books = bookList.books.map { toGQL(it) },
            pageInfo = PageInfoGQL(
                hasNextPage = bookList.pageInfo.hasNextPage,
                endCursor = bookList.pageInfo.endCursor
            )
        )
    }
}
