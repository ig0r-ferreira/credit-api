package me.dio.credit.api.exception

data class UserNotFoundException(override val message: String = "User not found.") : RuntimeException(message)