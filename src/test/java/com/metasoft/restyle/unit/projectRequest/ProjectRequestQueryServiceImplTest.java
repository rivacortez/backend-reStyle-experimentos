package com.metasoft.restyle.unit.projectRequest;

import com.metasoft.restyle.platform.projectRequest.application.internal.queryservices.ProjectRequestQueryServiceImpl;
import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import com.metasoft.restyle.platform.projectRequest.domain.model.commands.CreateProjectRequestCommand;
import com.metasoft.restyle.platform.projectRequest.domain.model.queries.GetAllProjectRequestsByBusinessIdQuery;
import com.metasoft.restyle.platform.projectRequest.domain.model.queries.GetAllProjectRequestsByContractorIdQuery;
import com.metasoft.restyle.platform.projectRequest.domain.model.queries.GetProjectRequestByIdQuery;
import com.metasoft.restyle.platform.projectRequest.infrastructure.persistance.jpa.ProjectRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectRequestQueryServiceImplTest {

    @Autowired
    private ProjectRequestQueryServiceImpl projectRequestQueryService;

    @Autowired
    private ProjectRequestRepository projectRequestRepository;

    private ProjectRequest request1;
    private ProjectRequest request2;
    private ProjectRequest request3;
    private Date deadlineDate;

    @BeforeEach
    void setUp() {
        projectRequestRepository.deleteAll();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        deadlineDate = calendar.getTime();

        // Create test project requests
        request1 = projectRequestRepository.save(new ProjectRequest(new CreateProjectRequestCommand(
                "Request 1", "Smith", "john@example.com", "123456789",
                "123 Main St", "City A", "Summary 1", 1, 1,
                deadlineDate, 2, 5000
        )));

        request2 = projectRequestRepository.save(new ProjectRequest(new CreateProjectRequestCommand(
                "Request 2", "Johnson", "jane@example.com", "987654321",
                "456 Second St", "City B", "Summary 2", 1, 2,
                deadlineDate, 3, 7000
        )));

        request3 = projectRequestRepository.save(new ProjectRequest(new CreateProjectRequestCommand(
                "Request 3", "Williams", "bob@example.com", "456123789",
                "789 Third St", "City C", "Summary 3", 2, 1,
                deadlineDate, 1, 3000
        )));
    }

    @Test
    void shouldReturnProjectRequestById() {
        // Act
        Optional<ProjectRequest> result = projectRequestQueryService.handle(new GetProjectRequestByIdQuery(request1.getId()));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(request1.getId(), result.get().getId());
        assertEquals("Request 1", result.get().getName());
        assertEquals("Smith", result.get().getSurname());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentId() {
        // Act
        Optional<ProjectRequest> result = projectRequestQueryService.handle(new GetProjectRequestByIdQuery(999L));

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnProjectRequestsByBusinessId() {
        // Act
        List<ProjectRequest> businessRequests = projectRequestQueryService.handle(new GetAllProjectRequestsByBusinessIdQuery(1L));

        // Assert
        assertEquals(2, businessRequests.size());
        assertTrue(businessRequests.stream().anyMatch(p -> p.getId().equals(request1.getId())));
        assertTrue(businessRequests.stream().anyMatch(p -> p.getId().equals(request2.getId())));
        assertFalse(businessRequests.stream().anyMatch(p -> p.getId().equals(request3.getId())));
    }

    @Test
    void shouldReturnEmptyListForNonExistentBusinessId() {
        // Act
        List<ProjectRequest> businessRequests = projectRequestQueryService.handle(new GetAllProjectRequestsByBusinessIdQuery(999L));

        // Assert
        assertTrue(businessRequests.isEmpty());
    }

    @Test
    void shouldReturnProjectRequestsByContractorId() {
        // Act
        List<ProjectRequest> contractorRequests = projectRequestQueryService.handle(new GetAllProjectRequestsByContractorIdQuery(1L));

        // Assert
        assertEquals(2, contractorRequests.size());
        assertTrue(contractorRequests.stream().anyMatch(p -> p.getId().equals(request1.getId())));
        assertTrue(contractorRequests.stream().anyMatch(p -> p.getId().equals(request3.getId())));
        assertFalse(contractorRequests.stream().anyMatch(p -> p.getId().equals(request2.getId())));
    }

    @Test
    void shouldReturnEmptyListForNonExistentContractorId() {
        // Act
        List<ProjectRequest> contractorRequests = projectRequestQueryService.handle(new GetAllProjectRequestsByContractorIdQuery(999L));

        // Assert
        assertTrue(contractorRequests.isEmpty());
    }
}