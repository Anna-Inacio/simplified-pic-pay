package com.simplified_pic_pay;

import com.simplified_pic_pay.domain.transaction.Transaction;
import com.simplified_pic_pay.domain.user.User;
import com.simplified_pic_pay.dtos.TransactionDTO;
import com.simplified_pic_pay.exception.InsufficientBalanceException;
import com.simplified_pic_pay.exception.OperationNotAllowedException;
import com.simplified_pic_pay.exception.UserNotFoundException;
import com.simplified_pic_pay.repository.TransactionRepository;
import com.simplified_pic_pay.repository.UserRepository;
import com.simplified_pic_pay.service.transaction.AuthorizationTransaction;
import com.simplified_pic_pay.service.transaction.TransactionService;
import com.simplified_pic_pay.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthorizationTransaction authorizationTransaction;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = newTransactionUser(1L, BigDecimal.valueOf(100));
        receiver = newTransactionUser(2L, BigDecimal.valueOf(50));
    }

    @Test
    void shouldSaveAndUpdateUsersBalanceWhenTransactionAuthorized() {
        Transaction transaction = new Transaction();

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserById(2L)).thenReturn(Optional.of(receiver));

        doNothing().when(userService).validateTransaction(sender, BigDecimal.valueOf(30));
        when(authorizationTransaction.authorizeTransaction(sender, BigDecimal.valueOf(30))).thenReturn(true);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(userRepository.save(sender)).thenReturn(sender);
        when(userRepository.save(receiver)).thenReturn(receiver);

        Transaction result = transactionService.createTransaction(newTransactionDTO());

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(70), sender.getBalance());
        assertEquals(BigDecimal.valueOf(80), receiver.getBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(userRepository, times(1)).save(sender);
        verify(userRepository, times(1)).save(receiver);
    }

    @Test
    void shouldReturnExceptionWhenSenderUserTypeIsMerchant() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserById(2L)).thenReturn(Optional.of(receiver));

        doThrow(new OperationNotAllowedException("Merchants cannot send money")).when(userService).validateTransaction(sender, BigDecimal.valueOf(30));
        OperationNotAllowedException ex = assertThrows(OperationNotAllowedException.class, () -> {
            transactionService.createTransaction(newTransactionDTO());
        });

        assertEquals("Merchants cannot send money", ex.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenSenderUserNotFound() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            transactionService.createTransaction(newTransactionDTO());
        });

        assertEquals("Sender not found", ex.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenReceiverUserNotFound() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserById(2L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            transactionService.createTransaction(newTransactionDTO());
        });

        assertEquals("Receiver not found", ex.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenInsufficientBalance() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserById(2L)).thenReturn(Optional.of(receiver));

        doThrow(new InsufficientBalanceException("Insufficient balance")).when(userService).validateTransaction(sender, BigDecimal.valueOf(30));
        InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.createTransaction(newTransactionDTO());
        });

        assertEquals("Insufficient balance", ex.getMessage());
    }

    @Test
    void shouldReturnExceptionWhenTransactionUnauthorized() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserById(2L)).thenReturn(Optional.of(receiver));
        doNothing().when(userService).validateTransaction(sender, BigDecimal.valueOf(30));
        when(authorizationTransaction.authorizeTransaction(sender, BigDecimal.valueOf(30))).thenReturn(false);

        Transaction result = transactionService.createTransaction(newTransactionDTO());

        assertNull(result);
    }

    private TransactionDTO newTransactionDTO() {
        return new TransactionDTO(
                BigDecimal.valueOf(30),
                1L,
                2L);
    }

    private User newTransactionUser(Long id, BigDecimal amount) {
        User user = new User();
        user.setId(id);
        user.setBalance(amount);
        return user;
    }
}
