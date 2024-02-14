package me.dio.credit.api.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import me.dio.credit.api.entity.Credit
import me.dio.credit.api.entity.Customer
import me.dio.credit.api.exception.CreditCodeNotFoundException
import me.dio.credit.api.exception.InvalidDateException
import me.dio.credit.api.exception.UserAccessForbiddenException
import me.dio.credit.api.repository.CreditRepository
import me.dio.credit.api.service.impl.CreditService
import me.dio.credit.api.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK
    lateinit var creditRepository: CreditRepository

    @MockK
    lateinit var customerService: CustomerService

    @InjectMockKs
    lateinit var creditService: CreditService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    companion object {
        private fun buildCredit(
            creditValue: BigDecimal = BigDecimal.valueOf(100.0),
            dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
            numberOfInstallments: Int = 15,
            customer: Customer = CustomerServiceTest.buildCustomer()
        ): Credit = Credit(
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
            numberOfInstallments = numberOfInstallments,
            customer = customer
        )
    }

    @Test
    fun `save should create credit successfully`() {
        val expectedCredit: Credit = buildCredit()
        val customerId: Long = expectedCredit.customer?.id!!

        every { customerService.findById(customerId) } returns expectedCredit.customer!!
        every { creditRepository.save(expectedCredit) } returns expectedCredit

        val credit: Credit = this.creditService.save(expectedCredit)

        Assertions.assertThat(credit).isSameAs(expectedCredit)
        verify(exactly = 1) { customerService.findById(customerId) }
        verify(exactly = 1) { creditRepository.save(expectedCredit) }
    }

    @Test
    fun `save should not create credit when the first installment date is invalid`() {
        val invalidDayFirstInstallment: LocalDate = LocalDate.now().plusMonths(5)
        val credit: Credit = buildCredit(dayFirstInstallment = invalidDayFirstInstallment)

        Assertions.assertThatThrownBy { creditService.save(credit) }
            .isInstanceOf(InvalidDateException::class.java)
            .hasMessage("The date is greater than 3 months from now.")
        verify(exactly = 0) { creditRepository.save(any()) }
        verify(exactly = 0) { customerService.findById(credit.customer?.id!!) }
    }

    @Test
    fun `findAllByCustomerId should return list of credits for a customer`() {
        val expectedCredits: List<Credit> = listOf(buildCredit(), buildCredit(), buildCredit())
        val customerId = expectedCredits[0].customer?.id!!

        every { creditRepository.findAllByCustomerId(customerId) } returns expectedCredits

        val credits: List<Credit> = creditService.findAllByCustomerId(customerId)

        Assertions.assertThat(credits).isSameAs(expectedCredits)
        verify(exactly = 1) { creditRepository.findAllByCustomerId(customerId) }
    }

    @Test
    fun `findByCreditCode should return credit for a valid customer and credit code`() {
        val customerId = 1L
        val creditCode: UUID = UUID.randomUUID()
        val expectedCredit: Credit = buildCredit(customer = Customer(id = customerId))

        every { creditRepository.findByCreditCode(creditCode) } returns expectedCredit

        val credit: Credit = creditService.findByCreditCode(customerId, creditCode)

        Assertions.assertThat(credit).isSameAs(expectedCredit)
        verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
    }

    @Test
    fun `throws CreditCodeNotFoundException when calling findByCreditCode with invalid credit code`() {
        val customerId = 1L
        val invalidCreditCode: UUID = UUID.randomUUID()

        every { creditRepository.findByCreditCode(invalidCreditCode) } returns null

        Assertions.assertThatThrownBy { creditService.findByCreditCode(customerId, invalidCreditCode) }
            .isInstanceOf(CreditCodeNotFoundException::class.java)
            .hasMessage("Credit code not found.")

        verify(exactly = 1) { creditRepository.findByCreditCode(invalidCreditCode) }
    }

    @Test
    fun `throws UserAccessForbiddenException when calling findByCreditCode with different customer ID`() {
        val customerId = 1L
        val creditCode: UUID = UUID.randomUUID()
        val credit: Credit = buildCredit(customer = Customer(id = 2L))

        every { creditRepository.findByCreditCode(creditCode) } returns credit

        Assertions.assertThatThrownBy { creditService.findByCreditCode(customerId, creditCode) }
            .isInstanceOf(UserAccessForbiddenException::class.java)
            .hasMessage("User access forbidden.")

        verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
    }
}