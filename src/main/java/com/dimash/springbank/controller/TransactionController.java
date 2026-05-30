package com.dimash.springbank.controller;

import com.dimash.springbank.dto.TransferRequest;
import com.dimash.springbank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public String transfer(@Valid @RequestBody TransferRequest request){
        transactionService.transfer(request);

        return "Transfer Successful!";
    }

}
