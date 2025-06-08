package com.metasoft.restyle.integration.IAM;

import com.metasoft.restyle.platform.iam.domain.model.commands.SeedRolesCommand;
import com.metasoft.restyle.platform.iam.domain.model.valueobjects.Roles;
import com.metasoft.restyle.platform.iam.domain.services.RoleCommandService;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.SignInResource;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.SignUpResource;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.UserResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.security.user.name=admin",
        "spring.security.user.password=admin",
        "jwt.secret=testSecretKeyForJwtTokenGenerationInTestEnvironment1234567890",
        "jwt.expiration=86400000",
        
})
public class AuthenticationControllerIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationControllerIntegrationTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleCommandService roleCommandService;

    private final String SIGN_UP_URL = "https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/authentication/sign-up";
    private final String SIGN_IN_URL = "https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/authentication/sign-in";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        // Seed roles and verify they exist
        roleCommandService.handle(new SeedRolesCommand());
        assertTrue(roleRepository.findByName(Roles.ROLE_CONTRACTOR).isPresent(),
                "CONTRACTOR role should be available for tests");
    }

    @Test
    void signUp_withValidData_shouldReturnCreatedUser() {
        // Arrange
        SignUpResource signUpResource = new SignUpResource(
                "testuser",
                "password123",
                List.of(Roles.ROLE_CONTRACTOR.name()),
                "test@example.com",
                "Test",
                "User",
                "",
                "Description",
                "123456789",
                "image.jpg"
        );

        // Act
        ResponseEntity<UserResource> response = restTemplate.postForEntity(
                SIGN_UP_URL,
                signUpResource,
                UserResource.class
        );

        // Log response for debugging
        logger.info("Sign up response: {}", response);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserResource createdUser = response.getBody();
        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.username());
        assertEquals("test@example.com", createdUser.email());
        assertTrue(createdUser.roles().contains(Roles.ROLE_CONTRACTOR.name()));
    }


    @Test
    void signIn_withValidCredentials_shouldReturnAuthenticatedUser() {
        // Arrange - Create user first
        SignUpResource signUpResource = new SignUpResource(
                "loginuser",
                "password123",
                List.of(Roles.ROLE_CONTRACTOR.name()),
                "login@example.com",
                "Login",
                "User",
                "",
                "Description",
                "123456789",
                "image.jpg"
        );

        ResponseEntity<UserResource> signUpResponse = restTemplate.postForEntity(
                SIGN_UP_URL,
                signUpResource,
                UserResource.class
        );

        // Log detailed info if sign-up fails
        if (!signUpResponse.getStatusCode().equals(HttpStatus.CREATED)) {
            logger.error("Sign-up failed with status: {}, body: {}",
                    signUpResponse.getStatusCode(), signUpResponse.getBody());
        }

        assertEquals(HttpStatus.CREATED, signUpResponse.getStatusCode(), "User should be created successfully before sign-in");

        // Act - Sign in with created user
        SignInResource signInResource = new SignInResource("loginuser", "password123");
        ResponseEntity<AuthenticatedUserResource> response = restTemplate.postForEntity(
                SIGN_IN_URL,
                signInResource,
                AuthenticatedUserResource.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthenticatedUserResource authenticatedUser = response.getBody();
        assertNotNull(authenticatedUser, "Authenticated user response should not be null");
        assertEquals("loginuser", authenticatedUser.username());
        assertNotNull(authenticatedUser.token(), "Token should not be null");
        assertFalse(authenticatedUser.token().isEmpty(), "Token should not be empty");
    }
    @Test
    void signIn_withWrongPassword_shouldReturnClientError() {
        // Arrange - Create user first
        SignUpResource signUpResource = new SignUpResource(
                "passworduser",
                "correctpassword",
                List.of(Roles.ROLE_CONTRACTOR.name()),
                "pwd@example.com",
                "Password",
                "User",
                "",
                "Description",
                "123456789",
                "image.jpg"
        );

        ResponseEntity<UserResource> signUpResponse = restTemplate.postForEntity(
                SIGN_UP_URL,
                signUpResource,
                UserResource.class
        );

        assertEquals(HttpStatus.CREATED, signUpResponse.getStatusCode(), "User should be created successfully before testing wrong password");

        // Act - Sign in with wrong password
        SignInResource signInResource = new SignInResource("passworduser", "wrongpassword");
        ResponseEntity<AuthenticatedUserResource> response = restTemplate.postForEntity(
                SIGN_IN_URL,
                signInResource,
                AuthenticatedUserResource.class
        );

        // Assert - The specific error code depends on how your application handles wrong passwords
        assertTrue(response.getStatusCode().is4xxClientError(),
                "Should return a 4xx client error for wrong password");
    }
    
}