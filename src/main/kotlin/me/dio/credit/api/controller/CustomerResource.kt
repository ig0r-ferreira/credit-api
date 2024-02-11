package me.dio.credit.api.controller

import me.dio.credit.api.dto.CustomerDTO
import me.dio.credit.api.dto.CustomerUpdateDTO
import me.dio.credit.api.dto.CustomerView
import me.dio.credit.api.service.impl.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
class CustomerResource(
    private val customerService: CustomerService
) {
    @PostMapping
    fun create(@RequestBody customerDTO: CustomerDTO): ResponseEntity<String> {
        val customerEmail = this.customerService.save(customerDTO.toEntity()).email
        return ResponseEntity.status(HttpStatus.CREATED).body("Customer $customerEmail saved!")
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<CustomerView> {
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(this.customerService.findById(id)))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        this.customerService.delete(id)
    }

    @PatchMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody customer: CustomerUpdateDTO): ResponseEntity<CustomerView> {
        val customerUpdated = this.customerService.save(customer.toEntity(this.customerService.findById(id)))
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customerUpdated))
    }
}