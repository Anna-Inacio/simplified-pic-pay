package com.simplified_pic_pay.service;

import com.simplified_pic_pay.domain.user.User;
import com.simplified_pic_pay.domain.user.UserType;
import com.simplified_pic_pay.exception.InsufficientBalanceException;
import com.simplified_pic_pay.exception.OperationNotAllowedException;
import com.simplified_pic_pay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        validationType(sender);
        validationBalance(sender, amount);
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private static void validationBalance(User sender, BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }

    private static void validationType(User sender) {
        if (sender.getUserType() == UserType.MERCHANT){
            throw new OperationNotAllowedException("Merchants cannot send money");
        }
    }
}
