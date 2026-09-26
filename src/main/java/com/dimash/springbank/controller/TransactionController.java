package com.dimash.springbank.controller;

import com.dimash.springbank.dto.TransactionResponse;
import com.dimash.springbank.dto.TransferRequest;
import com.dimash.springbank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request, Principal principal){
        return transactionService.transfer(request, principal.getName());
    }

    @GetMapping
    public PagedModel<TransactionResponse> getMyTransactions(
            Principal principal,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return new PagedModel<>(transactionService.getMyTransactions(principal.getName(), pageable));
    }

}
