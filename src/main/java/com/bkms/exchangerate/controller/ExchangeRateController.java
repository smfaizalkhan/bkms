package com.bkms.exchangerate.controller;

import com.bkms.exchangerate.model.ExchangeRate;
import com.bkms.exchangerate.model.ExchangeRateDTO;
import com.bkms.exchangerate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;

@RestController
@RequestMapping("/api/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("{date}/{baseCurrency}/{targetCurrency}")
    public ExchangeRateDTO getExchangeRateDetails(@PathVariable("date") String date,
                                                  @PathVariable("baseCurrency") String baseCurrency,
                                                  @PathVariable("targetCurrency") String targetCurrency){
       if(LocalDate.parse(date).isAfter(LocalDate.now()) || LocalDate.parse(date).isBefore(LocalDate.of(2000,01,01)))
           throw new InputMismatchException(String.format("Date %s is not between 2000-01-01 and yesterday",date));
      return exchangeRateService.getExchangeRateDetails(date,baseCurrency,targetCurrency);
    }

    @GetMapping("/history/daily/{yyyy}/{MM}/{dd}")
    public ExchangeRateDTO getDailyExchangeRates(@PathVariable("yyyy")String year,
                                                    @PathVariable("MM")String month,
                                                    @PathVariable("dd")String date){
        return exchangeRateService.getDailyExchangeRates(year,month,date);
    }

    @GetMapping("/history/monthly/{yyyy}/{MM}")
    public List<ExchangeRateDTO> getDailyExchangeRates(@PathVariable("yyyy")String year,
                                                    @PathVariable("MM")String month){
        return exchangeRateService.getMonthlyExchangeRates(year,month);
    }
}
