package com.partnr.bank.repository;

import com.partnr.bank.entity.CurrentAccountView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentAccountViewRepository extends JpaRepository<CurrentAccountView, String> {
}
