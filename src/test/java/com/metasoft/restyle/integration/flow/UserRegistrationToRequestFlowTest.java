package com.metasoft.restyle.integration.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.valueobjects.Roles;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.metasoft.restyle.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import com.metasoft.restyle.platform.projectRequest.interfaces.rest.resources.CreateProjectRequestResource;
import com.metasoft.restyle.platform.projectRequest.interfaces.rest.resources.ProjectRequestResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.security.user.name=admin",
        "spring.security.user.password=admin",
        "authorization.jwt.secret=veryLongSecretKeyForTestingPurposesOnlyDoNotUseInProduction123456789",
        "authorization.jwt.expiration.days=1"
})
public class UserRegistrationToRequestFlowTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BearerTokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String authToken;
    private Long userId;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // Ensure test user exists in DB with CONTRACTOR and REMODELER roles
        if (!userRepository.existsByUsername("testuser")) {
            User user = new User();
            user.setUsername("testuser");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setEmail("test@example.com");
            user.setFirstName("John");
            user.setPaternalSurname("Doe");
            user.setMaternalSurname("Smith");
            User saved = userRepository.save(user);

            var roleContractor = roleRepository.findByName(Roles.ROLE_CONTRACTOR)
                    .orElseThrow(() -> new RuntimeException("ROLE_CONTRACTOR not found"));
            var roleRemodeler = roleRepository.findByName(Roles.ROLE_REMODELER)
                    .orElseThrow(() -> new RuntimeException("ROLE_REMODELER not found"));

            saved.setRoles(Set.of(roleContractor, roleRemodeler));
            User updated = userRepository.save(saved);
            userId = updated.getId();

            // Generate JWT for test user
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "testuser", null,
                    List.of(new SimpleGrantedAuthority("ROLE_CONTRACTOR"),
                            new SimpleGrantedAuthority("ROLE_REMODELER"))
            );
            authToken = tokenService.generateToken(auth);
        }
    }

    @Test
    void testCreateProjectRequestFlow() throws Exception {
        // Prepare a deadline 3 months from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);
        Date deadline = cal.getTime();

        CreateProjectRequestResource req = new CreateProjectRequestResource(
                "Projecaat" + UUID.randomUUID(),
                "Test Client",
                "client@example.com",
                "987654321",
                "123 Test Street",
                "Test City",
                "Kitchen renovation project",
                userId.intValue(), // businessId uses same test user
                userId.intValue(), // contractorId uses same test user
                deadline,
                3,
                5000
        );

        MvcResult result = mockMvc.perform(post("/api/v1/project-requests")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        ProjectRequestResource pr = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ProjectRequestResource.class
        );
        assertNotNull(pr.id(), "Project request ID should not be null");
    }
}
