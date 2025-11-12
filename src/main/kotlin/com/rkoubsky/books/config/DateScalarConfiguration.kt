package com.rkoubsky.books.config

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@Configuration
class DateScalarConfiguration {

    @Bean
    fun dateScalar(): GraphQLScalarType {
        return GraphQLScalarType.newScalar()
            .name("Date")
            .description("Custom scalar for OffsetDateTime in ISO-8601 format")
            .coercing(DateCoercing())
            .build()
    }

    @Bean
    fun runtimeWiringConfigurer(dateScalar: GraphQLScalarType): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { builder ->
            builder.scalar(dateScalar)
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
