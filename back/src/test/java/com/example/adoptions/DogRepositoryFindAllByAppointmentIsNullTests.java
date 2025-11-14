package com.example.adoptions;

import com.example.adoptions.config.TestConfig;
import com.example.adoptions.entity.Appointment;
import com.example.adoptions.entity.Dog;
import com.example.adoptions.jpa_listener.AppointmentListener;
import com.example.adoptions.jpa_listener.DogListener;
import com.example.adoptions.repository.AppointmentRepository;
import com.example.adoptions.repository.DogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Qualifier("mockVectorStore")
@Import({
        DogListener.class,
        AppointmentListener.class,
        TestConfig.class,
})
class DogRepositoryFindAllByAppointmentIsNullTests {

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    void findAllByAppointmentIsNull_returnsOnlyDogsWithoutAppointment() {
        // given: one dog without appointment
        Dog dogWithoutAppointment = new Dog();
        dogWithoutAppointment.setName("No Appointment");
        dogWithoutAppointment.setDescription("Should be returned");
        dogWithoutAppointment = dogRepository.save(dogWithoutAppointment);

        // and: one dog with an appointment
        Dog dogWithAppointment = new Dog();
        dogWithAppointment.setName("With Appointment");
        dogWithAppointment.setDescription("Should NOT be returned");
        dogWithAppointment = dogRepository.save(dogWithAppointment);

        Appointment appointment = new Appointment();
        appointment.setName("John Doe");
        appointment.setPhone("123456789");
        appointment.setTime(LocalDateTime.now().plusDays(1));
        appointment.setComment("Test appointment");
        appointment.setDog(dogWithAppointment);
        dogWithAppointment.setAppointment(appointment);
        appointmentRepository.save(appointment);

        // when
        List<Dog> dogsWithoutAppointments = dogRepository.findAllByAppointmentIsNull();

        // then: list is not empty and contains only dogs without appointment
        assertThat(dogsWithoutAppointments)
                .isNotEmpty()
                .contains(dogWithoutAppointment)
                .doesNotContain(dogWithAppointment);

        // and: every returned dog really has no associated appointment
        assertThat(dogsWithoutAppointments)
                .allSatisfy(dog -> assertThat(dog.getAppointment()).isNull());

        dogRepository.delete(dogWithoutAppointment);
        dogRepository.delete(dogWithAppointment);
    }
}