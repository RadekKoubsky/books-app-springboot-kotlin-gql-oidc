package com.rkoubsky.books.gql

import com.rkoubsky.books.service.*
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class BookController(
    private val bookService: BookService,
    private val bookMapper: BookMapper,
    private val authorMapper: AuthorMapper
) {

    @QueryMapping
    fun book(@Argument id: UUID): BookGQL? {
        return bookMapper.toGQL(bookService.findById(id))
    }

    @QueryMapping
    fun books(): List<BookGQL> {
        return bookService.findAll().map { bookMapper.toGQL(it) }
    }

    @QueryMapping
    fun booksByTitle(@Argument title: String): List<BookGQL> {
        return bookService.findByTitle(title).map { bookMapper.toGQL(it) }
    }

    @QueryMapping
    fun booksByIsbn(@Argument isbn: String): BookGQL? {
        return bookService.findByIsbn(isbn)?.let { bookMapper.toGQL(it) }
    }

    @QueryMapping
    fun booksByYear(@Argument year: Int): List<BookGQL> {
        return bookService.findByYear(year).map { bookMapper.toGQL(it) }
    }

    @MutationMapping
    fun createBook(@Argument input: CreateBookInputGQL): BookGQL {
        val command = CreateBookCommand(
            title = input.title,
            isbn = input.isbn,
            year = input.year,
            authorId = input.authorId
        )
        return bookMapper.toGQL(bookService.create(command))
    }

    @MutationMapping
    fun updateBook(@Argument id: UUID, @Argument input: UpdateBookInputGQL): BookGQL {
        val command = UpdateBookCommand(
            title = input.title,
            isbn = input.isbn,
            year = input.year,
            authorId = input.authorId
        )
        return bookMapper.toGQL(bookService.update(id, command))
    }

    @MutationMapping
    fun deleteBook(@Argument id: UUID): Boolean {
        return bookService.delete(id)
    }

    @SchemaMapping(typeName = "Book", field = "author")
    fun author(book: BookGQL): AuthorGQL {
        return book.author
    }
}
