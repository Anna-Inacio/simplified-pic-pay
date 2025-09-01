package com.simplified_pic_pay.service.user;

import com.simplified_pic_pay.domain.user.User;
import com.simplified_pic_pay.domain.user.UserType;
import com.simplified_pic_pay.dtos.UserDTO;
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

    public void validateTransaction(User sender, BigDecimal amount) {
        validationType(sender);
        validationBalance(sender, amount);
    }

    public User createUser(UserDTO user) {
        var documentAlreadyExist = repository.findUserByDocument(user.document());

        if (documentAlreadyExist.isPresent()) {
            //TODO criar exception personalizada
            throw new IllegalArgumentException("Document already exists");
        }

        User newUser = new User(user);
        repository.save(newUser);
        return newUser;
    }

    private static void validationType(User sender) {
        if (sender.getUserType() == UserType.MERCHANT) {
            throw new OperationNotAllowedException("Merchants cannot send money");
        }
    }

    private static void validationBalance(User sender, BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }
}
