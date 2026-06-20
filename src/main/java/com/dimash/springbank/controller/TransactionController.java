package com.dimash.springbank.controller;

import com.dimash.springbank.dto.TransactionResponse;
import com.dimash.springbank.dto.TransferRequest;
import com.dimash.springbank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    @GetMapping
    public List<TransactionResponse> getMyTransactions(Principal principal){
        return transactionService.getMyTransactions(principal.getName());
    }


}
