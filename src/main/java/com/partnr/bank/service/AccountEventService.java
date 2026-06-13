package com.partnr.bank.service;

import com.partnr.bank.event.AccountCreatedEvent;
import com.partnr.bank.event.MoneyDepositedEvent;
import com.partnr.bank.event.MoneyWithdrawnEvent;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountEventService {

    private final EventStore eventStore;

    public AccountEventService(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public List<Map<String, Object>> getEventsForAccount(String accountId) {
        List<Map<String, Object>> result = new ArrayList<>();
        var stream = eventStore.readEvents(accountId);
        while (stream.hasNext()) {
            var event = stream.next();
            Map<String, Object> entry = new LinkedHashMap<>();
            Object payload = event.getPayload();
            entry.put("type", payload.getClass().getSimpleName());
            entry.put("payload", payload);
            result.add(entry);
        }
        return result;
    }

    public double getBalanceAtTimestamp(String accountId, Instant timestamp) {
        BalanceReconstructor reconstructor = new BalanceReconstructor();
        var stream = eventStore.readEvents(accountId);
        while (stream.hasNext()) {
            var event = stream.next();
            if (!event.getTimestamp().isAfter(timestamp)) {
                reconstructor.handle(event.getPayload());
            }
        }
        return reconstructor.getBalance();
    }

    private static class BalanceReconstructor {
        private double balance = 0;

        public void handle(Object event) {
            if (event instanceof AccountCreatedEvent) {
                balance = ((AccountCreatedEvent) event).getInitialBalance();
            } else if (event instanceof MoneyDepositedEvent) {
                balance = ((MoneyDepositedEvent) event).getBalance();
            } else if (event instanceof MoneyWithdrawnEvent) {
                balance = ((MoneyWithdrawnEvent) event).getBalance();
            }
        }

        public double getBalance() {
            return balance;
        }
    }
}
