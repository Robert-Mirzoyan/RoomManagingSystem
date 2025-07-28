package com.example.project.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

@Configuration
@ConfigurationProperties(prefix = "booking")
@Getter
@Setter
public class BookingTimeConfig {
    private LocalTime openTime;
    private LocalTime closeTime;
}

