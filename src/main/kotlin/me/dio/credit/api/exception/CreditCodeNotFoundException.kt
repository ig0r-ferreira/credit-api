package me.dio.credit.api.exception

data class CreditCodeNotFoundException(override val message: String = "Credit code not found.") :
    RuntimeException(message)