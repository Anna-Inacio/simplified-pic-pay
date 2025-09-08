package com.simplified_pic_pay.service.transaction;

import com.simplified_pic_pay.domain.transaction.Transaction;
import com.simplified_pic_pay.domain.user.User;
import com.simplified_pic_pay.dtos.TransactionDTO;
import com.simplified_pic_pay.exception.UserNotFoundException;
import com.simplified_pic_pay.repository.TransactionRepository;
import com.simplified_pic_pay.repository.UserRepository;
import com.simplified_pic_pay.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationTransaction authorizationTransaction;

    @Autowired
    private NotificationTransaction notificationTransaction;

    public Transaction createTransaction(TransactionDTO transaction) {
        var sender = getUserOrThrow(transaction.senderId(), "Sender not found");
        var receiver = getUserOrThrow(transaction.receiverId(), "Receiver not found");

        validateTransaction(sender, transaction.amount());

        var isAuthorized = authorizeTransaction(sender, transaction.amount());

        Transaction savedTransaction = null;
        if (isAuthorized) {
            savedTransaction = saveTransactionAndUpdateUsers(transaction, sender, receiver);
            //TODO Criar feature toggle
//            notifyUsers(sender, receiver);
        }
        return savedTransaction;
    }

    private User getUserOrThrow(Long userId, String errorMessage) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(errorMessage));
    }

    private void validateTransaction(User sender, BigDecimal amount) {
        userService.validateTransaction(sender, amount);
    }

    private boolean authorizeTransaction(User sender, BigDecimal amount) {
        return authorizationTransaction.authorizeTransaction(sender, amount);
    }

    private void notifyUsers(User sender, User receiver) {
        notificationTransaction.notifyTransaction(sender, "Transaction completed successfully");
        notificationTransaction.notifyTransaction(receiver, "You have received a new transaction");
    }

    private Transaction saveTransactionAndUpdateUsers(TransactionDTO transaction, User sender, User receiver) {
        Transaction savedTransaction = transactionRepository.save(processTransaction(transaction, sender, receiver));
        userRepository.save(sender);
        userRepository.save(receiver);
        return savedTransaction;
    }

   public Transaction processTransaction(TransactionDTO transactionDTO, User sender, User receiver) {
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transactionDTO.amount());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTransactionDate(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDTO.amount()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.amount()));
        return newTransaction;
    }
}
