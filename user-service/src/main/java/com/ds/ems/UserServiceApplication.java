package com.ds.ems;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// schema de securitate "basicAuth"
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic" // !!! "basic" pentru Basic Authentication
)
// schema de securitate JWT
@SecurityScheme(
        name = "JWT Authentication",    // nume la alegere
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",           // format JWT
        scheme = "bearer"               // "bearer"
)
// informațiile generale ale API-ului
@OpenAPIDefinition(
        info = @Info(title = "User Service API", version = "v1"),
        // aplica schema "basicAuth" global (pe toate endpoint-urile)
        security = @SecurityRequirement(name = "JWT Authentication")
)

public class UserServiceApplication {

    public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
