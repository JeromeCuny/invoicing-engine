package com.jeromecuny;

import com.jeromecuny.config.InvoicingServiceConfiguration;
import com.jeromecuny.config.JsonConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({JsonConfiguration.class, InvoicingServiceConfiguration.class})
public class InvoicingApiModule {

    public static void main(String[] args) {
        SpringApplication.run(InvoicingApiModule.class, args);
    }
}
