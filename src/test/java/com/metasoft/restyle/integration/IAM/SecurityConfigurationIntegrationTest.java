package com.metasoft.restyle.integration.IAM;

import com.metasoft.restyle.platform.iam.domain.model.valueobjects.Roles;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.SignInResource;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.SignUpResource;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.UserResource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityConfigurationIntegrationTest extends AbstractTestContainers {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfigurationIntegrationTest.class);
    private static final String SIGN_UP_URL = "https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/authentication/sign-up";
    private static final String SIGN_IN_URL = "https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/authentication/sign-in";
    private static final String USERS_URL = "https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/users";
/*
    @Test
    void publicEndpoints_shouldBeAccessibleWithoutAuthentication() {
        // Act - Try to access authentication endpoints
        ResponseEntity<String> docsResponse = restTemplate.getForEntity("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/v3/api-docs", String.class);
        ResponseEntity<String> swaggerResponse = restTemplate.getForEntity("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/swagger-ui.html", String.class);

        // Assert
        assertTrue(docsResponse.getStatusCode().is2xxSuccessful(),
                "API docs should be accessible without authentication");
        assertTrue(swaggerResponse.getStatusCode().is2xxSuccessful() ||
                        swaggerResponse.getStatusCode().is3xxRedirection(),
                "Swagger UI should be accessible or redirect without authentication");
    }

    @Test
    void protectedEndpoints_shouldRequireAuthentication() {
        // Act - Try to access protected endpoint without authentication
        ResponseEntity<String> response = restTemplate.getForEntity(USERS_URL, String.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(),
                "Protected endpoint should require authentication");
    }

    @Test
    void authenticatedUser_shouldAccessProtectedEndpoints() {
        // Arrange - Create and authenticate a user
        SignUpResource signUpResource = new SignUpResource(
                "securitytestuser",
                "password123",
                List.of(Roles.ROLE_CONTRACTOR.name()),
                "security@example.com",
                "Security",
                "Test",
                "User",
                "Description",
                "123456789",
                "image.jpg"
        );

        restTemplate.postForEntity(SIGN_UP_URL, signUpResource, UserResource.class);

        SignInResource signInResource = new SignInResource("securitytestuser", "password123");
        ResponseEntity<AuthenticatedUserResource> signInResponse = restTemplate.postForEntity(
                SIGN_IN_URL,
                signInResource,
                AuthenticatedUserResource.class
        );

        assertEquals(HttpStatus.OK, signInResponse.getStatusCode());
        AuthenticatedUserResource authenticatedUser = signInResponse.getBody();
        assertNotNull(authenticatedUser);
        String token = authenticatedUser.token();

        // Act - Access protected endpoint with authentication
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<UserResource[]> response = restTemplate.exchange(
                USERS_URL,
                HttpMethod.GET,
                requestEntity,
                UserResource[].class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Authenticated user should be able to access protected endpoint");
        assertNotNull(response.getBody());
    }

    @Test
    void invalidToken_shouldBeRejected() {
        // Arrange - Create invalid authentication header
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid.token.here");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Act - Try to access protected endpoint with invalid token
        ResponseEntity<String> response = restTemplate.exchange(
                USERS_URL,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(),
                "Invalid token should be rejected");
    }
    */
}