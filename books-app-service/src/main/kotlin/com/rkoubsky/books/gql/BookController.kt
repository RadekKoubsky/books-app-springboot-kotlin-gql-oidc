package com.rkoubsky.books.gql

import com.rkoubsky.books.service.AuthorService
import com.rkoubsky.books.service.BookService
import com.rkoubsky.books.service.model.Author
import com.rkoubsky.books.service.model.CreateBookCommand
import com.rkoubsky.books.service.model.UpdateBookCommand
import org.dataloader.DataLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import java.util.*
import java.util.concurrent.CompletableFuture

@Controller
class BookController(
    private val bookService: BookService,
    private val authorService: AuthorService,
    private val bookMapper: BookMapper,
    private val authorMapper: AuthorMapper
) {
    var logger: Logger = LoggerFactory.getLogger(BookController::class.java)

    @QueryMapping
    fun book(@Argument id: UUID): BookGQL? {
        return bookMapper.toGQL(bookService.findById(id))
    }

    @QueryMapping
    fun books(
        @Argument filter: BookFilterGQL?,
        @Argument cursor: String?,
        @Argument limit: Int
    ): BookListGQL {
        val domainFilter = bookMapper.mapFilter(filter)
        val bookPage = bookService.findAllPaginated(domainFilter, cursor, limit)
        return bookMapper.toBookListGQL(bookPage)
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
        return bookService.delete(listOf(id)) > 0
    }

    @SchemaMapping(typeName = "Book", field = "author")
    fun author(book: BookGQL, dataLoader: DataLoader<UUID, Author>): CompletableFuture<AuthorGQL> {
        logger.info("Batch resolving authors")

        return dataLoader.load(book.authorId).thenApply {
            authorMapper.toGQL(it)
        }
    }
}
