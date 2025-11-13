package com.example.adoptions.tool;

import com.example.adoptions.entity.Appointment;
import com.example.adoptions.entity.Dog;
import com.example.adoptions.repository.AppointmentRepository;
import com.example.adoptions.repository.DogRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class DogAdoptionScheduler {
    private final DogRepository dogRepository;
    private final AppointmentRepository appointmentRepository;

    DogAdoptionScheduler(AppointmentRepository appointmentRepository, DogRepository dogRepository) {
        this.appointmentRepository = appointmentRepository;
        this.dogRepository = dogRepository;
    }

    @Tool(description = "Schedule an appointment to pickup or adopt a " +
            "dog from a Pooch Palace location. You need dog's ID, date with time, user's name, user's phone number"
            + " to be able to schedule an appointment")
    String schedule(
            @ToolParam(description = "ID of a dog") Integer dogId,
            @ToolParam(description = "format: ISO-8601 (examples: 2025-11-13T18:00, 2025-01-15T09:00)"
                    +" please, specify time in range from 9am to 9pm."
                    + " It's Pooch Palace's time for meeting visitors. Fix time format by yourself, "
                    + "if provided information is enough to extract date, then accept it,"
                    + "don't show input format") String time,
            @ToolParam(description = "name") String visitorName,
            @ToolParam(description = "phone with country code, format examples: 420771333425, 380962225081."
                    + " Clarify user to input phone with country code. Fix time format by yourself: leave only "
                    + "digits in phone") String phone
    ) {
        LocalDateTime reservationTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);

        dogRepository.findById(dogId).ifPresent(dog -> {
            Appointment appointment = new Appointment();
            appointment.setName(visitorName);
            appointment.setPhone(phone);
            appointment.setTime(reservationTime);
            appointment.setDog(dog);
            appointment = appointmentRepository.save(appointment);
            dog.setAppointment(appointment);
            dogRepository.save(dog);
        });

        return String.format("""
                ✅ I have scheduled your appointment at %s, phone is %s, thank you!
                """, reservationTime, phone);
    }

    @Tool(description = "Show appointment data ONLY when user asks")
    String showAppointment(@ToolParam(description = "Phone number with country code,"
            + " which user used fo appointment. Format examples: 420771333425, 380962225081."
            + " Clarify user to input phone with country code. Fix time format by yourself: leave only "
            + "digits in phone") String phone) {
        Appointment appointment = appointmentRepository.findByPhone(phone);

        if (appointment == null) {
            return String.format("Sorry, no appointment found with your phone %s", phone);
        }

        return appointment.toString();
    }

    @Tool(description = "Edit existing appointment. Ask the phone number, which user used for appointment, to find it."
            + " User is able to update any of these fields: "
            + "time, name, description, or select another dog. Or just see his appointment and do not change anything")
    String updateAppointment(
            @ToolParam(description = " phone number with country code, which user used fo appointment. Format examples:"
                    + "420771333425, 380962225081."
                    + " Clarify user to input phone with country code. Fix time format by yourself: leave only "
                    + "digits in phone", required = false) String oldPhone,
            @ToolParam(description = "New name", required = false) String name,
            @ToolParam(description = "New phone number with country code, format examples: 420771333425, 380962225081."
                    + " Clarify user to input phone with country code. Fix time format by yourself: leave only "
                    + "digits in phone", required = false) String phone,
            @ToolParam(description = "New appointment time. Format: ISO-8601 "
                    +"(examples: 2025-11-13T18:00, 2025-01-15T09:00), please, specify time in range"
                    + " from 9am to 9pm. It's Pooch Palace's time for meeting visitors. Fix time format by yourself, "
                    + "don't ask user to do this", required = false) String time,
            @ToolParam(description = "ID of a dog. Say that you are ready to tell about dogs you have", required = false)
            Integer dogId
    ) {
        Appointment appointment = appointmentRepository.findByPhone(oldPhone);
        if (appointment == null) {
            return String.format("Sorry, no appointment found with your phone %s", oldPhone);
        }
        if (time != null) {
            LocalDateTime reservationTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
            appointment.setTime(reservationTime);
        }
        if (phone != null) {
            appointment.setPhone(phone);
        }
        if (name != null) {
            appointment.setName(name);
        }

        if (dogId > 0) {
            Optional<Dog> dog = dogRepository.findById(dogId);
            if (dog.isPresent()) {
                appointment.setDog(dog.get());
            } else {
                return String.format("Sorry, no dog found with ID %s", dogId);
            }
        }

        appointmentRepository.save(appointment);

        return "Successfully updated your appointment";
    }

    @Tool(description = "Cancel or remove an appointment. You need a phone number to find necessary appointment")
    String deleteAppointment(@ToolParam(description = "Phone number with country code,"
            + " which user used fo appointment. Format examples: 420771333425, 380962225081."
            + " Clarify user to input phone with country code. Fix time format by yourself: leave only "
            + "digits in phone") String phone) {
        Appointment appointment = appointmentRepository.findByPhone(phone);
        if (appointment == null) {
            return String.format("Sorry, no appointment found with your phone %s", phone);
        }
        Dog dog = appointment.getDog();
        dog.setAppointment(null);
        dogRepository.save(dog);

        return "You appointment canceled";
    }
}
