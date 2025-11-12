package com.rkoubsky.books.service

import com.rkoubsky.books.exception.AuthorNotFoundException
import com.rkoubsky.books.exception.BookNotFoundException
import com.rkoubsky.books.exception.DuplicateIsbnException
import com.rkoubsky.books.exception.InvalidInputException
import com.rkoubsky.books.repository.AuthorRepository
import com.rkoubsky.books.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {

    fun findById(id: UUID): Book {
        return bookRepository.findById(id) ?: throw BookNotFoundException(id)
    }

    fun findAll(): List<Book> {
        return bookRepository.findAll()
    }

    fun findByTitle(title: String): List<Book> {
        return bookRepository.findByTitle(title)
    }

    fun findByIsbn(isbn: String): Book? {
        return bookRepository.findByIsbn(isbn)
    }

    fun findByYear(year: Int): List<Book> {
        return bookRepository.findByYear(year)
    }

    fun findByAuthorId(authorId: UUID): List<Book> {
        return bookRepository.findByAuthorId(authorId)
    }

    fun create(command: CreateBookCommand): Book {
        validateBookCommand(command.title, command.isbn, command.year)

        // Check if author exists
        authorRepository.findById(command.authorId) ?: throw AuthorNotFoundException(command.authorId)

        // Check if ISBN already exists
        bookRepository.findByIsbn(command.isbn)?.let {
            throw DuplicateIsbnException(command.isbn)
        }

        return bookRepository.create(command.title, command.isbn, command.year, command.authorId)
    }

    fun update(id: UUID, command: UpdateBookCommand): Book {
        val existing = bookRepository.findById(id) ?: throw BookNotFoundException(id)

        // Validate fields if provided
        val title = command.title ?: existing.title
        val isbn = command.isbn ?: existing.isbn
        val year = command.year ?: existing.year

        validateBookCommand(title, isbn, year)

        // Check if author exists if authorId is being updated
        if (command.authorId != null) {
            authorRepository.findById(command.authorId) ?: throw AuthorNotFoundException(command.authorId)
        }

        // Check if ISBN is being changed and if it already exists
        if (command.isbn != null && command.isbn != existing.isbn) {
            bookRepository.findByIsbn(command.isbn)?.let {
                throw DuplicateIsbnException(command.isbn)
            }
        }

        return bookRepository.update(id, command.title, command.isbn, command.year, command.authorId)
            ?: throw BookNotFoundException(id)
    }

    fun delete(id: UUID): Boolean {
        if (bookRepository.findById(id) == null) {
            throw BookNotFoundException(id)
        }
        return bookRepository.delete(id)
    }

    private fun validateBookCommand(title: String, isbn: String, year: Int) {
        if (title.isBlank()) {
            throw InvalidInputException("Book title cannot be blank")
        }
        if (isbn.isBlank()) {
            throw InvalidInputException("Book ISBN cannot be blank")
        }
        if (isbn.length != 13) {
            throw InvalidInputException("Book ISBN must be 13 characters")
        }
        if (year < 1000 || year > 9999) {
            throw InvalidInputException("Book year must be a valid 4-digit year")
        }
    }
}
