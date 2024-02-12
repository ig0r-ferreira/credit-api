package me.dio.credit.api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class RestExceptionHandler {
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