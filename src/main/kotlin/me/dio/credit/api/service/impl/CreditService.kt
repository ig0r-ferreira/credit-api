package me.dio.credit.api.service.impl

import me.dio.credit.api.entity.Credit
import me.dio.credit.api.exception.CreditCodeNotFoundException
import me.dio.credit.api.exception.InvalidDateException
import me.dio.credit.api.exception.UserAccessForbiddenException
import me.dio.credit.api.repository.CreditRepository
import me.dio.credit.api.service.ICreditService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
) : ICreditService {
    override fun save(credit: Credit): Credit {
        this.validDayFirstInstallment(credit.dayFirstInstallment)
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomerId(customerId: Long): List<Credit> =
        this.creditRepository.findAllByCustomerId(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit: Credit = this.creditRepository.findByCreditCode(creditCode)
            ?: throw CreditCodeNotFoundException()

        if (credit.customer?.id != customerId) {
            throw UserAccessForbiddenException()
        }

        return credit
    }

    private fun validDayFirstInstallment(dayFirstInstallment: LocalDate): Boolean {
        if (dayFirstInstallment.isAfter(LocalDate.now().plusMonths(3))) {
            throw InvalidDateException("The date is greater than 3 months from now.")
        }
        return true
    }
}