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
        var sender = userRepository.findUserById(transaction.senderId())
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));
        var receiver = userRepository.findUserById(transaction.receiverId())
                .orElseThrow(() -> new UserNotFoundException("Receiver not found"));

        userService.validateTransaction(sender, transaction.amount());

        //TODO Criar feature toggle
        var isAuthorized = authorizationTransaction.authorizeTransaction(sender, transaction.amount());

        Transaction savedTransaction = null;
        if (isAuthorized) {
            savedTransaction = transactionRepository.save(processTransaction(transaction, sender, receiver));
            userRepository.save(sender);
            userRepository.save(receiver);
            //TODO Criar feature toggle
//            notificationTransaction.notifyTransaction(sender, "Transaction completed successfully");
//            notificationTransaction.notifyTransaction(receiver, "You have received a new transaction");
        }
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
