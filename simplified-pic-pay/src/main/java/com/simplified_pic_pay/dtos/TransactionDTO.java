package com.simplified_pic_pay.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record TransactionDTO(
        @Schema(example = "100.00")
        BigDecimal amount,
        @Schema(example = "1")
        Long senderId,
        @Schema(example = "2")
        Long receiverId) {
}
