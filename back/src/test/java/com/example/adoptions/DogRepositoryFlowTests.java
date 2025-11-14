package com.example.adoptions;

import com.example.adoptions.entity.Dog;
import com.example.adoptions.jpa_listener.AppointmentListener;
import com.example.adoptions.jpa_listener.DogListener;
import com.example.adoptions.repository.DogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        DogListener.class,
        AppointmentListener.class,
        DogRepositoryFlowTests.TestVectorStoreConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DogRepositoryFlowTests {

    @Autowired
    private DogRepository dogRepository;

    @Test
    @DisplayName("Dog can be created, updated and deleted via DogRepository")
    void dogRepositoryCrudFlow() {
        // CREATE
        Dog dog = new Dog();
        dog.setName("Rex");
        dog.setDescription("Friendly dog");

        Dog savedDog = dogRepository.save(dog);

        assertThat(savedDog.getId()).as("Dog ID after save").isNotNull();
        assertThat(savedDog.getName()).isEqualTo("Rex");
        assertThat(savedDog.getDescription()).isEqualTo("Friendly dog");

        Integer dogId = savedDog.getId();

        // READ
        Optional<Dog> foundAfterCreate = dogRepository.findById(dogId);
        assertThat(foundAfterCreate).isPresent();
        assertThat(foundAfterCreate.get().getName()).isEqualTo("Rex");

        // UPDATE
        Dog toUpdate = foundAfterCreate.get();
        toUpdate.setName("Max");
        toUpdate.setDescription("Very friendly dog");

        Dog updatedDog = dogRepository.save(toUpdate);

        assertThat(updatedDog.getId()).isEqualTo(dogId);
        assertThat(updatedDog.getName()).isEqualTo("Max");
        assertThat(updatedDog.getDescription()).isEqualTo("Very friendly dog");

        Optional<Dog> foundAfterUpdate = dogRepository.findById(dogId);
        assertThat(foundAfterUpdate).isPresent();
        assertThat(foundAfterUpdate.get().getName()).isEqualTo("Max");

        // DELETE
        dogRepository.deleteById(dogId);

        Optional<Dog> foundAfterDelete = dogRepository.findById(dogId);
        assertThat(foundAfterDelete).isEmpty();
    }

    @TestConfiguration
    static class TestVectorStoreConfig {

        @Bean
        VectorStore vectorStore() {
            // Use a mock for tests so that DogListener / AppointmentListener can be constructed
            return Mockito.mock(VectorStore.class);
        }
    }
}