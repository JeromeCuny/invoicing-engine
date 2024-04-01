package com.jeromecuny.config;

import com.jeromecuny.service.InvoicingService;
import com.jeromecuny.service.InvoicingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvoicingServiceConfiguration {
    @Bean
    public InvoicingService invoicingService() {
        return new InvoicingServiceImpl();
    }
}
