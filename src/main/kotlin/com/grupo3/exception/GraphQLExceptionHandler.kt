package com.grupo3.exception

import com.grupo3.controller.LocationController
import com.grupo3.exception.GraphQLErrorFactory.buildError
import com.grupo3.exception.GraphQLErrorFactory.buildValidationError
import com.grupo3.exception.GraphQLErrorFactory.extractViolations
import com.grupo3.exception.customException.HotelApiException
import com.grupo3.exception.customException.LocationAlreadyExistsException
import com.grupo3.exception.customException.LocationNotFoundException
import graphql.GraphQLError
import graphql.GraphQLException
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.execution.ErrorType
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ControllerAdvice
import java.util.Date

@ControllerAdvice
class GraphQLExceptionHandler {

    private val logger= LoggerFactory.getLogger(LocationController::class.java)

    // ============ VALIDATION EXCEPTIONS ============

    @GraphQlExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException, env: DataFetchingEnvironment): GraphQLError {
        val violations = ex.constraintViolations.map {
            Violation(
                field = it.propertyPath.toString(),
                message = it.message,
                rejectedValue = it.invalidValue?.toString()
            )
        }
        return buildValidationError(violations, env)
    }

    @GraphQlExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, env: DataFetchingEnvironment): GraphQLError {
        val violations = extractViolations(ex.bindingResult)
        return buildValidationError(violations, env)
    }

    @GraphQlExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException, env: DataFetchingEnvironment): GraphQLError {
        val violations = extractViolations(ex.bindingResult)
        return buildValidationError(violations, env)
    }

    // ============ CUSTOM LOCATION EXCEPTIONS ============

    @GraphQlExceptionHandler(LocationNotFoundException::class)
    fun handleLocationNotFound(ex: LocationNotFoundException, env: DataFetchingEnvironment): GraphQLError {
        return buildError(
            message = ex.message ?: "Location not found",
            code = "LOCATION_NOT_FOUND",
            errorType = ErrorType.NOT_FOUND,
            status = 404,
            env = env
        )
    }

    @GraphQlExceptionHandler(LocationAlreadyExistsException::class)
    fun handleLocationAlreadyExists(ex: LocationAlreadyExistsException, env: DataFetchingEnvironment): GraphQLError {
        return buildError(
            message = ex.message ?: "Location already exists",
            code = "LOCATION_ALREADY_EXISTS",
            errorType = ErrorType.BAD_REQUEST,
            status = 409,
            env = env
        )
    }

    @GraphQlExceptionHandler(HotelApiException::class)
    fun handleHotelApiException(ex: HotelApiException, env: DataFetchingEnvironment): GraphQLError {
        return buildError(
            message = ex.message ?: "Hotel service unavailable",
            code = "HOTEL_API_ERROR",
            errorType = ErrorType.INTERNAL_ERROR,
            status = ex.statusCode,
            env = env
        )
    }

    // ============ GENERIC EXCEPTIONS ============

    @GraphQlExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException, env: DataFetchingEnvironment): GraphQLError {
        return buildError(
            message = ex.message ?: "Invalid request",
            code = "INVALID_ARGUMENT",
            errorType = ErrorType.BAD_REQUEST,
            status = 400,
            env = env
        )
    }

    @GraphQlExceptionHandler(GraphQLException::class)
    fun handleGraphQLException(ex: GraphQLException, env: DataFetchingEnvironment): GraphQLError {
        return buildError(
            message = ex.message ?: "Request failed",
            code = "GRAPHQL_ERROR",
            errorType = ErrorType.BAD_REQUEST,
            status = 400,
            env = env
        )
    }

    // Catch-all for unexpected exceptions
    @GraphQlExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, env: DataFetchingEnvironment): GraphQLError {

        logger.error("Unexpected error", ex)

        return buildError(
            message = "An unexpected error occurred",
            code = "INTERNAL_ERROR",
            errorType = ErrorType.INTERNAL_ERROR,
            status = 500,
            env = env
        )
    }


}