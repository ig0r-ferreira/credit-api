package me.dio.credit.api.repository

import me.dio.credit.api.entity.Address
import me.dio.credit.api.entity.Credit
import me.dio.credit.api.entity.Customer
import me.dio.credit.api.service.CustomerServiceTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {
    @Autowired
    lateinit var creditRepository: CreditRepository

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var credit1: Credit
    private lateinit var credit2: Credit

    private fun buildCustomer(
        firstName: String = "João",
        lastName: String = "Silva",
        cpf: String = "505.490.420-40",
        email: String = "joao@test.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Anywhere",
        income: BigDecimal = BigDecimal.valueOf(5000.0)
    ): Customer = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(zipCode, street),
        income = income
    )

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

    @BeforeEach
    fun setup() {
        customer = testEntityManager.persist(buildCustomer())
        credit1 = testEntityManager.persist(buildCredit(customer = customer))
        credit2 = testEntityManager.persist(buildCredit(customer = customer))
    }

    @Test
    fun `findByCreditCode should return credit for a valid credit code`() {
        val fakeCredit1: Credit = creditRepository.findByCreditCode(credit1.creditCode)!!
        val fakeCredit2: Credit = creditRepository.findByCreditCode(credit2.creditCode)!!

        Assertions.assertThat(fakeCredit1).isSameAs(credit1)
        Assertions.assertThat(fakeCredit2).isSameAs(credit2)
    }

    @Test
    fun `findAllByCustomerId should return all credits for a valid customer id`() {
        val credits: List<Credit> = creditRepository.findAllByCustomerId(customer.id!!)

        Assertions.assertThat(credits.size).isEqualTo(2)
        Assertions.assertThat(credits).contains(credit1, credit2)
    }
}