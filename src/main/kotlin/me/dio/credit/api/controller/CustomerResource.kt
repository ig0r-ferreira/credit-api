package me.dio.credit.api.controller

import me.dio.credit.api.dto.CustomerDTO
import me.dio.credit.api.dto.CustomerUpdateDTO
import me.dio.credit.api.dto.CustomerView
import me.dio.credit.api.service.impl.CustomerService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers")
class CustomerResource (
    private val customerService: CustomerService
) {
    @PostMapping
    fun saveCustomer(@RequestBody customerDTO: CustomerDTO): String {
        val customerEmail = this.customerService.save(customerDTO.toEntity()).email
        return "Customer $customerEmail saved!"
    }

    @GetMapping("/{id}")
    fun getCustomer(@PathVariable id: Long): CustomerView {
        return CustomerView(this.customerService.findById(id))
    }

    @DeleteMapping("/{id}")
    fun deleteCustomer(@PathVariable id: Long) = this.customerService.delete(id)

    @PatchMapping("/{id}")
    fun updateCustomer(@PathVariable id: Long, @RequestBody customer: CustomerUpdateDTO): CustomerView {
        return CustomerView(this.customerService.save(customer.toEntity(this.customerService.findById(id))))
    }
}