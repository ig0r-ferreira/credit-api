package me.dio.credit.api.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import me.dio.credit.api.entity.Credit
import me.dio.credit.api.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDTO (
    @field:NotNull(message = "creditValue required") val creditValue: BigDecimal,
    @field:Future val dayFirstInstallment: LocalDate,
    @field:Min(value = 1) @field:Max(value = 48) val numberOfInstallments: Int,
    @field:NotNull(message = "customerId required") val customerId: Long
) {
    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}

