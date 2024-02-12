package me.dio.credit.api.dto

import me.dio.credit.api.entity.Customer
import java.math.BigDecimal

data class CustomerUpdateDTO(
    val firstName: String? = null,
    val lastName: String? = null,
    val income: BigDecimal? = null,
    val zipCode: String? = null,
    val street: String? = null
) {
    fun toEntity(customer: Customer): Customer {
        this.firstName?.let {
            customer.firstName = it
        }
        this.lastName?.let {
            customer.lastName = it
        }
        this.income?.let {
            customer.income = it
        }
        this.zipCode?.let {
            customer.address.zipcode = it
        }
        this.street?.let {
            customer.address.street = it
        }
        return customer
    }
}
