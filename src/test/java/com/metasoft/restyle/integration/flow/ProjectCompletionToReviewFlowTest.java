package com.metasoft.restyle.integration.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.valueobjects.Roles;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.metasoft.restyle.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import com.metasoft.restyle.platform.project.interfaces.rest.resources.CreateProjectResource;
import com.metasoft.restyle.platform.project.interfaces.rest.resources.ProjectResource;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.CreateReviewResource;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.ReviewResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class ProjectCompletionToReviewFlowTest {

    @Autowired
    private TestRestTemplate restTemplate;

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

    private String businessToken;
    private String contractorToken;
    private Long businessId;
    private Long contractorId;

    @BeforeEach
    void setupUsers() {
        // Create or fetch business user with ROLE_REMODELER
        User biz = userRepository.findByUsername("business1").orElseGet(() -> {
            User u = new User();
            u.setUsername("business1");
            u.setPassword(passwordEncoder.encode("password123"));
            u.setEmail("business@example.com");
            u.setFirstName("John");
            u.setPaternalSurname("Doe");
            u.setMaternalSurname("");
            User saved = userRepository.save(u);
            var roleRemodeler = roleRepository.findByName(Roles.ROLE_REMODELER)
                    .orElseThrow(() -> new RuntimeException("ROLE_REMODELER not found"));
            saved.setRoles(Set.of(roleRemodeler));
            return userRepository.save(saved);
        });
        businessId = biz.getId();
        // Generate JWT for remodeler
        businessToken = tokenService.generateToken(
                new UsernamePasswordAuthenticationToken(
                        biz.getUsername(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_REMODELER"))
                )
        );

        // Create or fetch contractor user with ROLE_CONTRACTOR
        User ctr = userRepository.findByUsername("contractor1").orElseGet(() -> {
            User u = new User();
            u.setUsername("contractor1");
            u.setPassword(passwordEncoder.encode("password123"));
            u.setEmail("contractor@example.com");
            u.setFirstName("Jane");
            u.setPaternalSurname("Smith");
            u.setMaternalSurname("");
            User saved = userRepository.save(u);
            var roleContractor = roleRepository.findByName(Roles.ROLE_CONTRACTOR)
                    .orElseThrow(() -> new RuntimeException("ROLE_CONTRACTOR not found"));
            saved.setRoles(Set.of(roleContractor));
            return userRepository.save(saved);
        });
        contractorId = ctr.getId();
        // Generate JWT for contractor
        contractorToken = tokenService.generateToken(
                new UsernamePasswordAuthenticationToken(
                        ctr.getUsername(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_CONTRACTOR"))
                )
        );
    }

    @Test
    void testProjectCompletionToReviewFlow() {
    	String uniqueName = "Home Renovation " + System.currentTimeMillis();
        // 1. Create project as remodeler (business role)
        CreateProjectResource projectReq = new CreateProjectResource(
        		uniqueName,
                "Complete home renovation project",
                businessId.intValue(),
                contractorId.intValue(),
                new Date(),
                new Date(System.currentTimeMillis() + 86400000), // 1 day later
                "project.jpg"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(businessToken);
        HttpEntity<CreateProjectResource> projectEntity = new HttpEntity<>(projectReq, headers);
        ResponseEntity<ProjectResource> projectRes = restTemplate.exchange(
                "/api/v1/projects", HttpMethod.POST, projectEntity, ProjectResource.class);
        assertEquals(201, projectRes.getStatusCodeValue());
        ProjectResource createdProject = projectRes.getBody();
        assertNotNull(createdProject);
        assertNotNull(createdProject.id());

        // 2. Submit review as remodeler (business)
        CreateReviewResource reviewReq = new CreateReviewResource(
                contractorId.intValue(),
                createdProject.id().intValue(),
                "2 weeks",
                5,
                "Excellent work, completed on time and within budget",
                "review.jpg"
        );
        HttpEntity<CreateReviewResource> reviewEntity = new HttpEntity<>(reviewReq, headers);
        ResponseEntity<ReviewResource> reviewRes = restTemplate.exchange(
                "/api/v1/reviews", HttpMethod.POST, reviewEntity, ReviewResource.class);
        assertEquals(201, reviewRes.getStatusCodeValue());
        ReviewResource rv = reviewRes.getBody();
        assertNotNull(rv);
        assertEquals(5, rv.rating());
        assertEquals(contractorId.intValue(), rv.contractorId());
        assertEquals(createdProject.id().intValue(), rv.projectId());
    }
}
