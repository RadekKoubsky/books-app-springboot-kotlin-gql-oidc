package com.rkoubsky.books.service

import com.rkoubsky.books.exception.AuthorNotFoundException
import com.rkoubsky.books.exception.BookNotFoundException
import com.rkoubsky.books.exception.DuplicateIsbnException
import com.rkoubsky.books.exception.InvalidInputException
import com.rkoubsky.books.persistence.AuthorPersistence
import com.rkoubsky.books.persistence.BookPersistence
import com.rkoubsky.books.service.model.Book
import com.rkoubsky.books.service.model.BookFilter
import com.rkoubsky.books.service.model.CreateBookCommand
import com.rkoubsky.books.service.model.UpdateBookCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class BookService(
    private val bookPersistence: BookPersistence,
    private val authorPersistence: AuthorPersistence
) {

    fun findById(id: UUID): Book {
        return bookPersistence.findById(id) ?: throw BookNotFoundException(id)
    }

    fun findAll(filter: BookFilter? = null): List<Book> {
        return bookPersistence.findAll(filter)
    }

    fun create(command: CreateBookCommand): Book {
        validateBookCommand(command.title, command.isbn, command.publishedYear)

        authorPersistence.findById(command.authorId) ?: throw AuthorNotFoundException(command.authorId)

        bookPersistence.findByIsbn(command.isbn)?.let {
            throw DuplicateIsbnException(command.isbn)
        }

        return bookPersistence.create(command.title, command.isbn, command.publishedYear, command.authorId)
    }

    fun update(id: UUID, command: UpdateBookCommand): Book {
        val existing = bookPersistence.findById(id) ?: throw BookNotFoundException(id)

        val title = command.title ?: existing.title
        val isbn = command.isbn ?: existing.isbn
        val publishedYear = command.publishedYear ?: existing.publishedYear

        validateBookCommand(title, isbn, publishedYear)

        if (command.authorId != null) {
            authorPersistence.findById(command.authorId) ?: throw AuthorNotFoundException(command.authorId)
        }

        // Check if ISBN is being changed and if it already exists
        if (command.isbn != null && command.isbn != existing.isbn) {
            bookPersistence.findByIsbn(command.isbn)?.let {
                throw DuplicateIsbnException(command.isbn)
            }
        }

        return bookPersistence.update(id, command.title, command.isbn, command.publishedYear, command.authorId)
            ?: throw BookNotFoundException(id)
    }

    fun delete(id: UUID): Boolean {
        if (bookPersistence.findById(id) == null) {
            throw BookNotFoundException(id)
        }
        return bookPersistence.delete(id)
    }

    private fun validateBookCommand(title: String, isbn: String, publishedYear: Int) {
        if (title.isBlank()) {
            throw InvalidInputException("Book title cannot be blank")
        }
        if (isbn.isBlank()) {
            throw InvalidInputException("Book ISBN cannot be blank")
        }
        if (isbn.length != 13) {
            throw InvalidInputException("Book ISBN must be 13 characters")
        }
        if (publishedYear < 1000 || publishedYear > 9999) {
            throw InvalidInputException("Book published year must be a valid 4-digit year")
        }
    }
}
