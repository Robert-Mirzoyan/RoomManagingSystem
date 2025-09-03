package com.example.project.service;

import com.example.project.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final WebClient userWebClient;

    public UserDto findUser(String userEmail, Jwt jwt) {
        String bearer = "Bearer " + jwt.getTokenValue();
        return userWebClient.get()
                .uri("/api/{email}", userEmail)
                .headers(h -> h.set(HttpHeaders.AUTHORIZATION, bearer))
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }
}
