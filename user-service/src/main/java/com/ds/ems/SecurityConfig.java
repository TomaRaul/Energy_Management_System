package com.ds.ems;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // lista cailor publice pentru swagger
    private static final String[] SWAGGER_WHITE_LIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("tomarm")
                .password("{noop}tomarm")
                .roles("ADMIN")
                .build();

        UserDetails client = User.withUsername("aaa")
                .password("{noop}123")
                .roles("CLIENT")
                .build();

        return new InMemoryUserDetailsManager(admin, client);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_WHITE_LIST).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                // cum sa iei rolul din token
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        // !!! fara prefix
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Ia cheia secretă din .properties
        byte[] keyBytes = Decoders.BASE64.decode("K7A24qesyls5nKqOqYv6L+N8D5C4w3A6E9B3zF0pG2rV7uXzR8iY/J6kZ3sV5bY+P9C7gA3bE6fH0jD5eS8gQ==");
        // O transformă într-o cheie de criptare
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        // Construiește și returnează un decoder care folosește această cheie
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}

