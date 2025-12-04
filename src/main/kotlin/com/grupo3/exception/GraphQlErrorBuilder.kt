// exception/GraphQLErrorBuilder.kt
package com.grupo3.exception

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.execution.ErrorType
import java.util.Date

object GraphQLErrorFactory {
    fun buildError(
        message: String,
        code: String,
        errorType: ErrorType,
        status: Int,
        env: DataFetchingEnvironment,
        details: String? = null
    ): GraphQLError {
        val error = ErrorDetails(
            message = message,
            details = details ?: code.formatAsDetails(),
            localDateTime = Date(),
            code = code,
            status = status,
            path = env.executionStepInfo.path.toString()
        )
        return GraphqlErrorBuilder.newError()
            .errorType(errorType)
            .message(error.message)
            .path(env.executionStepInfo.path)
            .location(env.field.sourceLocation)
            .extensions(mapOf("error" to error))
            .build()
    }

    fun buildValidationError(violations: List<Violation>, env: DataFetchingEnvironment): GraphQLError {
        val error = ErrorDetails(
            message = "Validation failed",
            details = "Validation Error",
            localDateTime = Date(),
            code = "VALIDATION_FAILED",
            status = 400,
            path = env.executionStepInfo.path.toString(),
            violations = violations
        )
        return GraphqlErrorBuilder.newError()
            .errorType(ErrorType.BAD_REQUEST)
            .message(error.message)
            .path(env.executionStepInfo.path)
            .location(env.field.sourceLocation)
            .extensions(mapOf("error" to error))
            .build()
    }

    fun extractViolations(bindingResult: org.springframework.validation.BindingResult): List<Violation> {
        return bindingResult.fieldErrors.map {
            Violation(
                field = it.field,
                message = it.defaultMessage ?: "Invalid value",
                rejectedValue = it.rejectedValue?.toString()
            )
        }
    }
    private fun String.formatAsDetails(): String =
        this.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}