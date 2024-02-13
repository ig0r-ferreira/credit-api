package me.dio.credit.api.controller

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import me.dio.credit.api.dto.CreditDTO
import me.dio.credit.api.dto.CreditView
import me.dio.credit.api.dto.CustomerView
import me.dio.credit.api.entity.Credit
import me.dio.credit.api.exception.ExceptionData
import me.dio.credit.api.service.impl.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
@Tag(name = "credits")
class CreditResource(
    private val creditService: CreditService
) {
    @PostMapping
    @ApiResponse(
        responseCode = "201", description = "Credit created successfully",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = CreditView::class))]
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid data for credit",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionData::class))]
    )
    fun create(@RequestBody @Valid creditDTO: CreditDTO): ResponseEntity<CreditView> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CreditView(this.creditService.save(creditDTO.toEntity())))
    }

    @GetMapping
    @ApiResponse(
        responseCode = "200", description = "Get all customer credits successfully",
        content = [
            Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = CreditView::class))
            )
        ]
    )
    fun getAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<CreditView>> {
        val creditViewList: List<CreditView> = this.creditService.findAllByCustomerId(customerId)
            .stream()
            .map { credit: Credit -> CreditView(credit) }
            .collect(Collectors.toList())

        return ResponseEntity.status(HttpStatus.OK).body(creditViewList)
    }

    @GetMapping("/{creditCode}")
    @ApiResponse(
        responseCode = "200", description = "Get credit by code successfully",
        content = [
            Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = CreditView::class))
            )
        ]
    )
    @ApiResponse(
        responseCode = "404",
        description = "Credit not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionData::class))]
    )
    @ApiResponse(
        responseCode = "403",
        description = "Improper user access",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionData::class))]
    )
    fun getByCreditCode(
        @RequestParam(value = "customerId") customerId: Long,
        @PathVariable creditCode: UUID
    ): ResponseEntity<CreditView> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CreditView(this.creditService.findByCreditCode(customerId, creditCode)))
    }
}