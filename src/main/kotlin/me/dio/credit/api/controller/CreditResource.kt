package me.dio.credit.api.controller

import me.dio.credit.api.dto.CreditDTO
import me.dio.credit.api.dto.CreditView
import me.dio.credit.api.entity.Credit
import me.dio.credit.api.service.impl.CreditService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
class CreditResource(
    private val creditService: CreditService
) {
    @PostMapping
    fun create(@RequestBody creditDTO: CreditDTO): String {
        val credit = this.creditService.save(creditDTO.toEntity())
        val customer = credit.customer
        return "Credit code: ${credit.creditCode} - Customer: ${customer?.firstName} ${customer?.lastName} saved!"
    }

    @GetMapping
    fun getAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): List<CreditView> {
        return this.creditService.findAllByCustomerId(customerId)
            .stream()
            .map { credit: Credit -> CreditView(credit) }
            .collect(Collectors.toList())
    }
}