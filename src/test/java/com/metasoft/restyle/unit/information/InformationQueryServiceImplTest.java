package com.metasoft.restyle.unit.information;

import com.metasoft.restyle.platform.information.profiles.application.internal.queryservices.ContractorQueryServiceImpl;
import com.metasoft.restyle.platform.information.profiles.application.internal.queryservices.RemodelerQueryServiceImpl;
import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Contractor;
import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Remodeler;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetAllContractorQuery;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetAllRemodelerQuery;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetContractorByIdQuery;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetRemodelerByIdQuery;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.ContractorRepository;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.RemodelerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InformationQueryServiceImplTest {

    @Mock
    private RemodelerRepository remodelerRepository;

    @Mock
    private ContractorRepository contractorRepository;

    @InjectMocks
    private RemodelerQueryServiceImpl remodelerQueryService;

    @InjectMocks
    private ContractorQueryServiceImpl contractorQueryService;

    private Remodeler testRemodeler1;
    private Remodeler testRemodeler2;
    private Contractor testContractor1;
    private Contractor testContractor2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test remodelers
        testRemodeler1 = new Remodeler("Professional Remodeler", "+1234567890", "PREMIUM");
        testRemodeler2 = new Remodeler("Home Remodeler", "+9876543210", "BASIC");

        // Create test contractors
        testContractor1 = new Contractor("Specialized Contractor", "+5551234567");
        testContractor2 = new Contractor("General Contractor", "+5559876543");

        // Set IDs using reflection
        try {
            var remodelerField = Remodeler.class.getDeclaredField("id");
            remodelerField.setAccessible(true);
            remodelerField.set(testRemodeler1, 1L);
            remodelerField.set(testRemodeler2, 2L);

            var contractorField = Contractor.class.getDeclaredField("id");
            contractorField.setAccessible(true);
            contractorField.set(testContractor1, 1L);
            contractorField.set(testContractor2, 2L);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    // Remodeler Query Service Tests
    @Test
    void shouldGetRemodelerById() {
        // Arrange
        when(remodelerRepository.findById(1L)).thenReturn(Optional.of(testRemodeler1));
        when(remodelerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        var result = remodelerQueryService.handle(new GetRemodelerByIdQuery(1L));
        var emptyResult = remodelerQueryService.handle(new GetRemodelerByIdQuery(999L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Professional Remodeler", result.get().getDescription());
        assertEquals("+1234567890", result.get().getPhone());
        assertEquals("PREMIUM", result.get().getSubscription());

        assertTrue(emptyResult.isEmpty());
        verify(remodelerRepository, times(1)).findById(1L);
        verify(remodelerRepository, times(1)).findById(999L);
    }

    @Test
    void shouldGetAllRemodelers() {
        // Arrange
        List<Remodeler> remodelers = Arrays.asList(testRemodeler1, testRemodeler2);
        when(remodelerRepository.findAll()).thenReturn(remodelers);

        // Act
        List<Remodeler> result = remodelerQueryService.handle(new GetAllRemodelerQuery());

        // Assert
        assertEquals(2, result.size());
        assertEquals("Professional Remodeler", result.get(0).getDescription());
        assertEquals("Home Remodeler", result.get(1).getDescription());
        verify(remodelerRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRemodelers() {
        // Arrange
        when(remodelerRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Remodeler> result = remodelerQueryService.handle(new GetAllRemodelerQuery());

        // Assert
        assertTrue(result.isEmpty());
        verify(remodelerRepository, times(1)).findAll();
    }

    // Contractor Query Service Tests
    @Test
    void shouldGetContractorById() {
        // Arrange
        when(contractorRepository.findById(1L)).thenReturn(Optional.of(testContractor1));
        when(contractorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        var result = contractorQueryService.handle(new GetContractorByIdQuery(1L));
        var emptyResult = contractorQueryService.handle(new GetContractorByIdQuery(999L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Specialized Contractor", result.get().getDescription());
        assertEquals("+5551234567", result.get().getPhone());

        assertTrue(emptyResult.isEmpty());
        verify(contractorRepository, times(1)).findById(1L);
        verify(contractorRepository, times(1)).findById(999L);
    }

    @Test
    void shouldGetAllContractors() {
        // Arrange
        List<Contractor> contractors = Arrays.asList(testContractor1, testContractor2);
        when(contractorRepository.findAll()).thenReturn(contractors);

        // Act
        List<Contractor> result = contractorQueryService.handle(new GetAllContractorQuery());

        // Assert
        assertEquals(2, result.size());
        assertEquals("Specialized Contractor", result.get(0).getDescription());
        assertEquals("General Contractor", result.get(1).getDescription());
        verify(contractorRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoContractors() {
        // Arrange
        when(contractorRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Contractor> result = contractorQueryService.handle(new GetAllContractorQuery());

        // Assert
        assertTrue(result.isEmpty());
        verify(contractorRepository, times(1)).findAll();
    }
}