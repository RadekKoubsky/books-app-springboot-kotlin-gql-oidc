package com.rkoubsky.books.service

import com.rkoubsky.books.exception.AuthorNotFoundException
import com.rkoubsky.books.exception.BookNotFoundException
import com.rkoubsky.books.exception.DuplicateIsbnException
import com.rkoubsky.books.exception.InvalidInputException
import com.rkoubsky.books.repository.AuthorRepository
import com.rkoubsky.books.repository.BookRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.OffsetDateTime
import java.util.*

class BookServiceTest : FunSpec({

    lateinit var bookRepository: BookRepository
    lateinit var authorRepository: AuthorRepository
    lateinit var bookService: BookService

    val testAuthorId = UUID.randomUUID()
    val testBookId = UUID.randomUUID()

    val testAuthor = Author(
        id = testAuthorId,
        name = "John",
        surname = "Doe",
        bio = null,
        createdAt = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now()
    )

    val testBook = Book(
        id = testBookId,
        title = "Test Book",
        isbn = "1234567890123",
        year = 2024,
        author = testAuthor,
        createdAt = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now()
    )

    beforeEach {
        bookRepository = mockk()
        authorRepository = mockk()
        bookService = BookService(bookRepository, authorRepository)
    }

    test("findById should return book when found") {
        every { bookRepository.findById(testBookId) } returns testBook

        val result = bookService.findById(testBookId)

        result shouldBe testBook
        verify { bookRepository.findById(testBookId) }
    }

    test("findById should throw BookNotFoundException when not found") {
        every { bookRepository.findById(testBookId) } returns null

        shouldThrow<BookNotFoundException> {
            bookService.findById(testBookId)
        }
    }

    test("findAll should return all books") {
        val books = listOf(testBook)
        every { bookRepository.findAll() } returns books

        val result = bookService.findAll()

        result shouldBe books
        verify { bookRepository.findAll() }
    }

    test("findByTitle should return books matching title") {
        val books = listOf(testBook)
        every { bookRepository.findByTitle("Test") } returns books

        val result = bookService.findByTitle("Test")

        result shouldBe books
        verify { bookRepository.findByTitle("Test") }
    }

    test("findByIsbn should return book when found") {
        every { bookRepository.findByIsbn("1234567890123") } returns testBook

        val result = bookService.findByIsbn("1234567890123")

        result shouldBe testBook
        verify { bookRepository.findByIsbn("1234567890123") }
    }

    test("findByYear should return books matching year") {
        val books = listOf(testBook)
        every { bookRepository.findByYear(2024) } returns books

        val result = bookService.findByYear(2024)

        result shouldBe books
        verify { bookRepository.findByYear(2024) }
    }

    test("create should create book with valid input") {
        val command = CreateBookCommand(
            title = "Test Book",
            isbn = "1234567890123",
            year = 2024,
            authorId = testAuthorId
        )

        every { authorRepository.findById(testAuthorId) } returns testAuthor
        every { bookRepository.findByIsbn(command.isbn) } returns null
        every { bookRepository.create(command.title, command.isbn, command.year, command.authorId) } returns testBook

        val result = bookService.create(command)

        result shouldBe testBook
        verify { bookRepository.create(command.title, command.isbn, command.year, command.authorId) }
    }

    test("create should throw AuthorNotFoundException when author does not exist") {
        val command = CreateBookCommand(
            title = "Test Book",
            isbn = "1234567890123",
            year = 2024,
            authorId = testAuthorId
        )

        every { authorRepository.findById(testAuthorId) } returns null

        shouldThrow<AuthorNotFoundException> {
            bookService.create(command)
        }
    }

    test("create should throw DuplicateIsbnException when ISBN already exists") {
        val command = CreateBookCommand(
            title = "Test Book",
            isbn = "1234567890123",
            year = 2024,
            authorId = testAuthorId
        )

        every { authorRepository.findById(testAuthorId) } returns testAuthor
        every { bookRepository.findByIsbn(command.isbn) } returns testBook

        shouldThrow<DuplicateIsbnException> {
            bookService.create(command)
        }
    }

    test("create should throw InvalidInputException when title is blank") {
        val command = CreateBookCommand(
            title = "",
            isbn = "1234567890123",
            year = 2024,
            authorId = testAuthorId
        )

        shouldThrow<InvalidInputException> {
            bookService.create(command)
        }
    }

    test("create should throw InvalidInputException when ISBN length is invalid") {
        val command = CreateBookCommand(
            title = "Test Book",
            isbn = "123",
            year = 2024,
            authorId = testAuthorId
        )

        shouldThrow<InvalidInputException> {
            bookService.create(command)
        }
    }

    test("create should throw InvalidInputException when year is invalid") {
        val command = CreateBookCommand(
            title = "Test Book",
            isbn = "1234567890123",
            year = 999,
            authorId = testAuthorId
        )

        shouldThrow<InvalidInputException> {
            bookService.create(command)
        }
    }

    test("update should update book with valid input") {
        val command = UpdateBookCommand(
            title = "Updated Title",
            isbn = null,
            year = null,
            authorId = null
        )
        val updatedBook = testBook.copy(title = "Updated Title")

        every { bookRepository.findById(testBookId) } returns testBook
        every { bookRepository.update(testBookId, command.title, command.isbn, command.year, command.authorId) } returns updatedBook

        val result = bookService.update(testBookId, command)

        result shouldBe updatedBook
        verify { bookRepository.update(testBookId, command.title, command.isbn, command.year, command.authorId) }
    }

    test("update should throw BookNotFoundException when book not found") {
        val command = UpdateBookCommand(title = "Updated Title", isbn = null, year = null, authorId = null)
        every { bookRepository.findById(testBookId) } returns null

        shouldThrow<BookNotFoundException> {
            bookService.update(testBookId, command)
        }
    }

    test("update should throw DuplicateIsbnException when updating to existing ISBN") {
        val command = UpdateBookCommand(
            title = null,
            isbn = "9876543210123",
            year = null,
            authorId = null
        )
        val otherBook = testBook.copy(id = UUID.randomUUID(), isbn = "9876543210123")

        every { bookRepository.findById(testBookId) } returns testBook
        every { bookRepository.findByIsbn("9876543210123") } returns otherBook

        shouldThrow<DuplicateIsbnException> {
            bookService.update(testBookId, command)
        }
    }

    test("delete should delete book when exists") {
        every { bookRepository.findById(testBookId) } returns testBook
        every { bookRepository.delete(testBookId) } returns true

        val result = bookService.delete(testBookId)

        result shouldBe true
        verify { bookRepository.delete(testBookId) }
    }

    test("delete should throw BookNotFoundException when not found") {
        every { bookRepository.findById(testBookId) } returns null

        shouldThrow<BookNotFoundException> {
            bookService.delete(testBookId)
        }
    }
})
