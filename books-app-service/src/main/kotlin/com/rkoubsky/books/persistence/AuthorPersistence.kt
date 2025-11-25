package com.rkoubsky.books.persistence

import com.rkoubsky.books.jooq.tables.references.AUTHOR
import com.rkoubsky.books.service.model.Author
import com.rkoubsky.books.service.model.AuthorFilter
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class AuthorPersistence(private val dsl: DSLContext) {

    fun findById(id: UUID): Author? {
        return dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .fetchOne()
            ?.let { mapToAuthor(it) }
    }

    fun findByIds(ids: Set<UUID>): List<Author> {
        if (ids.isEmpty()) return emptyList()
        return dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.`in`(ids))
            .fetch()
            .map { mapToAuthor(it) }
    }

    fun findAll(filter: AuthorFilter? = null): List<Author> {
        val condition = getFilterCondition(filter)

        return dsl.selectFrom(AUTHOR)
            .where(condition)
            .fetch()
            .map { mapToAuthor(it) }
    }

    fun create(name: String, surname: String, bio: String?): Author {
        val now = OffsetDateTime.now()

        val record = dsl.insertInto(AUTHOR)
            .set(AUTHOR.NAME, name)
            .set(AUTHOR.SURNAME, surname)
            .set(AUTHOR.BIO, bio)
            .set(AUTHOR.CREATED_AT, now)
            .set(AUTHOR.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

        return mapToAuthor(record)
    }

    fun update(id: UUID, name: String?, surname: String?, bio: String?): Author? {
        val now = OffsetDateTime.now()

        val updateQuery = dsl.update(AUTHOR)
            .set(AUTHOR.UPDATED_AT, now)

        if (name != null) updateQuery.set(AUTHOR.NAME, name)
        if (surname != null) updateQuery.set(AUTHOR.SURNAME, surname)
        if (bio != null) updateQuery.set(AUTHOR.BIO, bio)

        updateQuery.where(AUTHOR.ID.eq(id)).execute()

        return findById(id)
    }

    fun delete(id: UUID): Boolean {
        val deleted = dsl.deleteFrom(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .execute()
        return deleted > 0
    }

    private fun getFilterCondition(filter: AuthorFilter?): Condition {
        var condition: Condition = DSL.trueCondition()

        if (filter != null) {
            if (filter.name != null) {
                condition = condition.and(AUTHOR.NAME.likeIgnoreCase("%${filter.name}%"))
            }

            if (filter.surname != null) {
                condition = condition.and(AUTHOR.SURNAME.likeIgnoreCase("%${filter.surname}%"))
            }
        }

        return condition
    }

    private fun mapToAuthor(record: org.jooq.Record): Author {
        return Author(
            id = record.get(AUTHOR.ID)!!,
            name = record.get(AUTHOR.NAME)!!,
            surname = record.get(AUTHOR.SURNAME)!!,
            bio = record.get(AUTHOR.BIO),
            createdAt = record.get(AUTHOR.CREATED_AT)!!,
            updatedAt = record.get(AUTHOR.UPDATED_AT)!!
        )
    }
}
