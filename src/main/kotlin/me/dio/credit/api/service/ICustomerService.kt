package me.dio.credit.api.service

import me.dio.credit.api.entity.Customer

interface ICustomerService {
    fun save(customer: Customer): Customer
    fun findById(id: Long): Customer
    fun delete(id: Long): Customer
}