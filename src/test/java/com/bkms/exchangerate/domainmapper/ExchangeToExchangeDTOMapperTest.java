package com.bkms.exchangerate.domainmapper;

import com.bkms.exchangerate.factory.DomainFactory;
import com.bkms.exchangerate.model.ExchangeRate;
import com.bkms.exchangerate.model.ExchangeRateDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class ExchangeToExchangeDTOMapperTest {

    @InjectMocks
    private ExchangeToExchangeDTOMapper exchangeToExchangeDTOMapper;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void apply() {
        ExchangeRate exchangeRate = DomainFactory.createExchangeRate();
        Assertions.assertThat(exchangeToExchangeDTOMapper.apply(exchangeRate)).isInstanceOf(ExchangeRateDTO.class);

    }
}