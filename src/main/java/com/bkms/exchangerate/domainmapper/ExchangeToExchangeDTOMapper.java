package com.bkms.exchangerate.domainmapper;

import com.bkms.exchangerate.model.ExchangeRate;
import com.bkms.exchangerate.model.ExchangeRateDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
public class ExchangeToExchangeDTOMapper implements Function<ExchangeRate, ExchangeRateDTO> {

    @Override
    public ExchangeRateDTO apply(ExchangeRate exchangeRate) {
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        exchangeRateDTO.setTrxDate(exchangeRate.getTrxDate());
        exchangeRateDTO.setBaseCurrency(exchangeRate.getBaseCurrency());
        exchangeRateDTO.setTargetCurrency(exchangeRate.getTargetCurrency());
        exchangeRateDTO.setExchangeRate(exchangeRate.getExchangeRate());
        if(!Objects.isNull(exchangeRate.getFiveDayAvg()))exchangeRateDTO.setFiveDayAvg(exchangeRate.getFiveDayAvg());
        if(!Objects.isNull(exchangeRate.getTrend()))exchangeRateDTO.setTrend(exchangeRate.getTrend());
        return exchangeRateDTO;
    }
}
