package com.grupo3.exception

import graphql.GraphQLError
import graphql.GraphQLException
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import jakarta.validation.ConstraintViolationException
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.execution.ErrorType
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ControllerAdvice
import java.util.Date

@ControllerAdvice
class GraphQLExceptionHandler {

    @GraphQlExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException, env: DataFetchingEnvironment): GraphQLError {
        val violations = ex.constraintViolations.map {
            Violation(
                field = it.propertyPath.toString(),
                message = it.message,
                rejectedValue = it.invalidValue?.toString()
            )
        }
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
            .extensions(mapOf(
                "error" to error
            ))
            .build()
    }

    @GraphQlExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, env: DataFetchingEnvironment): GraphQLError {
        val violations = ex.bindingResult.fieldErrors.map {
            Violation(
                field = it.field,
                message = it.defaultMessage ?: "Invalid value",
                rejectedValue = it.rejectedValue?.toString()
            )
        }
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
            .extensions(mapOf(
                "error" to error
            ))
            .build()
    }

    @GraphQlExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException, env: DataFetchingEnvironment): GraphQLError {
        val violations = ex.bindingResult.fieldErrors.map {
            Violation(
                field = it.field,
                message = it.defaultMessage ?: "Invalid value",
                rejectedValue = it.rejectedValue?.toString()
            )
        }
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
            .extensions(mapOf(
                "error" to error
            ))
            .build()
    }

    @GraphQlExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException, env: DataFetchingEnvironment): GraphQLError {
        val error = ErrorDetails(
            message = ex.message ?: "Invalid request",
            details = "Invalid Argument",
            localDateTime = Date(),
            code = "INVALID_ARGUMENT",
            status = 400,
            path = env.executionStepInfo.path.toString()
        )
        return GraphqlErrorBuilder.newError()
            .errorType(ErrorType.BAD_REQUEST)
            .message(error.message)
            .path(env.executionStepInfo.path)
            .location(env.field.sourceLocation)
            .extensions(mapOf("error" to error))
            .build()
    }

    @GraphQlExceptionHandler(GraphQLException::class)
    fun handleGraphQLException(ex: GraphQLException, env: DataFetchingEnvironment): GraphQLError {
        val error = ErrorDetails(
            message = ex.message ?: "Request failed",
            details = "GraphQL Error",
            localDateTime = Date(),
            code = "GRAPHQL_ERROR",
            status = 400,
            path = env.executionStepInfo.path.toString()
        )
        return GraphqlErrorBuilder.newError()
            .errorType(ErrorType.BAD_REQUEST)
            .message(error.message)
            .path(env.executionStepInfo.path)
            .location(env.field.sourceLocation)
            .extensions(mapOf("error" to error))
            .build()
    }
}
