package com.bkms.exchangerate.factory;

import com.bkms.exchangerate.model.ExchangeRate;
import com.bkms.exchangerate.model.ExchangeRateDTO;
import com.bkms.exchangerate.model.Trend;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DomainFactory {

    public static ExchangeRate createExchangeRate() {
        String date = "2021-05-05";
        String baseCurrency="USD";
        String targetCurrency="EUR";
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setTrxDate(date);
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        return exchangeRate;
    }

    public static List<ExchangeRate> createExchangeRateList(){
        String date = "2021-05-05";
        String baseCurrency="USD";
        String targetCurrency="EUR";
        ExchangeRate exchangeRate = new ExchangeRate();
        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        exchangeRate.setTrxDate(date);
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRateList.add(exchangeRate);
        return exchangeRateList;
    }

    public static JSONObject mockedResponseObject(){
        String date = "2021-05-05";
        String baseCurrency="USD";
        String targetCurrency="EUR";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("base",baseCurrency);
        jsonObject.put("success",true);
        jsonObject.put("date",date);
        JSONObject rateObject = new JSONObject();
        rateObject.put(targetCurrency,Double.valueOf("0.832899"));
        jsonObject.put("rates",rateObject);
        return jsonObject;
    }

    public static JSONObject mockedFailureInfo() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        JSONObject errorObject = new JSONObject();
        errorObject.put("code",102);
        errorObject.put("type","invalid_currency_codes");
        errorObject.put("info","You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]");
        jsonObject.put("error",errorObject);
        return jsonObject;
    }

    public static ExchangeRateDTO createExchangeRateDTO() {
        String date = "2021-05-05";
        String baseCurrency="USD";
        String targetCurrency="EUR";
        String fiveDayAvg = "0.832899";
        String exchangeRate = "0.832899";
        Trend trend = Trend.CONSTANT;
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        exchangeRateDTO.setTrxDate(date);
        exchangeRateDTO.setTrend(trend);
        exchangeRateDTO.setFiveDayAvg(fiveDayAvg);
        exchangeRateDTO.setExchangeRate(exchangeRate);
        exchangeRateDTO.setBaseCurrency(baseCurrency);
        exchangeRateDTO.setTargetCurrency(targetCurrency);
        return exchangeRateDTO;
    }

    public static ExchangeRateDTO createExchangeRateForDaily() {
        String date = "2021-05-05";
        String baseCurrency="USD";
        String targetCurrency="EUR";
        String exchangeRate = "0.832899";
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        exchangeRateDTO.setTrxDate(date);
        exchangeRateDTO.setExchangeRate(exchangeRate);
        exchangeRateDTO.setBaseCurrency(baseCurrency);
        exchangeRateDTO.setTargetCurrency(targetCurrency);
        return exchangeRateDTO;
    }

    public static List<ExchangeRateDTO> createExchangeRateDTOList() {
        List<ExchangeRateDTO> exchangeRateDTOList = new ArrayList<>();
        exchangeRateDTOList.add(createExchangeRateDTO());
        return exchangeRateDTOList;
    }
}
