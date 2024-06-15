package com.github.pozo.investmentfunds.api.error

import org.slf4j.LoggerFactory
import org.springframework.context.MessageSourceResolvable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.io.IOException


@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: WebRequest): ResponseEntity<ErrorDetails> {
        logger.error("'$ex.message'. Request description: " + request.getDescription(true))

        val errorDetails = ErrorDetails(
            HttpStatus.BAD_REQUEST.value(),
            ex.message ?: "This request can not be served",
            request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IOException::class)
    fun handleIOException(ex: IOException, request: WebRequest): ResponseEntity<ErrorDetails> {
        logger.error("'$ex.message'. Request description: " + request.getDescription(true), ex)

        val errorDetails = ErrorDetails(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "This request can not be served",
            request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorDetails> {
        val errors = ex.bindingResult
            .fieldErrors
            .map { fieldError: FieldError -> "${fieldError.field}: ${fieldError.defaultMessage}" }
        val errorResponse = ErrorDetails(
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = "Validation failed",
            details = errors.toString()
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleHandlerMethodValidationException(
        ex: HandlerMethodValidationException,
        request: WebRequest
    ): ResponseEntity<ErrorDetails> {
        val errors = ex.allErrors
            .map { fieldError: MessageSourceResolvable -> fieldError.defaultMessage }
        val errorResponse = ErrorDetails(
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = "Validation failed",
            details = errors.toString()
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(
        ex: NoResourceFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorDetails> {

        val errorResponse = ErrorDetails(
            statusCode = HttpStatus.NOT_FOUND.value(),
            message = "Resource not found",
            details = ex.resourcePath
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}

data class ErrorDetails(
    val statusCode: Int,
    val message: String,
    val details: String
)