package com.jeromecuny.config;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

/**
 * Generic and composable configuration of Jackson.
 */
@Configuration
@ImportAutoConfiguration({RestTemplateAutoConfiguration.class, JacksonAutoConfiguration.class})
@PropertySource("classpath:jackson-object-mapper.properties")
public class JsonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .modulesToInstall(items -> {
                    items.add(new Jdk8Module());
                    items.add(new JavaTimeModule());
                });
    }

    @Bean
    public HttpMessageConverters messageConverters(List<HttpMessageConverter<?>> converters, Jackson2ObjectMapperBuilder objectMapperBuilder,
                                                   @Value("${product-search.http.message-converters.exclusion}") List<String> exclusions) {
        return new HttpMessageConverters(true, converters) {
            @Override
            protected List<HttpMessageConverter<?>> postProcessConverters(List<HttpMessageConverter<?>> converters) {
                converters.removeIf(item -> exclusions.contains(item.getClass().getName()));
                converters.stream()
                        .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                        .map(MappingJackson2HttpMessageConverter.class::cast)
                        .forEach(converter -> objectMapperBuilder.configure(converter.getObjectMapper()));
                return converters;
            }
        };
    }
}
