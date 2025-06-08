package com.metasoft.restyle.integration.business;

import com.metasoft.restyle.platform.business.domain.model.aggregates.Business;
import com.metasoft.restyle.platform.business.domain.model.commands.CreateBusinessCommand;
import com.metasoft.restyle.platform.business.infrastructure.persistance.jpa.BusinessRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BusinessRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BusinessRepository businessRepository;

    @Test
    void saveBusinessPersistsBusinessToDatabase() {
        // Arrange
        Business business = createBusiness("Test Business");

        // Act
        Business savedBusiness = businessRepository.save(business);

        // Assert
        assertThat(savedBusiness.getId()).isNotNull();
        assertThat(savedBusiness.getName()).isEqualTo("Test Business");
        assertThat(savedBusiness.getExpertise()).isEqualTo("Remodeling");
    }

    @Test
    void findByIdReturnsBusinessWhenExists() {
        // Arrange
        Business business = createBusiness("Business For ID Test");
        entityManager.persist(business);
        entityManager.flush();

        // Act
        Optional<Business> found = businessRepository.findById(business.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(business.getName(), found.get().getName());
    }

    @Test
    void findByIdReturnsEmptyOptionalWhenNotExists() {
        // Act
        Optional<Business> found = businessRepository.findById(999L);

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    void existsByNameReturnsTrueWhenBusinessNameExists() {
        // Arrange
        Business business = createBusiness("Existing Business");
        entityManager.persist(business);
        entityManager.flush();

        // Act
        boolean exists = businessRepository.existsByName("Existing Business");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByNameReturnsFalseWhenBusinessNameDoesNotExist() {
        // Act
        boolean exists = businessRepository.existsByName("Non-existing Business");

        // Assert
        assertFalse(exists);
    }

    /*
    @Test
    void findAllReturnsAllBusinesses() {
        // Arrange
        Business business1 = createBusiness("Business One");
        Business business2 = createBusiness("Business Two");
        Business business3 = createBusiness("Business Three");

        entityManager.persist(business1);
        entityManager.persist(business2);
        entityManager.persist(business3);
        entityManager.flush();

        // Act
        List<Business> businesses = businessRepository.findAll();

        // Assert
        assertThat(businesses).hasSize(3);
        assertThat(businesses).extracting(Business::getName).containsExactlyInAnyOrder(
                "Business One", "Business Two", "Business Three");
    }*/

    @Test
    void deleteBusinessRemovesBusinessFromDatabase() {
        // Arrange
        Business business = createBusiness("Business To Delete");
        entityManager.persist(business);
        entityManager.flush();

        // Act
        businessRepository.delete(business);
        Optional<Business> found = businessRepository.findById(business.getId());

        // Assert
        assertTrue(found.isEmpty());
    }

    private Business createBusiness(String name) {
        CreateBusinessCommand command = new CreateBusinessCommand(
                name,
                "logo.jpg",
                "Remodeling",
                "123 Main St",
                "New York",
                "Test business description",
                1
        );
        return new Business(command);
    }
}