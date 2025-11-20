package com.rkoubsky.books.exception

import java.util.*

sealed class BooksException(message: String) : RuntimeException(message)

class AuthorNotFoundException(id: UUID) : BooksException("Author not found with id: $id")

class BookNotFoundException(id: UUID) : BooksException("Book not found with id: $id")

class DuplicateIsbnException(isbn: String) : BooksException("Book with ISBN $isbn already exists")

class InvalidInputException(message: String) : BooksException(message)
