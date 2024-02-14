package me.dio.credit.api.exception

import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionData> {
        val errors: Map<String, String?> = ex.bindingResult.allErrors.stream().toList()
            .associate { error: ObjectError -> (error as FieldError).field to error.defaultMessage }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ExceptionData(
                    message = "Bad request",
                    timestamp = LocalDateTime.now(),
                    exception = ex.javaClass.toString(),
                    details = mutableListOf(errors)
                )
            )
    }

    @ExceptionHandler(InvalidDateException::class)
    fun handlerInvalidDateException(ex: InvalidDateException): ResponseEntity<ExceptionData> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ExceptionData(
                    timestamp = LocalDateTime.now(),
                    exception = ex.javaClass.toString(),
                    message = ex.message
                )
            )
    }

    @ExceptionHandler(DataAccessException::class)
    fun handlerDataAccessException(ex: DataAccessException): ResponseEntity<ExceptionData> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                ExceptionData(
                    message = "User conflict",
                    timestamp = LocalDateTime.now(),
                    exception = ex.javaClass.toString(),
                    details = mutableListOf(ex.message)
                )
            )
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handlerUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ExceptionData> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ExceptionData(
                    message = ex.message,
                    timestamp = LocalDateTime.now(),
                    exception = ex.javaClass.toString(),
                )
            )
    }

    @ExceptionHandler(CreditCodeNotFoundException::class)
    fun handlerCreditCodeNotFoundException(ex: CreditCodeNotFoundException): ResponseEntity<ExceptionData> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ExceptionData(
                    message = ex.message,
                    timestamp = LocalDateTime.now(),
                    exception = ex.javaClass.toString(),
                )
            )
    }

    @ExceptionHandler(UserAccessForbiddenException::class)
    fun handlerUserAccessForbiddenException(ex: UserAccessForbiddenException): ResponseEntity<ExceptionData> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(
                ExceptionData(
                    message = ex.message,
                    timestamp = LocalDateTime.now(),
                    exception = ex.javaClass.toString(),
                )
            )
    }
}
