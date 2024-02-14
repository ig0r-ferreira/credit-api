package me.dio.credit.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.api.dto.CustomerDTO
import me.dio.credit.api.dto.CustomerUpdateDTO
import me.dio.credit.api.entity.Customer
import me.dio.credit.api.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    private fun buildCustomerDT0(
        firstName: String = "João",
        lastName: String = "Silva",
        cpf: String = "505.490.420-40",
        email: String = "joao@test.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Anywhere",
        income: BigDecimal = BigDecimal.valueOf(5000.0)
    ): CustomerDTO = CustomerDTO(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        income = income,
        zipCode = zipCode,
        street = street
    )

    private fun buildCustomerUpdateDT0(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        zipCode: String = "000000",
        street: String = "Rua da Cami, 123",
        income: BigDecimal = BigDecimal.valueOf(1000.0)
    ): CustomerUpdateDTO = CustomerUpdateDTO(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )

    @Test
    fun `create customer should return 201 status`() {
        val customerDTO: CustomerDTO = buildCustomerDT0()
        val dataAsString: String = objectMapper.writeValueAsString(customerDTO)

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(customerDTO.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(customerDTO.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value(customerDTO.cpf))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(customerDTO.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(customerDTO.income))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value(customerDTO.zipCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value(customerDTO.street))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `create customer should return 409 status when there is already a customer with the cpf entered`() {
        val customerDTO: CustomerDTO = buildCustomerDT0()
        customerRepository.save(customerDTO.toEntity())

        val dataAsString: String = objectMapper.writeValueAsString(customerDTO)

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User conflict"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `create customer should return 400 status when firstName field is empty`() {
        val customerDto: CustomerDTO = buildCustomerDT0(firstName = "")
        val dataAsString: String = objectMapper.writeValueAsString(customerDto)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .content(dataAsString)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bad request"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `get customer by id should return 200 status`() {
        val customer: Customer = customerRepository.save(buildCustomerDT0().toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(customer.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(customer.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value(customer.cpf))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value(customer.address.zipcode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value(customer.address.street))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `get customer by id should return 404 status when customer not found`() {
        val invalidId = Random.nextLong()

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.api.exception.UserNotFoundException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `delete customer should return 204 status`() {
        val customer: Customer = customerRepository.save(buildCustomerDT0().toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `delete customer should return 404 status when customer not found`() {
        val invalidId = Random.nextLong()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${invalidId}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.api.exception.UserNotFoundException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `update customer should return 200 status`() {
        val customer: Customer = customerRepository.save(buildCustomerDT0().toEntity())
        val customerUpdateDTO: CustomerUpdateDTO = buildCustomerUpdateDT0()
        val dataAsString: String = objectMapper.writeValueAsString(customerUpdateDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL/${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(customerUpdateDTO.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(customerUpdateDTO.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(customerUpdateDTO.income))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value(customerUpdateDTO.zipCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value(customerUpdateDTO.street))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `update customer should return 404 status when customer not found`() {
        val invalidId: Long = Random.nextLong()
        val customerUpdateDto: CustomerUpdateDTO = buildCustomerUpdateDT0()
        val dataAsString: String = objectMapper.writeValueAsString(customerUpdateDto)

        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL/${invalidId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.api.exception.UserNotFoundException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isEmpty)
            .andDo(MockMvcResultHandlers.print())
    }
}