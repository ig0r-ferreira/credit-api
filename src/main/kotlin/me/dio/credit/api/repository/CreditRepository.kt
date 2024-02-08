package me.dio.credit.api.repository

import me.dio.credit.api.entity.Credit
import org.springframework.data.jpa.repository.JpaRepository

interface CreditRepository: JpaRepository<Credit, Long>