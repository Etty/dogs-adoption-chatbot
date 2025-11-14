package com.example.adoptions;

import com.example.adoptions.config.TestConfig;
import com.example.adoptions.entity.Appointment;
import com.example.adoptions.entity.Dog;
import com.example.adoptions.jpa_listener.AppointmentListener;
import com.example.adoptions.jpa_listener.DogListener;
import com.example.adoptions.repository.AppointmentRepository;
import com.example.adoptions.repository.DogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({
        DogListener.class,
        AppointmentListener.class,
        TestConfig.class,
})
@Qualifier("mockVectorStore")
class AppointmentRepositoryFlowTests {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DogRepository dogRepository;

    @Test
    @DisplayName("Appointment can be created, updated and deleted via AppointmentRepository and DogRepository")
    void appointmentRepositoryCrudFlow() {
        // PREPARE: create a Dog that the Appointment will reference
        Dog dog = new Dog();
        dog.setName("Rex");
        dog.setDescription("Friendly dog for appointment");

        Dog savedDog = dogRepository.save(dog);
        Integer dogId = savedDog.getId();
        assertThat(dogId).as("Dog ID after save").isNotNull();

        // CREATE
        Appointment appointment = new Appointment();
        appointment.setName("John Doe");
        appointment.setPhone("123-456-7890");
        appointment.setTime(LocalDateTime.now().plusDays(1));
        appointment.setComment("Initial appointment");
        appointment.setDog(savedDog);

        // Maintain bidirectional consistency
        savedDog.setAppointment(appointment);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        Integer appointmentId = savedAppointment.getId();

        assertThat(appointmentId).as("Appointment ID after save").isNotNull();
        assertThat(savedAppointment.getName()).isEqualTo("John Doe");
        assertThat(savedAppointment.getPhone()).isEqualTo("123-456-7890");
        assertThat(savedAppointment.getDog()).isNotNull();
        assertThat(savedAppointment.getDog().getId()).isEqualTo(dogId);

        // READ
        Optional<Appointment> foundAfterCreate = appointmentRepository.findById(appointmentId);
        assertThat(foundAfterCreate).isPresent();
        assertThat(foundAfterCreate.get().getName()).isEqualTo("John Doe");

        Appointment byPhone = appointmentRepository.findByPhone("123-456-7890");
        assertThat(byPhone).isNotNull();
        assertThat(byPhone.getId()).isEqualTo(appointmentId);

        // UPDATE
        Appointment toUpdate = foundAfterCreate.get();
        toUpdate.setName("Jane Doe");
        toUpdate.setPhone("987-654-3210");
        toUpdate.setComment("Updated appointment comment");
        toUpdate.setTime(toUpdate.getTime().plusHours(2));

        Appointment updatedAppointment = appointmentRepository.save(toUpdate);

        assertThat(updatedAppointment.getId()).isEqualTo(appointmentId);
        assertThat(updatedAppointment.getName()).isEqualTo("Jane Doe");
        assertThat(updatedAppointment.getPhone()).isEqualTo("987-654-3210");
        assertThat(updatedAppointment.getComment()).isEqualTo("Updated appointment comment");

        Optional<Appointment> foundAfterUpdate = appointmentRepository.findById(appointmentId);
        assertThat(foundAfterUpdate).isPresent();
        assertThat(foundAfterUpdate.get().getName()).isEqualTo("Jane Doe");

        // DELETE:
        // Remove appointment through Dog side of one-to-one relationship
        Dog dogWithAppointment = dogRepository.findById(dogId).orElseThrow();
        assertThat(dogWithAppointment.getAppointment()).isNotNull();

        dogWithAppointment.setAppointment(null);
        dogRepository.save(dogWithAppointment);

        Optional<Appointment> foundAfterDelete = appointmentRepository.findById(appointmentId);
        assertThat(foundAfterDelete).isEmpty();

        Appointment afterDeleteByPhone = appointmentRepository.findByPhone("987-654-3210");
        assertThat(afterDeleteByPhone).isNull();

        // Dog itself must still exist
        Optional<Dog> dogStillPresent = dogRepository.findById(dogId);
        assertThat(dogStillPresent).isPresent();
        assertThat(dogStillPresent.get().getAppointment()).isNull();
    }

}