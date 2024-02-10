package me.dio.credit.api.dto

import me.dio.credit.api.entity.Customer
import java.math.BigDecimal

data class CustomerUpdateDTO (
    val firstName: String,
    val lastName: String,
    val income: BigDecimal,
    val zipCode: String,
    val street: String
) {
    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipcode = this.zipCode
        customer.address.street = this.street
        return customer
    }
}
