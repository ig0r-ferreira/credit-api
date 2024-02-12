package me.dio.credit.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit.api.entity.Address
import me.dio.credit.api.entity.Customer
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDTO(
    @field:NotEmpty(message = "firstName required") val firstName: String,
    @field:NotEmpty(message = "lastName required") val lastName: String,
    @field:NotEmpty(message = "cpf required")
    @field:CPF(message = "invalid CPF")
    val cpf: String,
    @field:NotNull(message = "income required")
    val income: BigDecimal,
    @field:NotEmpty(message = "email required")
    @field:Email(message = "invalid email")
    val email: String,
    @field:NotEmpty(message = "password required") val password: String,
    @field:NotEmpty(message = "zipCode required") val zipCode: String,
    @field:NotEmpty(message = "street required") val street: String
) {
    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        income = this.income,
        address = Address(this.zipCode, this.street)
    )
}
