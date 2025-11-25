package com.rkoubsky.books.exception

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Component

@Component
class GraphQLExceptionHandler : DataFetcherExceptionResolverAdapter() {

    override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment): GraphQLError? {
        return when (ex) {
            is AuthorNotFoundException -> {
                GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.message)
                    .path(env.executionStepInfo.path)
                    .location(env.field.sourceLocation)
                    .build()
            }
            is BookNotFoundException -> {
                GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.message)
                    .path(env.executionStepInfo.path)
                    .location(env.field.sourceLocation)
                    .build()
            }
            is DuplicateIsbnException -> {
                GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.message)
                    .path(env.executionStepInfo.path)
                    .location(env.field.sourceLocation)
                    .build()
            }
            is InvalidInputException -> {
                GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.message)
                    .path(env.executionStepInfo.path)
                    .location(env.field.sourceLocation)
                    .build()
            }
            else -> {
                logger.error("Unexpected error occurred", ex)
                GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .message("An unexpected error occurred")
                    .path(env.executionStepInfo.path)
                    .location(env.field.sourceLocation)
                    .build()
            }
        }
    }
}
