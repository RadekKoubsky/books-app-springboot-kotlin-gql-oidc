package com.rkoubsky.books.gql

import com.rkoubsky.books.service.*
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class AuthorController(
    private val authorService: AuthorService,
    private val authorMapper: AuthorMapper
) {

    @QueryMapping
    fun author(@Argument id: UUID): AuthorGQL? {
        return authorMapper.toGQL(authorService.findById(id))
    }

    @QueryMapping
    fun authors(@Argument filter: AuthorFilterGQL?): List<AuthorGQL> {
        val domainFilter = authorMapper.mapFilter(filter)
        return authorService.findAll(domainFilter).map { authorMapper.toGQL(it) }
    }

    @MutationMapping
    fun createAuthor(@Argument input: CreateAuthorInputGQL): AuthorGQL {
        val command = CreateAuthorCommand(
            name = input.name,
            surname = input.surname,
            bio = input.bio
        )
        return authorMapper.toGQL(authorService.create(command))
    }

    @MutationMapping
    fun updateAuthor(@Argument id: UUID, @Argument input: UpdateAuthorInputGQL): AuthorGQL {
        val command = UpdateAuthorCommand(
            name = input.name,
            surname = input.surname,
            bio = input.bio
        )
        return authorMapper.toGQL(authorService.update(id, command))
    }

    @MutationMapping
    fun deleteAuthor(@Argument id: UUID): Boolean {
        return authorService.delete(id)
    }
}
