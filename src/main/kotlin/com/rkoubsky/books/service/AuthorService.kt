package com.rkoubsky.books.service

import com.rkoubsky.books.exception.AuthorNotFoundException
import com.rkoubsky.books.exception.InvalidInputException
import com.rkoubsky.books.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class AuthorService(
    private val authorRepository: AuthorRepository
) {

    fun findById(id: UUID): Author {
        return authorRepository.findById(id) ?: throw AuthorNotFoundException(id)
    }

    fun findAll(): List<Author> {
        return authorRepository.findAll()
    }

    fun create(command: CreateAuthorCommand): Author {
        validateAuthorCommand(command.name, command.surname)
        return authorRepository.create(command.name, command.surname, command.bio)
    }

    fun update(id: UUID, command: UpdateAuthorCommand): Author {
        if (!authorRepository.findById(id)!!.let { true }) {
            throw AuthorNotFoundException(id)
        }

        if (command.name != null || command.surname != null) {
            val existing = authorRepository.findById(id)!!
            validateAuthorCommand(
                command.name ?: existing.name,
                command.surname ?: existing.surname
            )
        }

        return authorRepository.update(id, command.name, command.surname, command.bio)
            ?: throw AuthorNotFoundException(id)
    }

    fun delete(id: UUID): Boolean {
        if (!authorRepository.findById(id)!!.let { true }) {
            throw AuthorNotFoundException(id)
        }
        return authorRepository.delete(id)
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
