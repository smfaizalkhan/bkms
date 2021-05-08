package com.bkms.exchangerate.service;


import com.bkms.exchangerate.config.ExchangeRateBeansConfig;
import com.bkms.exchangerate.domainmapper.ExchangeToExchangeDTOMapper;
import com.bkms.exchangerate.exception.ExternalSourceException;
import com.bkms.exchangerate.exception.RecordNotFoundException;
import com.bkms.exchangerate.factory.DomainFactory;
import com.bkms.exchangerate.model.ExchangeRate;
import com.bkms.exchangerate.model.ExchangeRateDTO;
import com.bkms.exchangerate.model.Trend;
import com.bkms.exchangerate.repo.ExchangeRateRepo;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService;
    @Mock
    private ExchangeRateRepo exchangeRateRepo;
    @Mock
    private ExchangeToExchangeDTOMapper exchangeToExchangeDTOMapper;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        reset(exchangeRateRepo,exchangeToExchangeDTOMapper,restTemplate);
    }
    @Test
    void getExchangeRateDetails_Test_Success() {
        JSONObject mockedJsonObject = DomainFactory.mockedResponseObject();
        String date = "2021-05-05";
        String baseCurrency="USD";
        String targetCurrency="EUR";
        ExchangeRateDTO exchangeRateDTO = DomainFactory.createExchangeRateDTO();
        when(restTemplate.getForEntity(anyString(), any(Class.class))).thenReturn(new ResponseEntity(mockedJsonObject, HttpStatus.OK));
        when(exchangeToExchangeDTOMapper.apply(any(ExchangeRate.class))).thenReturn(exchangeRateDTO);
        ExchangeRateDTO exchangeRateRspDTO = exchangeRateService.getExchangeRateDetails(date,baseCurrency,targetCurrency);
        assertThat(exchangeRateRspDTO.getBaseCurrency()).isEqualTo(baseCurrency);
        assertThat(exchangeRateRspDTO.getTargetCurrency()).isEqualTo(targetCurrency);
        Double rate = JsonPath.parse(mockedJsonObject.toJSONString()).read("$.rates."+targetCurrency);
        assertThat(Double.valueOf(exchangeRateRspDTO.getExchangeRate())).isEqualTo(rate);
        assertThat(Double.valueOf(exchangeRateRspDTO.getFiveDayAvg())).isEqualTo(rate);
        assertThat(exchangeRateRspDTO.getTrend()).isEqualTo(Trend.CONSTANT);
    }

    @Test
    void getExchangeRateDetails_Test_Failure() {
        JSONObject mockedJsonObject = DomainFactory.mockedFailureInfo();
        String date = "2021-05-05";
        String baseCurrency="USD";
        String targetCurrency="EURS";
        when(restTemplate.getForEntity(anyString(), any(Class.class))).thenReturn(new ResponseEntity(mockedJsonObject, HttpStatus.OK));
        assertThrows(ExternalSourceException.class,() -> exchangeRateService.getExchangeRateDetails(date,baseCurrency,targetCurrency));
        assertThat(mockedJsonObject).isNotEmpty();
        assertThat(mockedJsonObject.get("success")).isEqualTo(false);
    }
    @Test
    void getDailyExchangeRates_Test() {
        ExchangeRateDTO exchangeRateDTO =  DomainFactory.createExchangeRateDTO();
        when(exchangeRateRepo.findById(anyString())).thenReturn(Optional.of(DomainFactory.createExchangeRate()));
        when(exchangeToExchangeDTOMapper.apply(any(ExchangeRate.class))).thenReturn(exchangeRateDTO);
        ExchangeRateDTO exchangeRateResponseDTO = exchangeRateService.getDailyExchangeRates("2021","05","05");
        assertThat(exchangeRateResponseDTO).isNotNull();
    }
    @Test
    void getDailyExchangeRates_Failure() {
        when(exchangeRateRepo.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(RecordNotFoundException.class,()->exchangeRateService.getDailyExchangeRates("2021","05","05"));
    }

    @Test
    void getMonthlyExchangeRates_Test() {
        ExchangeRateDTO exchangeRate = DomainFactory.createExchangeRateDTO();
        when(exchangeRateRepo.findByTrxDateStartsWith(anyString())).thenReturn(Optional.of(DomainFactory.createExchangeRateList()));
        when(exchangeToExchangeDTOMapper.apply(any(ExchangeRate.class))).thenReturn(exchangeRate);
        List<ExchangeRateDTO> exchangeRateDTOList = exchangeRateService.getMonthlyExchangeRates("2012","05");
        assertThat(exchangeRateDTOList).isNotNull();
        assertThat(exchangeRateDTOList).containsOnlyOnce(exchangeRateDTOList.get(0));
    }

    @Test
    void getMonthlyExchangeRates_Failure() {
        when(exchangeRateRepo.findByTrxDateStartsWith(anyString())).thenReturn(Optional.empty());
        assertThrows(RecordNotFoundException.class,()->exchangeRateService.getMonthlyExchangeRates("2012","05"));

    }

    @Test
    void calculateAvgAndTrend() {
        String date = "2021-05-05";
        String baseCurrency="USD";
        String targetCurrency="EUR";
        JSONObject mockedJsonObject = DomainFactory.mockedResponseObject();
        when(restTemplate.getForEntity(anyString(), any(Class.class))).thenReturn(new ResponseEntity(mockedJsonObject, HttpStatus.OK));
        JSONObject jsonObject = exchangeRateService.calculateAvgAndTrend(date,baseCurrency,targetCurrency);
        assertThat(jsonObject).isNotEmpty();
        System.out.println("trend"+jsonObject.getAsString("trend"));
        System.out.println("avg"+jsonObject.getAsString("avg"));
        System.out.println("mockedJsonObject"+JsonPath.parse(mockedJsonObject.toJSONString()).read("$.rates."+targetCurrency));
        assertThat(jsonObject.getAsString("trend")).isEqualTo(Trend.CONSTANT.name());
        assertThat(jsonObject.get("avg")).isEqualTo(JsonPath.parse(mockedJsonObject.toJSONString()).read("$.rates."+targetCurrency));
    }
}