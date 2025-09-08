package com.simplified_pic_pay.controller;

import com.simplified_pic_pay.domain.transaction.Transaction;
import com.simplified_pic_pay.dtos.TransactionDTO;
import com.simplified_pic_pay.service.transaction.TransactionService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction", description = "Transaction manipulation endpoints")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created transaction"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Insufficient Balance", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Merchants cannot send money", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Transaction unauthorized", content = @Content()),
    })
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDTO transaction) {
        var newTransaction = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(newTransaction, HttpStatus.OK);
    }
}
