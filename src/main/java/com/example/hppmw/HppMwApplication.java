package com.example.hppmw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ReactorNettyClientRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Clock;

@SpringBootApplication
public class HppMwApplication {

    public static void main(String[] args) {
        SpringApplication.run(HppMwApplication.class, args);
    }

    /**
     * Expose a shared RestClient bean. Services inject this directly, making it easy
     * to substitute a test-configured RestClient in unit tests.
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(new ReactorNettyClientRequestFactory())
                .build();
    }

//    return RestClient.create();


    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
