package com.metasoft.restyle.integration.information;

import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Contractor;
import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Remodeler;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.ContractorRepository;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.RemodelerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class InformationRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private RemodelerRepository remodelerRepository;

    @Test
    void shouldSaveContractor() {
        // Arrange
        Contractor contractor = new Contractor("Professional contractor", "+1234567890");

        // Act
        Contractor savedContractor = contractorRepository.save(contractor);

        // Assert
        assertNotNull(savedContractor.getId());
        assertEquals("Professional contractor", savedContractor.getDescription());
        assertEquals("+1234567890", savedContractor.getPhone());
    }

    @Test
    void shouldFindContractorById() {
        // Arrange
        Contractor contractor = new Contractor("Contract specialist", "+9876543210");
        contractor = entityManager.persistAndFlush(contractor);

        // Act
        Optional<Contractor> foundContractor = contractorRepository.findById(contractor.getId());

        // Assert
        assertTrue(foundContractor.isPresent());
        assertEquals("Contract specialist", foundContractor.get().getDescription());
        assertEquals("+9876543210", foundContractor.get().getPhone());
    }

    @Test
    void shouldFindContractorByPhone() {
        // Arrange
        Contractor contractor = new Contractor("Phone searchable contractor", "+1122334455");
        entityManager.persistAndFlush(contractor);

        // Act
        Optional<Contractor> foundContractor = contractorRepository.findByPhone("+1122334455");

        // Assert
        assertTrue(foundContractor.isPresent());
        assertEquals("Phone searchable contractor", foundContractor.get().getDescription());
    }

    @Test
    void shouldFindAllContractors() {
        // Arrange
        entityManager.persistAndFlush(new Contractor("First contractor", "+1111111111"));
        entityManager.persistAndFlush(new Contractor("Second contractor", "+2222222222"));
        entityManager.flush();

        // Act
        List<Contractor> contractors = contractorRepository.findAll();

        // Assert
        assertTrue(contractors.size() >= 2);
    }

    @Test
    void shouldSaveRemodeler() {
        // Arrange
        Remodeler remodeler = new Remodeler("Professional remodeler", "+5556667777", "PREMIUM");

        // Act
        Remodeler savedRemodeler = remodelerRepository.save(remodeler);

        // Assert
        assertNotNull(savedRemodeler.getId());
        assertEquals("Professional remodeler", savedRemodeler.getDescription());
        assertEquals("+5556667777", savedRemodeler.getPhone());
        assertEquals("PREMIUM", savedRemodeler.getSubscription());
    }

    @Test
    void shouldFindRemodelerById() {
        // Arrange
        Remodeler remodeler = new Remodeler("Expert remodeler", "+7778889999", "BASIC");
        remodeler = entityManager.persistAndFlush(remodeler);

        // Act
        Optional<Remodeler> foundRemodeler = remodelerRepository.findById(remodeler.getId());

        // Assert
        assertTrue(foundRemodeler.isPresent());
        assertEquals("Expert remodeler", foundRemodeler.get().getDescription());
        assertEquals("+7778889999", foundRemodeler.get().getPhone());
        assertEquals("BASIC", foundRemodeler.get().getSubscription());
    }

    @Test
    void shouldFindRemodelerByPhone() {
        // Arrange
        Remodeler remodeler = new Remodeler("Phone searchable remodeler", "+3334445555", "PREMIUM");
        entityManager.persistAndFlush(remodeler);

        // Act
        Optional<Remodeler> foundRemodeler = remodelerRepository.findByPhone("+3334445555");

        // Assert
        assertTrue(foundRemodeler.isPresent());
        assertEquals("Phone searchable remodeler", foundRemodeler.get().getDescription());
    }

    @Test
    void shouldFindAllRemodelers() {
        // Arrange
        entityManager.persistAndFlush(new Remodeler("First remodeler", "+4444444444", "BASIC"));
        entityManager.persistAndFlush(new Remodeler("Second remodeler", "+5555555555", "PREMIUM"));
        entityManager.flush();

        // Act
        List<Remodeler> remodelers = remodelerRepository.findAll();

        // Assert
        assertTrue(remodelers.size() >= 2);
    }
}