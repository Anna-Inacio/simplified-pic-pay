package com.simplified_pic_pay.dtos;

import java.math.BigDecimal;

public record TransactionDTO (BigDecimal amount, Long senderId, Long receiverId) {
}
