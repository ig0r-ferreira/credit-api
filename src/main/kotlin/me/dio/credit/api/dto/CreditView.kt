package me.dio.credit.api.dto

import me.dio.credit.api.entity.Credit
import java.math.BigDecimal
import java.util.UUID

data class CreditView (
    val creditCode: UUID,
    val creditValue: BigDecimal,
    val numberOfInstallments: Int
) {
    constructor(credit: Credit): this (
        creditCode = credit.creditCode,
        creditValue = credit.creditValue,
        numberOfInstallments = credit.numberOfInstallments
    )
}
