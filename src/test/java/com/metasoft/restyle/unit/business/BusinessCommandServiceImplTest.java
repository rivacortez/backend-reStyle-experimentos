package com.metasoft.restyle.unit.business;

import com.metasoft.restyle.platform.business.domain.model.aggregates.Business;
import com.metasoft.restyle.platform.business.domain.model.commands.CreateBusinessCommand;
import com.metasoft.restyle.platform.business.application.internal.commandservices.BusinessCommandServiceImpl;
import com.metasoft.restyle.platform.business.infrastructure.persistance.jpa.BusinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusinessCommandServiceImplTest {

    @Mock
    private BusinessRepository businessRepository;

    private BusinessCommandServiceImpl businessCommandService;

    @BeforeEach
    void setUp() {
        businessCommandService = new BusinessCommandServiceImpl(businessRepository);
    }

    @Test
    void createBusinessWithValidDataSucceeds() {
        // Arrange
        CreateBusinessCommand command = new CreateBusinessCommand(
                "Test Business",
                "logo.jpg",
                "Remodeling",
                "123 Main St",
                "New York",
                "Business description",
                1
        );

        Business savedBusiness = new Business(command);
        when(businessRepository.existsByName(command.name())).thenReturn(false);
        when(businessRepository.save(any(Business.class))).thenReturn(savedBusiness);

        // Act
        Optional<Business> result = businessCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(command.name(), result.get().getName());
        verify(businessRepository).existsByName(command.name());
        verify(businessRepository).save(any(Business.class));
    }

    @Test
    void createBusinessWithDuplicateNameThrowsException() {
        // Arrange
        CreateBusinessCommand command = new CreateBusinessCommand(
                "Existing Business",
                "logo.jpg",
                "Remodeling",
                "123 Main St",
                "Chicago",
                "Business description",
                1
        );

        when(businessRepository.existsByName(command.name())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            businessCommandService.handle(command);
        });

        verify(businessRepository).existsByName(command.name());
        verify(businessRepository, never()).save(any(Business.class));
    }

    @Test
    void createBusinessSavesCorrectData() {
        // Arrange
        CreateBusinessCommand command = new CreateBusinessCommand(
                "New Business",
                "business-logo.png",
                "Interior Design",
                "456 Park Ave",
                "Los Angeles",
                "Detailed business description",
                42
        );

        when(businessRepository.existsByName(command.name())).thenReturn(false);
        when(businessRepository.save(any(Business.class))).thenAnswer(invocation -> {
            Business savedBusiness = invocation.getArgument(0);
            return savedBusiness;
        });

        // Act
        Optional<Business> result = businessCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(command.name(), result.get().getName());
        assertEquals(command.image(), result.get().getImage());
        assertEquals(command.expertise(), result.get().getExpertise());
        assertEquals(command.address(), result.get().getAddress());
        assertEquals(command.city(), result.get().getCity());
        assertEquals(command.description(), result.get().getDescription());
        assertEquals(command.remodelerId(), result.get().getRemodelerId());
    }
}