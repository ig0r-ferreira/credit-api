package me.dio.credit.api.exception

class InvalidDateException(override val message: String = "Invalid date."): RuntimeException(message)