package com.simplified_pic_pay.dtos;

import com.simplified_pic_pay.domain.user.UserType;

import java.math.BigDecimal;

public record UserDTO (String firstName,
                       String lastName,
                       String document,
                       String email,
                       String password,
                       BigDecimal balance,
                       UserType userType) {
}
