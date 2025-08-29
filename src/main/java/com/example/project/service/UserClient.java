package com.example.project.service;

import com.example.project.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final WebClient userWebClient;

    public boolean isAdmin(int userId) {
        UserDto dto = userWebClient.get()
                .uri("/api/{id}", userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        return dto != null && "Admin".equalsIgnoreCase(dto.getRole());
    }
}
