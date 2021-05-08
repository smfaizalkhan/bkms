package com.bkms.exchangerate.service;

import com.bkms.exchangerate.domainmapper.ExchangeToExchangeDTOMapper;
import com.bkms.exchangerate.exception.ExternalSourceException;
import com.bkms.exchangerate.exception.RecordNotFoundException;
import com.bkms.exchangerate.model.ExchangeRate;
import com.bkms.exchangerate.model.ExchangeRateDTO;
import com.bkms.exchangerate.model.Trend;
import com.bkms.exchangerate.repo.ExchangeRateRepo;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.JsonPath.parse;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepo exchangeRateRepo;
    private final RestTemplate restTemplate;
    private final ExchangeToExchangeDTOMapper exchangeToExchangeDTOMapper;
    private static final String ACCESS_KEY = "5d5771804a397de5e6f16649a5b81c32";

    public ExchangeRateDTO getExchangeRateDetails(String date, String baseCurrency, String targetCurrency) {

        JSONObject responseBody = new JSONObject();
        ResponseEntity<JSONObject> response = restTemplate.getForEntity(
                "https://api.exchangeratesapi.io/" + date + "?access_key="+ACCESS_KEY+"&base=" + baseCurrency + "&symbols=" + targetCurrency, JSONObject.class);
        checkForExternalCallError(response);
        responseBody = response.getBody();
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(responseBody.getAsString("base"));
        exchangeRate.setTargetCurrency(targetCurrency);
        String rate = Double.toString(parse(response.getBody()).read("$.rates." + targetCurrency));
        exchangeRate.setExchangeRate(rate);

        JSONObject avgAndTrend = calculateAvgAndTrend(date,baseCurrency,targetCurrency);
        exchangeRate.setFiveDayAvg(avgAndTrend.getAsString("avg"));
        exchangeRate.setTrend(Trend.valueOf(avgAndTrend.getAsString("trend")));
        exchangeRate.setTrxDate(date);
        exchangeRateRepo.save(exchangeRate);
        return exchangeToExchangeDTOMapper.apply(exchangeRate);
    }

    public ExchangeRateDTO getDailyExchangeRates(String year, String month, String date) {
        ExchangeRate dailyExchangeRates ;
        final String filterationDate = LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(date)).toString();
        dailyExchangeRates =exchangeRateRepo.findById(filterationDate).orElseThrow(() -> new RecordNotFoundException(String.format("Record Not Found for date %s",filterationDate)));
        return exchangeToExchangeDTOMapper.apply(dailyExchangeRates);
    }

    public List<ExchangeRateDTO> getMonthlyExchangeRates(String year, String month) {
        List<ExchangeRate> monthlyExchangeRates = new ArrayList<>();
        List<ExchangeRateDTO> exchangeRateDTOList = new ArrayList<>();
        final String filterationMonth = year + "-" + month;
        monthlyExchangeRates = exchangeRateRepo.findByTrxDateStartsWith(filterationMonth)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Record Not Found for date %s", filterationMonth)));
        exchangeRateDTOList = monthlyExchangeRates.stream().map(exchangeRate -> exchangeToExchangeDTOMapper.apply(exchangeRate)).collect(Collectors.toList());
        return exchangeRateDTOList;
    }

    public JSONObject calculateAvgAndTrend(String date, String baseCurrency, String targetCurrency){
        JSONObject avgAndTrend = new JSONObject();
        Double total = new Double(0);
        Double trendIndicator = Double.MIN_VALUE;
        int count =0;
        for(int i=1;i<8;i++) {
            String startdate = LocalDate.parse(date).minusDays(i).toString();
            ResponseEntity<JSONObject> response = restTemplate.getForEntity(
                    "https://api.exchangeratesapi.io/" + startdate + "?access_key="+ACCESS_KEY+"&base=" + baseCurrency + "&symbols=" + targetCurrency, JSONObject.class);
            checkForExternalCallError(response);
            ExchangeRate exchangeRate = new ExchangeRate();
            if(!isWeekEnd(startdate)) {
                Double rate = parse(response.getBody()).read("$.rates." + targetCurrency);
                count = calculateTrendCount(trendIndicator, count, rate);
                exchangeRate.setExchangeRate(rate.toString());
                trendIndicator = rate;
                total = total + rate ;
            }

            exchangeRate.setBaseCurrency(response.getBody().getAsString("base"));
            exchangeRate.setTargetCurrency(targetCurrency);
            exchangeRate.setTrxDate(startdate);

            exchangeRateRepo.save(exchangeRate);
        }
        Trend trend = calculateTrend(count);
        avgAndTrend.put("trend",trend);
        avgAndTrend.put("avg",(total/5));
        return avgAndTrend;
    }

    private void checkForExternalCallError(ResponseEntity<JSONObject> response) {
        if (parse(response.getBody()).read("$.success").equals(false)) {
            String info = parse(response.getBody()).read("$.error.info");
            String code = parse(response.getBody()).read("$.error.code").toString();
            throw new ExternalSourceException(code, info);
        }
    }



    private int calculateTrendCount(Double trendIndicator, int count, Double rate) {
        if(rate > trendIndicator)
          ++count;
        else if(rate < trendIndicator)
            --count;
        return count;
    }

    private Trend calculateTrend(int count) {
        switch (count){
            case 5:
                return Trend.UP;
            case -5:
                return Trend.DOWN;
            case 1:
                return Trend.CONSTANT;
            default:
                return Trend.UNDEFINED;
        }
    }

    private boolean isWeekEnd(String startDate){
        LocalDate weekEndCheckDate = LocalDate.parse(startDate);
        return (weekEndCheckDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || weekEndCheckDate.getDayOfWeek().equals(DayOfWeek.SUNDAY));
    }


}
