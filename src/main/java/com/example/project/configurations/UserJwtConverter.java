package com.example.project.configurations;

import com.example.project.service.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class UserJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserClient userClient;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String role = userClient.findUser(email, jwt).getRole();

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        }

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}

