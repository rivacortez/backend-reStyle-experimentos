package com.metasoft.restyle.unit.business;

import com.metasoft.restyle.platform.business.application.internal.queryservices.BusinessQueryServiceImpl;
import com.metasoft.restyle.platform.business.domain.model.aggregates.Business;
import com.metasoft.restyle.platform.business.domain.model.commands.CreateBusinessCommand;
import com.metasoft.restyle.platform.business.domain.model.queries.GetAllBusinessesQuery;
import com.metasoft.restyle.platform.business.domain.model.queries.GetBusinessByIdQuery;
import com.metasoft.restyle.platform.business.infrastructure.persistance.jpa.BusinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusinessQueryServiceImplTest {

    @Mock
    private BusinessRepository businessRepository;

    private BusinessQueryServiceImpl businessQueryService;

    @BeforeEach
    void setUp() {
        businessQueryService = new BusinessQueryServiceImpl(businessRepository);
    }

    @Test
    void getBusinessByIdWithExistingIdReturnsExpectedBusiness() {
        // Arrange
        Long businessId = 1L;
        Business expectedBusiness = createSampleBusiness();
        when(businessRepository.findById(businessId)).thenReturn(Optional.of(expectedBusiness));

        // Act
        Optional<Business> result = businessQueryService.handle(new GetBusinessByIdQuery(businessId));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedBusiness, result.get());
        verify(businessRepository).findById(businessId);
    }

    @Test
    void getBusinessByIdWithNonExistingIdReturnsEmptyOptional() {
        // Arrange
        Long businessId = 99L;
        when(businessRepository.findById(businessId)).thenReturn(Optional.empty());

        // Act
        Optional<Business> result = businessQueryService.handle(new GetBusinessByIdQuery(businessId));

        // Assert
        assertFalse(result.isPresent());
        verify(businessRepository).findById(businessId);
    }

    @Test
    void getAllBusinessesReturnsAllBusinessesFromRepository() {
        // Arrange
        List<Business> expectedBusinesses = List.of(
                createSampleBusiness(),
                createAnotherSampleBusiness()
        );
        when(businessRepository.findAll()).thenReturn(expectedBusinesses);

        // Act
        List<Business> result = businessQueryService.handle(new GetAllBusinessesQuery());

        // Assert
        assertEquals(expectedBusinesses.size(), result.size());
        assertEquals(expectedBusinesses, result);
        verify(businessRepository).findAll();
    }

    @Test
    void getAllBusinessesReturnsEmptyListWhenNoBusinessesExist() {
        // Arrange
        when(businessRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Business> result = businessQueryService.handle(new GetAllBusinessesQuery());

        // Assert
        assertTrue(result.isEmpty());
        verify(businessRepository).findAll();
    }

    @Test
    void getBusinessByIdWithNullIdThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new GetBusinessByIdQuery(null);
        });
    }

    private Business createSampleBusiness() {
        CreateBusinessCommand command = new CreateBusinessCommand(
                "Sample Business",
                "image.jpg",
                "Remodeling",
                "123 Main St",
                "New York",
                "Business description",
                1);
        return new Business(command);
    }

    private Business createAnotherSampleBusiness() {
        CreateBusinessCommand command = new CreateBusinessCommand(
                "Another Business",
                "logo.png",
                "Interior Design",
                "456 Park Ave",
                "Los Angeles",
                "Another business description",
                2);
        return new Business(command);
    }
}