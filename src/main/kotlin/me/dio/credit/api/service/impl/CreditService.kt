package me.dio.credit.api.service.impl

import me.dio.credit.api.entity.Credit
import me.dio.credit.api.repository.CreditRepository
import me.dio.credit.api.service.ICreditService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreditService (
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): ICreditService {
    override fun save(credit: Credit): Credit {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findByCustomerId(customerId: Long): List<Credit> =
        this.creditRepository.findAllByCustomerId(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit: Credit = this.creditRepository.findByCreditCode(creditCode)
            ?: throw RuntimeException("CreditCode $creditCode not found.")

        if (credit.customer?.id != customerId) {
            throw RuntimeException("Unauthorized access. Contact the admin.")
        }

        return credit
    }
}