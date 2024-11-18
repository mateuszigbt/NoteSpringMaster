package com.noteapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation in the NoteApp Spring Boot application.
 * This class provides bean definitions for configuring OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

    // Constants for security scheme
    private static final String SCHEME_NAME = "bearerScheme";
    private static final String SCHEME = "Bearer";

    /**
     * Bean definition method for custom OpenAPI configuration.
     * @return OpenAPI instance configured with API information and security.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        var openApi = new OpenAPI()
                .info(getInfo());
        addSecurity(openApi);
        return openApi;
    }

    /**
     * Method to configure API information.
     * @return Info instance with API title and description.
     */
    private Info getInfo() {
        return new Info()
                .title("Notes app API")
                .description("API documentation");
    }

    /**
     * Method to add security to the API.
     * @param openApi OpenAPI instance to which security will be added.
     */
    private void addSecurity(OpenAPI openApi) {
        var components = createComponents();
        var securityItem = new SecurityRequirement().addList(SCHEME_NAME);

        openApi
                .components(components)
                .addSecurityItem(securityItem);
    }

    /**
     * Method to create components for the API.
     * @return Components instance containing security schemes.
     */
    private Components createComponents() {
        var components = new Components();
        components.addSecuritySchemes(SCHEME_NAME, createSecurityScheme());

        return components;
    }

    /**
     * Method to create a security scheme for the API.
     * @return SecurityScheme instance with bearer token authentication.
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(SCHEME);
    }
}