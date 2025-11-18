package com.rkoubsky.books.repository

import com.rkoubsky.books.jooq.tables.references.AUTHOR
import com.rkoubsky.books.jooq.tables.references.BOOK
import com.rkoubsky.books.service.Author
import com.rkoubsky.books.service.Book
import com.rkoubsky.books.service.BookFilter
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class BookRepository(private val dsl: DSLContext) {

    fun findById(id: UUID): Book? {
        return dsl.select()
            .from(BOOK)
            .leftOuterJoin(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .where(BOOK.ID.eq(id))
            .fetchOne()
            ?.let { mapToBookWithAuthor(it) }
    }

    fun findAll(filter: BookFilter? = null): List<Book> {
        val condition = getFilterCondition(filter)

        return dsl.select()
            .from(BOOK)
            .leftOuterJoin(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .where(condition)
            .fetch()
            .map { mapToBookWithAuthor(it) }
    }

    fun findByIsbn(isbn: String): Book? {
        return dsl.select()
            .from(BOOK)
            .leftOuterJoin(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .where(BOOK.ISBN.eq(isbn))
            .fetchOne()
            ?.let { mapToBookWithAuthor(it) }
    }

    fun create(title: String, isbn: String, publishedYear: Int, authorId: UUID): Book {
        val now = OffsetDateTime.now()

        val record = dsl.insertInto(BOOK)
            .set(BOOK.TITLE, title)
            .set(BOOK.ISBN, isbn)
            .set(BOOK.PUBLISHED_YEAR, publishedYear)
            .set(BOOK.AUTHOR_ID, authorId)
            .set(BOOK.CREATED_AT, now)
            .set(BOOK.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

        return findById(record.get(BOOK.ID)!!)!!
    }

    fun update(id: UUID, title: String?, isbn: String?, publishedYear: Int?, authorId: UUID?): Book? {
        val existing = findById(id) ?: return null
        val now = OffsetDateTime.now()

        val updateQuery = dsl.update(BOOK)
            .set(BOOK.UPDATED_AT, now)

        if (title != null) updateQuery.set(BOOK.TITLE, title)
        if (isbn != null) updateQuery.set(BOOK.ISBN, isbn)
        if (publishedYear != null) updateQuery.set(BOOK.PUBLISHED_YEAR, publishedYear)
        if (authorId != null) updateQuery.set(BOOK.AUTHOR_ID, authorId)

        updateQuery.where(BOOK.ID.eq(id)).execute()

        return findById(id)
    }

    fun delete(id: UUID): Boolean {
        val deleted = dsl.deleteFrom(BOOK)
            .where(BOOK.ID.eq(id))
            .execute()
        return deleted > 0
    }

    private fun getFilterCondition(filter: BookFilter?): Condition {
        var condition: Condition = DSL.trueCondition()

        if (filter != null) {
            if (filter.title != null) {
                condition = condition.and(BOOK.TITLE.likeIgnoreCase("%${filter.title}%"))
            }

            if (filter.isbn != null) {
                condition = condition.and(BOOK.ISBN.eq(filter.isbn))
            }

            if (filter.publishedYear != null) {
                condition = condition.and(BOOK.PUBLISHED_YEAR.eq(filter.publishedYear))
            }

            if (filter.authorId != null) {
                condition = condition.and(BOOK.AUTHOR_ID.eq(filter.authorId))
            }
        }

        return condition
    }

    private fun mapToBookWithAuthor(record: org.jooq.Record): Book {
        val author = Author(
            id = record.get(AUTHOR.ID)!!,
            name = record.get(AUTHOR.NAME)!!,
            surname = record.get(AUTHOR.SURNAME)!!,
            bio = record.get(AUTHOR.BIO),
            createdAt = record.get(AUTHOR.CREATED_AT)!!,
            updatedAt = record.get(AUTHOR.UPDATED_AT)!!
        )

        return Book(
            id = record.get(BOOK.ID)!!,
            title = record.get(BOOK.TITLE)!!,
            isbn = record.get(BOOK.ISBN)!!,
            publishedYear = record.get(BOOK.PUBLISHED_YEAR)!!,
            author = author,
            createdAt = record.get(BOOK.CREATED_AT)!!,
            updatedAt = record.get(BOOK.UPDATED_AT)!!
        )
    }
}
