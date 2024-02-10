package me.dio.credit.api.dto

import me.dio.credit.api.entity.Credit
import me.dio.credit.api.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDTO (
    val creditValue: BigDecimal,
    val dayFirstInstallment: LocalDate,
    val numberOfInstallments: Int,
    val customerId: Long
) {
    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}

