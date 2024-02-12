package me.dio.credit.api.service.impl

import me.dio.credit.api.entity.Customer
import me.dio.credit.api.exception.UserNotFoundException
import me.dio.credit.api.repository.CustomerRepository
import me.dio.credit.api.service.ICustomerService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
) : ICustomerService {
    override fun save(customer: Customer): Customer = this.customerRepository.save(customer)

    override fun findById(id: Long): Customer = this.customerRepository.findById(id)
        .orElseThrow { throw UserNotFoundException() }


    override fun delete(id: Long) = this.customerRepository.delete(this.findById(id))
}