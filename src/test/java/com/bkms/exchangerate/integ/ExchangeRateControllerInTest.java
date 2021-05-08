package com.bkms.exchangerate.integ;

import com.bkms.exchangerate.ExchangeRateApplication;
import com.bkms.exchangerate.error.ApiError;
import com.bkms.exchangerate.model.ExchangeRate;
import com.bkms.exchangerate.repo.ExchangeRateRepo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = ExchangeRateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExchangeRateControllerInTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExchangeRateRepo exchangeRateRepo;


    @Test
    public void test_getExchangeRateDetails() {

        ExchangeRate exchangeRate = restTemplate
                .getForObject("http://localhost:" + port + "/api/exchange-rate/2021-05-07/USD/EUR", ExchangeRate.class);
        assertTrue(exchangeRate.getBaseCurrency().equals("USD"));
        assertTrue(exchangeRate.getTargetCurrency().equals("EUR"));
        assertThat(exchangeRate.getExchangeRate()).isNotEmpty();
        assertThat(exchangeRate.getTrend().name()).isNotEmpty();
        assertThat(exchangeRate.getFiveDayAvg()).isNotEmpty();
    }

    @Test
    public void test_getExchangeRateDetails_Failure() {

        ApiError apiError = restTemplate
                .getForObject("http://localhost:" + port + "/api/exchange-rate/1999-05-07/USD/EUR", ApiError.class);
        assertThat(apiError.getStatus()).isEqualTo(String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @Transactional
    public void test_getExchangeRateDetails_Daily() {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setTargetCurrency("EUR");
        exchangeRate.setBaseCurrency("USD");
        exchangeRate.setTrxDate("2021-05-05");
        exchangeRate.setExchangeRate("0.8999");
        exchangeRateRepo.save(exchangeRate);

        ExchangeRate exchangeRateResponse = restTemplate
                .getForObject("http://localhost:" + port + "/api/exchange-rate/history/daily/2021/05/05", ExchangeRate.class);
        assertThat(exchangeRateResponse.getBaseCurrency()).isNotEmpty();
        assertThat(exchangeRateResponse.getTargetCurrency()).isNotEmpty();
        assertThat(exchangeRateResponse.getExchangeRate()).isNotEmpty();
        assertThat(exchangeRateResponse.getTrend()).isNull();
        assertThat(exchangeRateResponse.getFiveDayAvg()).isNull();
    }

    @Test
    public void test_getExchangeRateDetails_Monthly() {

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setTargetCurrency("EUR");
        exchangeRate.setBaseCurrency("USD");
        exchangeRate.setTrxDate("2021-05-05");
        exchangeRate.setExchangeRate("0.8999");
        exchangeRateRepo.save(exchangeRate);

        List exchangeRateList = restTemplate
                .getForObject("http://localhost:" + port + "/api/exchange-rate/history/monthly/2021/05", List.class);

        assertThat(exchangeRateList.size()).isGreaterThan(0);
        Map<String, String> exchangeRateRes = (Map<String, String>) exchangeRateList.get(0);
        System.out.println("Key and Val" + exchangeRateRes.get("trxDate"));
        assertThat(exchangeRateRes.get("trxDate")).isNotEmpty();
        assertThat(exchangeRateRes.get("baseCurrency")).isNotEmpty();
        assertThat(exchangeRateRes.get("targetCurrency")).isNotEmpty();


    }


}
