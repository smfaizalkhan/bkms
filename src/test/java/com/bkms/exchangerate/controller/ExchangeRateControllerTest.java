package com.bkms.exchangerate.controller;

import com.bkms.exchangerate.exception.RecordNotFoundException;
import com.bkms.exchangerate.factory.DomainFactory;
import com.bkms.exchangerate.model.ExchangeRate;
import com.bkms.exchangerate.model.ExchangeRateDTO;
import com.bkms.exchangerate.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExchangeRateService exchangeRateService;

    @Test
    void getExchangeRateDetails_Test_Success() throws Exception {
        ExchangeRateDTO exchangeRateDTO = DomainFactory.createExchangeRateDTO();
        when(exchangeRateService.getExchangeRateDetails(anyString(),anyString(),anyString())).thenReturn(exchangeRateDTO);
        mockMvc.perform(get("/api/exchange-rate/2021-05-07/USD/EUR")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.baseCurrency").exists())
                .andExpect(jsonPath("$.targetCurrency").exists())
                .andExpect(jsonPath("$.trxDate").exists());
    }

    @Test
    void getExchangeRateDetails_Test_Failure() throws Exception {
        ExchangeRateDTO exchangeRateDTO = DomainFactory.createExchangeRateDTO();
        when(exchangeRateService.getExchangeRateDetails(anyString(),anyString(),anyString())).thenReturn(exchangeRateDTO);
        mockMvc.perform(get("/api/exchange-rate/1999-05-07/USD/EUR")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void getDailyExchangeRates_Test_Success() throws Exception {
        ExchangeRateDTO exchangeRateDTO = DomainFactory.createExchangeRateForDaily();
        when(exchangeRateService.getDailyExchangeRates(anyString(),anyString(),anyString())).thenReturn(exchangeRateDTO);
        mockMvc.perform(get("/api/exchange-rate/history/daily/2021/05/05")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.baseCurrency").exists())
                .andExpect(jsonPath("$.targetCurrency").exists())
                .andExpect(jsonPath("$.trxDate").exists());
    }


    @Test
    void getDailyExchangeRates_Test_Failure() throws Exception {
        when(exchangeRateService.getDailyExchangeRates(anyString(),anyString(),anyString())).thenThrow(new RecordNotFoundException(String.format("Record Not Found for date %s", "2021-05-05")));
        mockMvc.perform(get("/api/exchange-rate/history/daily/2021/05/05")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.status").exists());
    }


    @Test
    void getMonthlyExchangeRates_Test_Success() throws Exception {
        List<ExchangeRateDTO> exchangeRateDTOList = DomainFactory.createExchangeRateDTOList();
        when(exchangeRateService.getMonthlyExchangeRates(anyString(),anyString())).thenReturn(exchangeRateDTOList);
        mockMvc.perform(get("/api/exchange-rate/history/monthly/2021/05")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].baseCurrency").exists())
                .andExpect(jsonPath("$[0].targetCurrency").exists())
                .andExpect(jsonPath("$[0].trxDate").exists());
    }


    @Test
    void getMonthlyExchangeRates_Test_Failure() throws Exception {
        when(exchangeRateService.getMonthlyExchangeRates(anyString(),anyString())).thenThrow(new RecordNotFoundException(String.format("Record Not Found for date %s", "2021-05-05")));
        mockMvc.perform(get("/api/exchange-rate/history/monthly/2021/05")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.status").exists());
    }


}