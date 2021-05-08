package com.bkms.exchangerate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class ExchangeRate {

    @Id
    private String  trxDate;
    private String exchangeRate;
    private String baseCurrency;
    private String targetCurrency;
    private String fiveDayAvg;
    @Enumerated(EnumType.STRING)
    private Trend trend;
}
