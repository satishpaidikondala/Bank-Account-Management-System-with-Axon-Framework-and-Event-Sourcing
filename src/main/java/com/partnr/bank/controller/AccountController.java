package com.partnr.bank.controller;

import com.partnr.bank.command.CloseAccountCommand;
import com.partnr.bank.command.CreateAccountCommand;
import com.partnr.bank.command.DepositMoneyCommand;
import com.partnr.bank.command.WithdrawMoneyCommand;
import com.partnr.bank.entity.CurrentAccountView;
import com.partnr.bank.exception.AccountNotFoundException;
import com.partnr.bank.repository.CurrentAccountViewRepository;
import com.partnr.bank.service.AccountEventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final CommandGateway commandGateway;
    private final CurrentAccountViewRepository accountViewRepository;
    private final AccountEventService accountEventService;

    public AccountController(CommandGateway commandGateway,
                             CurrentAccountViewRepository accountViewRepository,
                             AccountEventService accountEventService) {
        this.commandGateway = commandGateway;
        this.accountViewRepository = accountViewRepository;
        this.accountEventService = accountEventService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        String accountId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new CreateAccountCommand(accountId, request.ownerName(), request.initialBalance()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/accounts/" + accountId)
                .body(Map.of("accountId", accountId));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Map<String, Object>> deposit(@PathVariable String accountId,
                                                        @Valid @RequestBody AmountRequest request) {
        Map<String, Object> result = commandGateway.sendAndWait(
            new DepositMoneyCommand(accountId, request.amount()));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(@PathVariable String accountId,
                                                         @Valid @RequestBody AmountRequest request) {
        Map<String, Object> result = commandGateway.sendAndWait(
            new WithdrawMoneyCommand(accountId, request.amount()));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{accountId}/close")
    public ResponseEntity<Void> close(@PathVariable String accountId) {
        commandGateway.sendAndWait(new CloseAccountCommand(accountId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<CurrentAccountView> getAccount(@PathVariable String accountId) {
        return accountViewRepository.findById(accountId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @GetMapping("/{accountId}/events")
    public ResponseEntity<List<Map<String, Object>>> getEvents(@PathVariable String accountId) {
        List<Map<String, Object>> events = accountEventService.getEventsForAccount(accountId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{accountId}/balance-at/{timestamp}")
    public ResponseEntity<Map<String, Object>> getBalanceAt(
            @PathVariable String accountId,
            @PathVariable String timestamp) {
        Instant ts = Instant.parse(timestamp);
        double balance = accountEventService.getBalanceAtTimestamp(accountId, ts);
        return ResponseEntity.ok(Map.of(
                "accountId", accountId,
                "balanceAsOf", timestamp,
                "balance", balance
        ));
    }

    public record CreateAccountRequest(
            @Positive double initialBalance,
            @NotBlank String ownerName) {}

    public record AmountRequest(@Positive double amount) {}
}
