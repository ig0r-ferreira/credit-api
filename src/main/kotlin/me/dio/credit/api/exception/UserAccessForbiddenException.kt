package me.dio.credit.api.exception

data class UserAccessForbiddenException(override val message: String = "User access forbidden.") : RuntimeException(message)