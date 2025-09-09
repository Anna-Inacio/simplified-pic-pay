package com.simplified_pic_pay.dtos;

import com.simplified_pic_pay.domain.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record UserDTO(
        @Schema(example = "Anna")
        String firstName,
        @Schema(example = "Banana")
        String lastName,
        @Schema(example = "12345678900")
        String document,
        @Schema(example = "anna.banana@gmail.com")
        String email,
        @Schema(example = "P@ssw0rd!")
        String password,
        @Schema(example = "1000.00")
        BigDecimal balance,
        @Schema(example = "COMMON")
        UserType userType) {
}
