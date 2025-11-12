package com.rkoubsky.books.service

import com.rkoubsky.books.exception.AuthorNotFoundException
import com.rkoubsky.books.exception.InvalidInputException
import com.rkoubsky.books.repository.AuthorRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.OffsetDateTime
import java.util.*

class AuthorServiceTest : FunSpec({

    lateinit var authorRepository: AuthorRepository
    lateinit var authorService: AuthorService

    val testAuthorId = UUID.randomUUID()
    val testAuthor = Author(
        id = testAuthorId,
        name = "John",
        surname = "Doe",
        bio = "Test bio",
        createdAt = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now()
    )

    beforeEach {
        authorRepository = mockk()
        authorService = AuthorService(authorRepository)
    }

    test("findById should return author when found") {
        every { authorRepository.findById(testAuthorId) } returns testAuthor

        val result = authorService.findById(testAuthorId)

        result shouldBe testAuthor
        verify { authorRepository.findById(testAuthorId) }
    }

    test("findById should throw AuthorNotFoundException when not found") {
        every { authorRepository.findById(testAuthorId) } returns null

        shouldThrow<AuthorNotFoundException> {
            authorService.findById(testAuthorId)
        }
    }

    test("findAll should return all authors") {
        val authors = listOf(testAuthor)
        every { authorRepository.findAll() } returns authors

        val result = authorService.findAll()

        result shouldBe authors
        verify { authorRepository.findAll() }
    }

    test("create should create author with valid input") {
        val command = CreateAuthorCommand(
            name = "John",
            surname = "Doe",
            bio = "Test bio"
        )
        every { authorRepository.create(command.name, command.surname, command.bio) } returns testAuthor

        val result = authorService.create(command)

        result shouldBe testAuthor
        verify { authorRepository.create(command.name, command.surname, command.bio) }
    }

    test("create should throw InvalidInputException when name is blank") {
        val command = CreateAuthorCommand(
            name = "",
            surname = "Doe",
            bio = null
        )

        shouldThrow<InvalidInputException> {
            authorService.create(command)
        }
    }

    test("create should throw InvalidInputException when surname is blank") {
        val command = CreateAuthorCommand(
            name = "John",
            surname = "",
            bio = null
        )

        shouldThrow<InvalidInputException> {
            authorService.create(command)
        }
    }

    test("update should update author with valid input") {
        val command = UpdateAuthorCommand(
            name = "Jane",
            surname = "Smith",
            bio = "Updated bio"
        )
        val updatedAuthor = testAuthor.copy(name = "Jane", surname = "Smith", bio = "Updated bio")

        every { authorRepository.findById(testAuthorId) } returns testAuthor
        every { authorRepository.update(testAuthorId, command.name, command.surname, command.bio) } returns updatedAuthor

        val result = authorService.update(testAuthorId, command)

        result shouldBe updatedAuthor
        verify { authorRepository.update(testAuthorId, command.name, command.surname, command.bio) }
    }

    test("update should throw AuthorNotFoundException when author not found") {
        val command = UpdateAuthorCommand(name = "Jane", surname = "Smith", bio = null)
        every { authorRepository.findById(testAuthorId) } returns null

        shouldThrow<AuthorNotFoundException> {
            authorService.update(testAuthorId, command)
        }
    }

    test("delete should delete author when exists") {
        every { authorRepository.findById(testAuthorId) } returns testAuthor
        every { authorRepository.delete(testAuthorId) } returns true

        val result = authorService.delete(testAuthorId)

        result shouldBe true
        verify { authorRepository.delete(testAuthorId) }
    }

    test("delete should throw AuthorNotFoundException when not found") {
        every { authorRepository.findById(testAuthorId) } returns null

        shouldThrow<AuthorNotFoundException> {
            authorService.delete(testAuthorId)
        }
    }
})
