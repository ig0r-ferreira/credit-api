package me.dio.credit.api.controller

import jakarta.validation.Valid
import me.dio.credit.api.dto.CustomerDTO
import me.dio.credit.api.dto.CustomerUpdateDTO
import me.dio.credit.api.dto.CustomerView
import me.dio.credit.api.entity.Customer
import me.dio.credit.api.service.impl.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/customers")
class CustomerResource(
    private val customerService: CustomerService
) {
    @PostMapping
    fun create(@RequestBody @Valid customerDTO: CustomerDTO): ResponseEntity<String> {
        val customerEmail = this.customerService.save(customerDTO.toEntity()).email
        return ResponseEntity.status(HttpStatus.CREATED).body("Customer $customerEmail saved!")
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<Any> {
        val customer: Customer = this.customerService.findById(id)
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customer))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        this.customerService.delete(id)
    }

    @PatchMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody customerDTO: CustomerUpdateDTO): ResponseEntity<Any> {
        val customer: Customer = this.customerService.findById(id)
        val customerUpdated = this.customerService.save(customerDTO.toEntity(customer))
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customerUpdated))
    }
}