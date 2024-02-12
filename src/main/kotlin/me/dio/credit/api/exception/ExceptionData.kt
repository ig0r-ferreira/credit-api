package me.dio.credit.api.exception

import java.time.LocalDateTime

data class ExceptionData(
    val message: String,
    val timestamp: LocalDateTime,
    val exception: String,
    val details: List<Any?>? = null
)
