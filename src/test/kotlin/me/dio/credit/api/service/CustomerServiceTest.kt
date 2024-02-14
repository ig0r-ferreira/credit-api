package me.dio.credit.api.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.credit.api.entity.Address
import me.dio.credit.api.entity.Customer
import me.dio.credit.api.exception.UserNotFoundException
import me.dio.credit.api.repository.CustomerRepository
import me.dio.credit.api.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
    @MockK
    lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    lateinit var customerService: CustomerService

    companion object {
        fun buildCustomer(
            id: Long = 1L,
            firstName: String = "João",
            lastName: String = "Silva",
            cpf: String = "505.490.420-40",
            email: String = "joao@test.com",
            password: String = "12345",
            zipCode: String = "12345",
            street: String = "Anywhere",
            income: BigDecimal = BigDecimal.valueOf(5000.0)
        ): Customer = Customer(
            id = id,
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(zipCode, street),
            income = income
        )
    }

    @Test
    fun `save should create customer successfully`() {
        val fakeCustomer: Customer = buildCustomer()
        every { customerRepository.save(any()) } returns fakeCustomer

        val actual: Customer = customerService.save(fakeCustomer)

        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerService.save(fakeCustomer) }
    }

    @Test
    fun `findById should return a customer for a valid id`() {
        val fakeId: Long = Random.nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)

        val actual: Customer = customerService.findById(fakeId)

        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `throws UserNotFoundException when find customer by invalid id`() {
        val fakeId: Long = Random.nextLong()
        every { customerRepository.findById(fakeId) } returns Optional.empty()

        Assertions.assertThatExceptionOfType(UserNotFoundException::class.java)
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("User not found.")
    }

    @Test
    fun `delete should succeed for a valid id`() {
        val fakeId: Long = Random.nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        every { customerRepository.delete(fakeCustomer) } just runs

        customerService.delete(fakeId)

        verify(exactly = 1) { customerRepository.findById(fakeId) }
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }

    @Test
    fun `throws UserNotFoundException when delete customer by invalid ID`() {
        val fakeId: Long = Random.nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.empty()

        Assertions.assertThatExceptionOfType(UserNotFoundException::class.java)
            .isThrownBy { customerService.delete(fakeId) }
            .withMessage("User not found.")
        verify(exactly = 1) { customerRepository.findById(fakeId) }
        verify(exactly = 0) { customerRepository.delete(fakeCustomer) }
    }
}