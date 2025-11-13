package com.example.adoptions.repository;

import com.example.adoptions.entity.Appointment;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface AppointmentRepository extends ListCrudRepository<Appointment, Integer> {
    Appointment findByPhone(String phone);
}
