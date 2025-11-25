package com.rkoubsky.books.gql

import com.rkoubsky.books.service.*
import com.rkoubsky.books.service.model.CreateBookCommand
import com.rkoubsky.books.service.model.UpdateBookCommand
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
    fun books(@Argument filter: BookFilterGQL?): List<BookGQL> {
        val domainFilter = bookMapper.mapFilter(filter)
        return bookService.findAll(domainFilter).map { bookMapper.toGQL(it) }
    }

    @MutationMapping
    fun createBook(@Argument input: CreateBookInputGQL): BookGQL {
        val command = CreateBookCommand(
            title = input.title,
            isbn = input.isbn,
            publishedYear = input.publishedYear,
            authorId = input.authorId
        )
        return bookMapper.toGQL(bookService.create(command))
    }

    @MutationMapping
    fun updateBook(@Argument id: UUID, @Argument input: UpdateBookInputGQL): BookGQL {
        val command = UpdateBookCommand(
            title = input.title,
            isbn = input.isbn,
            publishedYear = input.publishedYear,
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
