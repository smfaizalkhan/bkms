package com.bkms.exchangerate.model;

import com.bkms.exchangerate.model.Trend;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExchangeRateDTO {
    private String  trxDate;
    private String exchangeRate;
    private String baseCurrency;
    private String targetCurrency;
    private String fiveDayAvg;
    private Trend trend;
}
