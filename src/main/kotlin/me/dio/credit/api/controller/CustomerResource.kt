package me.dio.credit.api.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import me.dio.credit.api.dto.CustomerDTO
import me.dio.credit.api.dto.CustomerUpdateDTO
import me.dio.credit.api.dto.CustomerView
import me.dio.credit.api.entity.Customer
import me.dio.credit.api.exception.ExceptionData
import me.dio.credit.api.service.impl.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/customers")
@Tag(name = "customers")
class CustomerResource(
    private val customerService: CustomerService
) {
    @PostMapping
    @ApiResponse(
        responseCode = "201", description = "Customer created successfully",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomerView::class))]
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid data for customer",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionData::class))]
    )
    @ApiResponse(
        responseCode = "409",
        description = "The customer already exists",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionData::class))]
    )
    fun create(@RequestBody @Valid customerDTO: CustomerDTO): ResponseEntity<CustomerView> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CustomerView(this.customerService.save(customerDTO.toEntity())))
    }

    @GetMapping("/{id}")
    @ApiResponse(
        responseCode = "200", description = "Customer found successfully",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomerView::class))]
    )
    @ApiResponse(
        responseCode = "404",
        description = "Customer not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionData::class))]
    )
    fun get(@PathVariable id: Long): ResponseEntity<Any> {
        val customer: Customer = this.customerService.findById(id)
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customer))
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Customer deleted successfully")
    @ApiResponse(
        responseCode = "404",
        description = "Customer not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionData::class))]
    )
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        this.customerService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}")
    @ApiResponse(
        responseCode = "200", description = "Customer updated successfully",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomerView::class))]
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid data for customer",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionData::class))]
    )
    fun update(@PathVariable id: Long, @RequestBody customerDTO: CustomerUpdateDTO): ResponseEntity<Any> {
        val customer: Customer = this.customerService.findById(id)
        val customerUpdated = this.customerService.save(customerDTO.toEntity(customer))
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customerUpdated))
    }
}