package com.rkoubsky.books.gql

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@Configuration
class GraphQLScalarConfig {

    @Bean
    fun runtimeWiringConfigurer(): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { wiringBuilder ->
            wiringBuilder.scalar(
                GraphQLScalarType.newScalar()
                    .name("UUID")
                    .description("UUID scalar type")
                    .coercing(UUIDCoercing())
                    .build()
            ).scalar(
                GraphQLScalarType.newScalar()
                .name("Date")
                .description("Custom scalar for OffsetDateTime in ISO-8601 format")
                .coercing(DateCoercing())
                .build())
        }
    }

    private class UUIDCoercing : Coercing<UUID, String> {
        override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String {
            return when (dataFetcherResult) {
                is UUID -> dataFetcherResult.toString()
                is String -> dataFetcherResult
                else -> throw CoercingSerializeException("Expected UUID but got: ${dataFetcherResult::class.java.simpleName}")
            }
        }

        override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): UUID {
            return try {
                when (input) {
                    is UUID -> input
                    is String -> UUID.fromString(input)
                    else -> throw CoercingParseValueException("Expected String but got: ${input::class.java.simpleName}")
                }
            } catch (e: IllegalArgumentException) {
                throw CoercingParseValueException("Invalid UUID format: $input", e)
            }
        }

        override fun parseLiteral(
            input: Value<*>,
            variables: CoercedVariables,
            graphQLContext: GraphQLContext,
            locale: Locale
        ): UUID {
            return try {
                when (input) {
                    is StringValue -> UUID.fromString(input.value)
                    else -> throw CoercingParseLiteralException("Expected StringValue but got: ${input::class.java.simpleName}")
                }
            } catch (e: IllegalArgumentException) {
                throw CoercingParseLiteralException("Invalid UUID format: $input", e)
            }
        }
    }

    private class DateCoercing : Coercing<OffsetDateTime, String> {

        override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String {
            return when (dataFetcherResult) {
                is OffsetDateTime -> dataFetcherResult.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                else -> throw CoercingSerializeException("Expected OffsetDateTime but got: ${dataFetcherResult.javaClass.name}")
            }
        }

        override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): OffsetDateTime {
            return when (input) {
                is String -> parseOffsetDateTime(input)
                else -> throw CoercingParseValueException("Expected String but got: ${input.javaClass.name}")
            }
        }

        override fun parseLiteral(
            input: Value<*>,
            variables: CoercedVariables,
            graphQLContext: GraphQLContext,
            locale: Locale
        ): OffsetDateTime {
            if (input is StringValue) {
                return parseOffsetDateTime(input.value)
            }
            throw CoercingParseLiteralException("Expected StringValue but got: ${input.javaClass.name}")
        }

        private fun parseOffsetDateTime(value: String): OffsetDateTime {
            return try {
                OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } catch (e: DateTimeParseException) {
                throw CoercingParseValueException("Invalid date format. Expected ISO-8601 format", e)
            }
        }
    }
}