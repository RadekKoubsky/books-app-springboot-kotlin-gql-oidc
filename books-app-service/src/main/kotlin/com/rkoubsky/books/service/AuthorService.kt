package com.rkoubsky.books.service

import com.rkoubsky.books.exception.AuthorNotFoundException
import com.rkoubsky.books.exception.InvalidInputException
import com.rkoubsky.books.persistence.AuthorPersistence
import com.rkoubsky.books.service.model.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class AuthorService(
    private val authorPersistence: AuthorPersistence,
    private val bookService: BookService,
) {

    fun findById(id: UUID): Author {
        return authorPersistence.findById(id) ?: throw AuthorNotFoundException(id)
    }

    fun findAll(filter: AuthorFilter? = null): List<Author> {
        return authorPersistence.findAll(filter)
    }

    fun create(command: CreateAuthorCommand): Author {
        validateAuthorCommand(command.name, command.surname)
        return authorPersistence.create(command.name, command.surname, command.bio)
    }

    fun update(id: UUID, command: UpdateAuthorCommand): Author {
        authorPersistence.findById(id) ?: throw AuthorNotFoundException(id)

        if (command.name != null || command.surname != null) {
            val existing = authorPersistence.findById(id)!!
            validateAuthorCommand(
                command.name ?: existing.name,
                command.surname ?: existing.surname
            )
        }

        return authorPersistence.update(id, command.name, command.surname, command.bio)
            ?: throw AuthorNotFoundException(id)
    }

    fun delete(id: UUID): Boolean {
        authorPersistence.findById(id) ?: throw AuthorNotFoundException(id)
        val bookIds = bookService.findAll(BookFilter(authorId = id)).map { it.id }
        if (bookIds.isNotEmpty()) {
            bookService.delete(bookIds)
        }
        return authorPersistence.delete(id)
    }

    private fun validateAuthorCommand(name: String, surname: String) {
        if (name.isBlank()) {
            throw InvalidInputException("Author name cannot be blank")
        }
        if (surname.isBlank()) {
            throw InvalidInputException("Author surname cannot be blank")
        }
    }
}
