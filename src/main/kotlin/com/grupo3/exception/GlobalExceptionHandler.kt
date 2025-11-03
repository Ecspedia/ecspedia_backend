package com.grupo3.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import java.util.Date

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorDetails> {
        val violations = ex.bindingResult.fieldErrors.map {
            Violation(
                field = it.field,
                message = it.defaultMessage ?: "Invalid value",
                rejectedValue = it.rejectedValue?.toString()
            )
        }
        val firstErrorMessage = violations.firstOrNull()?.message ?: "Invalid value"
        val errorDetails = ErrorDetails(
            message = firstErrorMessage,
            details = "Validation Error",
            localDateTime = Date(),
            code = "VALIDATION_FAILED",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI,
            violations = violations
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }


    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorDetails> {
        val response = ErrorDetails(
            message = ex.message ?: "Unexpected error occurred",
            details = "Internal Server Error",
            localDateTime = Date(),
            code = "INTERNAL_SERVER_ERROR",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            path = request.requestURI
        )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorDetails> {
        val errorDetails = ErrorDetails(
            message = ex.message ?: "Not Found",
            details = "NotFound",
            localDateTime = Date(),
            code = "NOT_FOUND",
            status = HttpStatus.NOT_FOUND.value(),
            path = request.requestURI
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }
}
