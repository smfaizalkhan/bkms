package com.bkms.exchangerate.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ExchangeRateBeansConfig {

    static final int TIMEOUT = 5000;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
      return builder
               .setConnectTimeout(Duration.ofMillis(TIMEOUT))
               .setReadTimeout(Duration.ofMillis(TIMEOUT))
               .build();
    }
}
