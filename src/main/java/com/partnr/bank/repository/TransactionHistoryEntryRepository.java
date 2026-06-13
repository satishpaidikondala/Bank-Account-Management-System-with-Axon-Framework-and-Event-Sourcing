package com.partnr.bank.repository;

import com.partnr.bank.entity.TransactionHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryEntryRepository extends JpaRepository<TransactionHistoryEntry, Long> {
    List<TransactionHistoryEntry> findByAccountIdOrderByTimestampAsc(String accountId);
}
