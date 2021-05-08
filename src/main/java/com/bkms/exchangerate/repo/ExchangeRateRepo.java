package com.bkms.exchangerate.repo;

import com.bkms.exchangerate.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepo extends JpaRepository<ExchangeRate,String> {
    Optional<List<ExchangeRate>> findByTrxDateStartsWith(String filterationMonth);
}
