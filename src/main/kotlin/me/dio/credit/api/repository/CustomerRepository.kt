package me.dio.credit.api.repository

import me.dio.credit.api.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository: JpaRepository<Customer, Long>